package br.edu.utfpr.aps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.CategoriaDao
import br.edu.utfpr.aps.bd.dao.DificuldadeDao
import br.edu.utfpr.aps.entidades.Categoria
import br.edu.utfpr.aps.entidades.Dificuldade
import br.edu.utfpr.aps.entidades.Question
import br.edu.utfpr.aps.entidades.Validator
import kotlinx.android.synthetic.main.fragment_create_category.*
import kotlinx.android.synthetic.main.fragment_create_question.*

class CreateDifficultyFragment : Fragment() {

    private val dificuldadeDao: DificuldadeDao by lazy { DatabaseClient.getDificuldadeDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_difficulty, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSalvar.setOnClickListener {
            val difficultyTitle = editTextTextMultiLine.text.toString()
            val validator = Validator(requireContext())

            val inputs = listOf(
                difficultyTitle to "Titulo não pode ser vazio."
            )

            if (validator.validate(inputs)) {
                val newDifficulty: Dificuldade = Dificuldade(difficultyTitle)
                dificuldadeDao.inserir(newDifficulty)

                val mensagemPulo = "Dificuldade criada com sucesso!"
                Toast.makeText(activity, mensagemPulo, Toast.LENGTH_SHORT).show()
            }
        }
    }
}