package br.edu.utfpr.aps

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.CategoriaDao
import br.edu.utfpr.aps.bd.dao.DificuldadeDao
import br.edu.utfpr.aps.bd.dao.PerguntaDao
import br.edu.utfpr.aps.entidades.*
import br.edu.utfpr.aps.ui.*
import kotlinx.android.synthetic.main.fragment_edit_question.*
import kotlinx.android.synthetic.main.fragment_edit_question.btCadastrar
import kotlinx.android.synthetic.main.fragment_edit_question.listCategories
import kotlinx.android.synthetic.main.fragment_edit_question.listDificuldades
import kotlinx.android.synthetic.main.fragment_edit_question.txtAnswer1
import kotlinx.android.synthetic.main.fragment_edit_question.txtAnswer2
import kotlinx.android.synthetic.main.fragment_edit_question.txtAnswer3
import kotlinx.android.synthetic.main.fragment_edit_question.txtCorrectAnswer
import kotlinx.android.synthetic.main.fragment_edit_question.txtQuestionTitle
import kotlinx.android.synthetic.main.fragment_edit_question.txtSelected
import kotlinx.android.synthetic.main.fragment_edit_question.txtSelectedDificuldade


class EditQuestionFragment : Fragment(), CategoriaListListener, DificuldadeListListener, QuestaoListListener {
    lateinit var adapterCategoria: CategoriaAdapter
    lateinit var adapterDificuldade: DificuldadeAdapter
    lateinit var adapterQuestao: QuestaoAdapter
    lateinit var prefs: SharedPreferences
    lateinit var dificuldade: String
    var categ: Int = 0
    var questionId: Int = 0
    lateinit var categoriaNome: String
    lateinit var dificuldadeNome: String


    private val perguntasDao: PerguntaDao by lazy { DatabaseClient.getPerguntaDao(requireContext()) }
    private val categoriaDao: CategoriaDao by lazy { DatabaseClient.getCategoriaDao(requireContext()) }
    private val perguntaDao: PerguntaDao by lazy { DatabaseClient.getPerguntaDao(requireContext()) }
    private val dificuldadeDao: DificuldadeDao by lazy { DatabaseClient.getDificuldadeDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_question, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buscaCategoria()

        btCadastrar.setOnClickListener {
            val questionTitle = txtQuestionTitle.text.toString()
            val correctAnswer = txtCorrectAnswer.text.toString()
            val type = "multiple"
            val answer1 = txtAnswer1.text.toString()
            val answer2 = txtAnswer2.text.toString()
            val answer3 = txtAnswer3.text.toString()
            dificuldade = txtSelectedDificuldade.text.toString()
            categoriaNome = txtSelected.text.toString()
            val answersList = listOf(answer1, answer2, answer3)

            val incorrectAnswersString = convertListToString(answersList)
            val response = perguntasDao.alterarPergunta(questionId, categoriaNome, type, dificuldade, questionTitle, correctAnswer, incorrectAnswersString)

            if(response == 1){
                val mensagemPulo = "Questão criada com sucesso!"
                Toast.makeText(activity, mensagemPulo, Toast.LENGTH_SHORT).show()
                buscaCategoria()
            }
        }

    }
    private fun buscaCategoria() {
        val categorias = categoriaDao.buscarCategorias();
        val dificuldades = dificuldadeDao.buscarDificuldades();
        val questoes = perguntaDao.buscaTodas();
        configuraRecyclerView(categorias, dificuldades, questoes)
    }

    fun configuraRecyclerView(categorias: List<Categoria>, dificuldades: List<Dificuldade>, questoes: List<Question>) {
        adapterCategoria = CategoriaAdapter(categorias.toList(), this)
        listCategories.adapter = adapterCategoria
        listCategories.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        adapterDificuldade = DificuldadeAdapter(dificuldades.toList(), this)
        listDificuldades.adapter = adapterDificuldade
        listDificuldades.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        adapterQuestao = QuestaoAdapter(questoes.toList(), this)
        listQuestions.adapter = adapterQuestao
        listQuestions.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }

    override fun getCategoria(categoria: Categoria) {
        txtSelected.text = categoria.name
        categoriaNome = categoria.name
        categ = categoria.id.toInt()
    }

    override fun getDificuldade(dificuldade: Dificuldade) {
        txtSelectedDificuldade.text = dificuldade.name
        dificuldadeNome = dificuldade.name
    }

    override fun getQuestao(question: Question) {
        questionId = question.id.toInt()
        txtSelectedDificuldade.text = question.difficulty
        dificuldadeNome = question.difficulty

        txtSelected.text = question.category
        categoriaNome = question.category
        txtSelected.text = question.category

        txtQuestionTitle.setText(question.question)

        txtCorrectAnswer.setText(question.correctAnswer)
        txtAnswer1.setText(question.incorrectAnswers[0])
        txtAnswer2.setText(question.incorrectAnswers[1])
        txtAnswer3.setText(question.incorrectAnswers[2])
    }

    fun convertListToString(list: List<String>): String {
        return list.joinToString(separator = ",")
    }
}