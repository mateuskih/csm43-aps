package br.edu.utfpr.aps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.PreferenceManager
import br.edu.utfpr.aps.entidades.QuestionsResponse
import br.edu.utfpr.aps.services.UsuarioService
import br.edu.utfpr.aps.services.JogoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.navigation.Navigation
import br.edu.utfpr.aps.bd.dao.PerguntaDao
import br.edu.utfpr.aps.entidades.Question
import kotlinx.android.synthetic.main.fragment_jogo.*
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.edu.utfpr.aps.app.MainActivity
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.CategoriaDao
import br.edu.utfpr.aps.bd.dao.UsuarioDao
import java.time.LocalDateTime
import java.util.*


class JogoFragment : Fragment() {
    lateinit var serviceJogo: JogoService
    lateinit var prefs: SharedPreferences
    lateinit var questao: String
    lateinit var alternativa1: String
    lateinit var alternativa2: String
    lateinit var alternativa3: String
    lateinit var alternativa4: String
    lateinit var alternativaCorreta: String
    lateinit var email: String
    lateinit var senha: String
    lateinit var pontuacao: String
    lateinit var type: String
    lateinit var countDownTimer: CountDownTimer
    lateinit var dificuldade: String
    lateinit var categoria: String
    lateinit var categoriaName: String
    var pontosA: Int = 0
    var pontosE: Int = 0
    var pontosPulo: Int = 0

    private val perguntasDao: PerguntaDao by lazy { DatabaseClient.getPerguntaDao(requireContext()) }
    private val usuarioDao: UsuarioDao by lazy { DatabaseClient.getUsuarioDao(requireContext()) }
    private val categoriaDao: CategoriaDao by lazy { DatabaseClient.getCategoriaDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_jogo, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureRetrofit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // BLOQUEAR AÇÃO DE VOLTAR TELA
            }
        })

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        email = prefs.getString("email", "")!!
        senha = prefs.getString("senha", "")!!
        pontuacao = prefs.getString("pontos", "")!!
        dificuldade = prefs.getString("dificuldade", "")!!
        categoria = prefs.getString("categoria", "")!!
        categoriaName = prefs.getString("categoriaName", "")!!

        if(categoria.isNullOrEmpty() || dificuldade.isNullOrEmpty()){
            categoria = "10"
            dificuldade = "medium"
        }

        when (dificuldade) {
            "easy" -> {
                pontosA = 5
                pontosE = -5
                pontosPulo = -2
            }
            "medium" -> {
                pontosA = 8
                pontosE = -8
                pontosPulo = -4
            }
            else -> {
                pontosA = 10
                pontosE = -10
                pontosPulo = -6
            }
        }

        var condJogo = prefs.getString("condicaoJogo", "")
        if (condJogo == "web"){
            gerarPerguntas(categoria.toInt(), dificuldade)
        }
        else {
            gerarPerguntasLocais(dificuldade)
        }

        txtJogoEmail.text = email
        txtJogoPontos.text = pontuacao

        var botoes = listOf(btJogoA1, btJogoA2, btJogoA3, btJogoA4)

        botoes.forEach { botao ->
            botao.setOnClickListener {
                conferirResposta(botao.text.toString(), alternativaCorreta, botao)
                desativarOutrosBotoes(botoes, botao)
            }
        }

        btDica.setOnClickListener {
            btDica.isClickable = false
            botoes = gerarDica(botoes, alternativaCorreta)
            pontosA -= 3
            pontosE -= 3
            pontosPulo -= 3
        }

        btMaisTarde.setOnClickListener {
            pontuar(email, senha, pontosPulo)

            val alternativas = if (type == "boolean") {
                listOf(alternativa1)
            } else {
                listOf(alternativa1, alternativa2, alternativa3)
            }

            val pergunta = Question(categoria, type, dificuldade, questao, alternativaCorreta, alternativas)
            perguntasDao.inserir(pergunta)

            val mensagemPulo = getString(R.string.jogo_question_skiped) + pontosPulo + getString(R.string.jogo_points)
            Toast.makeText(activity, mensagemPulo, Toast.LENGTH_SHORT).show()

            pontuar(email, senha, pontosPulo)
            countDownTimer.cancel()

            val navController = Navigation.findNavController(requireActivity(), R.id.fragmentContent)
            navController.navigate(R.id.jogoToMenu)
        }
    }

    private fun gerarPerguntas(category: Int, dificuldade: String) {
        val categorias = categoriaDao.buscarCategorias();
        val filteredCategorias = categorias.filter { it.name == categoriaName }

        if (filteredCategorias.isNotEmpty()){
            println("categoriaName "+categoriaName)
            println("categorias "+categorias)
            println("filtered "+filteredCategorias)
            val question = perguntasDao.buscaPerguntaPorCategoria(categoriaName)
            type = question.type
            montarQuestion(question, dificuldade)
            countDownTimer.start()
        }
        else{
            serviceJogo.getPerguntas(1, category, dificuldade).enqueue(object : Callback<QuestionsResponse> {
            override fun onFailure(call: Call<QuestionsResponse>, t: Throwable) {
                Log.e("ERRO", t.message, t)
            }
            override fun onResponse(call: Call<QuestionsResponse>, response: Response<QuestionsResponse>) {
                val resposta = response.body()!!.results[0]
                type = resposta.type
                montarQuestion(resposta, dificuldade)
                println("ralo" + resposta)
                countDownTimer.start()
            }
            })
        }
    }

    private fun configureRetrofit() {
        val retrofitJogo = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        serviceJogo = retrofitJogo.create(JogoService::class.java)
    }


    private fun pontuar(email: String, senha: String, pontos: Int) {
        val ultimaPartida = Date()
        val response = usuarioDao.pontuar(pontos, ultimaPartida, email, senha);
        if(response == 1){
            pontuacao = usuarioDao.getPontuacao(email, senha).toString()
            Toast.makeText(activity, pontos.toString(), Toast.LENGTH_SHORT).show()
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().putString("pontos", pontuacao).apply()
        }
    }

    private fun montarTimer(difficulty: String){
        var tempo: Long

        if (difficulty == "easy"){
            tempo = 45000
        }
        else if (difficulty == "medium"){
            tempo = 30000
        }
        else{
            tempo = 15000
        }

        countDownTimer = object : CountDownTimer(tempo, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                txtJogoTimer.text = ""+millisUntilFinished / 1000
            }
            override fun onFinish() {
                txtJogoTimer.setText("FIM DO TEMPO")
                Toast.makeText(activity, getString(R.string.jogo_end_time) + pontosE + getString(R.string.jogo_points), Toast.LENGTH_SHORT).show()
                pontuar(email, senha, pontosE)
                val nav = Navigation.findNavController(this@JogoFragment.activity!!, R.id.fragmentContent)
                nav.navigate(R.id.jogoToMenu)
            }
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "teste"
            val descriptionText = "teste"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("teste", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notification(){
        createNotificationChannel()
        val intent = Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(activity, 0, intent, 0)

        val builder = NotificationCompat.Builder(activity as MainActivity, "teste")
            .setSmallIcon(R.drawable.notify_panel_notification_icon_bg)
            .setContentTitle("Quizap")
            .setContentText("Vamos jogar novamente XDDDDDDDD!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(activity as MainActivity)) {
            notify(1, builder.build())
        }

    }
    private fun gerarPerguntasLocais(dificuldade: String){
        btMaisTarde.visibility = View.INVISIBLE
        var pergunta = perguntasDao.buscaTodas().shuffled()
        if (pergunta.isEmpty()) {
            Toast.makeText(activity, getString(R.string.jogo_saved_questions), Toast.LENGTH_SHORT).show()
            val nav = Navigation.findNavController(this@JogoFragment.activity!!, R.id.fragmentContent)
            nav.navigate(R.id.jogoToMenu)
        } else {
            when (dificuldade) {
                "easy" -> {
                    pontosA = 4
                    pontosE = -5
                }
                "medium" -> {
                    pontosA = 6
                    pontosE = -8
                }
                else -> {
                    pontosA = 8
                    pontosE = -10
                }
            }
            type = pergunta[0].type
            montarQuestion(pergunta[0], dificuldade)
            perguntasDao.apagar(pergunta[0])
        }
    }

    private fun conferirResposta(respostaBotao: String, respostaCorreta: String, button: Button){
        button.isClickable = false
        btMaisTarde.isClickable = false
        if (respostaBotao.equals(respostaCorreta)){
            Toast.makeText(activity, getString(R.string.jogo_correct_answer) + "+" +pontosA + getString(R.string.jogo_points), Toast.LENGTH_SHORT).show()
            button.setBackgroundResource(R.drawable.button_correct)
            pontuar(email, senha, pontosA)
            countDownTimer.cancel()
        }
        else{
            Toast.makeText(activity, getString(R.string.jogo_incorrect_answer) + pontosE + getString(R.string.jogo_points), Toast.LENGTH_SHORT).show()
            pontuar(email, senha, pontosE)
            button.setBackgroundResource(R.drawable.button_incorrect)
            countDownTimer.cancel()

        }
        Handler(Looper.getMainLooper()).postDelayed({
            val nav = Navigation.findNavController(this@JogoFragment.requireActivity(), R.id.fragmentContent)
            nav.navigate(R.id.jogoToMenu)
        }, 5000)
    }

    fun desativarOutrosBotoes(botoes: List<Button>, botaoClicado: Button) {
        for (botao in botoes) {
            botao.isEnabled = botao == botaoClicado
        }
    }

    private fun gerarDica(botoes: List<Button>, respostaCorreta: String): List<Button> {
        val botoesNaoCorretos = botoes.filter { it.text != respostaCorreta }

        val quantidadeParaDesabilitar = (1..minOf(3, botoesNaoCorretos.size)).random()

        val botoesParaDesabilitar = botoesNaoCorretos.shuffled().take(quantidadeParaDesabilitar)

        for (botao in botoesParaDesabilitar) {
            botao.isEnabled = false
            botao.setBackgroundResource(R.color.incorrectAnswer)
        }

        return botoes
    }

    private fun montarQuestion(resposta: Question, dificuldade: String){
        txtJogoSettings.text = "${dificuldade.toUpperCase()} - ${resposta.category}"

        if (resposta.type == "boolean"){
            questao = resposta.question
            alternativa1 = resposta.incorrectAnswers[0]
            alternativa2 = resposta.correctAnswer
            alternativaCorreta = resposta.correctAnswer

            val list = listOf(alternativa1, alternativa2).shuffled()
            txtJogoQuestion.text = questao
            btJogoA1.text = list[0]
            btJogoA3.text = list[1]
            btJogoA2.visibility = View.INVISIBLE
            btJogoA4.visibility = View.INVISIBLE
            montarTimer(dificuldade)
            countDownTimer.start()
        }
        else{
            questao = resposta.question
            alternativa1 = resposta.incorrectAnswers[0]
            alternativa2 = resposta.incorrectAnswers[1]
            alternativa3 = resposta.incorrectAnswers[2]
            alternativa4 = resposta.correctAnswer
            alternativaCorreta = resposta.correctAnswer

            val list = listOf(alternativa1, alternativa2, alternativa3, alternativa4).shuffled()
            txtJogoQuestion.text = questao
            btJogoA1.text = list[0]
            btJogoA2.text = list[1]
            btJogoA3.text = list[2]
            btJogoA4.text = list[3]
            montarTimer(dificuldade)
        }
    }
}