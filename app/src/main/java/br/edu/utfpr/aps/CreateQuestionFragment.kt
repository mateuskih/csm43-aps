package br.edu.utfpr.aps

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.TypeConverters
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.CategoriaDao
import br.edu.utfpr.aps.bd.dao.DificuldadeDao
import br.edu.utfpr.aps.bd.dao.PerguntaDao
import br.edu.utfpr.aps.bd.dao.UsuarioDao
import br.edu.utfpr.aps.entidades.*
import br.edu.utfpr.aps.services.JogoService
import br.edu.utfpr.aps.ui.CategoriaAdapter
import br.edu.utfpr.aps.ui.CategoriaListListener
import br.edu.utfpr.aps.ui.DificuldadeAdapter
import br.edu.utfpr.aps.ui.DificuldadeListListener
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.fragment_create_question.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CreateQuestionFragment : Fragment(), CategoriaListListener, DificuldadeListListener {
    lateinit var serviceJogo: JogoService
    lateinit var adapterCategoria: CategoriaAdapter
    lateinit var adapterDificuldade: DificuldadeAdapter
    lateinit var prefs: SharedPreferences
    lateinit var dificuldade: String
    var categ: Int = 0
    lateinit var categoriaNome: String
    lateinit var dificuldadeNome: String

    private val categoriaDao: CategoriaDao by lazy { DatabaseClient.getCategoriaDao(requireContext()) }
    private val dificuldadeDao: DificuldadeDao by lazy { DatabaseClient.getDificuldadeDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_question, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buscaCategoria()

        btCadastrar.setOnClickListener {
            val perguntasDao = DatabaseClient.getPerguntaDao(requireContext())
            val questionTitle = txtQuestionTitle.text.toString()
            val correctAnswer = txtCorrectAnswer.text.toString()
            val type = "multiple"
            val answer1 = txtAnswer1.text.toString()
            val answer2 = txtAnswer2.text.toString()
            val answer3 = txtAnswer3.text.toString()
            dificuldade = txtSelectedDificuldade.text.toString()
            categoriaNome = txtSelected.text.toString()
            val answersList = listOf(answer1, answer2, answer3)

            val validator = Validator(requireContext())

            val inputs = listOf(
                questionTitle to "Titulo não pode ser vazio.",
                correctAnswer to "Resposta correta não pode ser vazia.",
                answer1 to "Altenativa 1 não pode ser vazia.",
                answer2 to "Altenativa 2 não pode ser vazia.",
                answer3 to "Altenativa 3 não pode ser vazia."
            )

            if (validator.validate(inputs)) {
                val newQuestion: Question = Question(categoriaNome, type, dificuldade, questionTitle, correctAnswer, answersList)
                val response = perguntasDao.inserir(newQuestion)

                if(response != null){
                    val mensagemPulo = "Questão criada com sucesso!"
                    Toast.makeText(activity, mensagemPulo, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun buscaCategoria() {
        val categorias = categoriaDao.buscarCategorias();
        val dificuldades = dificuldadeDao.buscarDificuldades();
        configuraRecyclerView(categorias, dificuldades)
    }

    fun configuraRecyclerView(categorias: List<Categoria>, dificuldades: List<Dificuldade>) {
        adapterCategoria = CategoriaAdapter(categorias.toList(), this)
        listCategories.adapter = adapterCategoria
        listCategories.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        adapterDificuldade = DificuldadeAdapter(dificuldades.toList(), this)
        listDificuldades.adapter = adapterDificuldade
        listDificuldades.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
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
}