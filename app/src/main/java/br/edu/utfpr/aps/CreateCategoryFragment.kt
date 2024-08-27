package br.edu.utfpr.aps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.CategoriaDao
import br.edu.utfpr.aps.entidades.Categoria
import br.edu.utfpr.aps.entidades.Question
import br.edu.utfpr.aps.entidades.Validator
import kotlinx.android.synthetic.main.fragment_create_category.*
import kotlinx.android.synthetic.main.fragment_create_question.*

class CreateCategoryFragment : Fragment() {

    private val categoriaDao: CategoriaDao by lazy { DatabaseClient.getCategoriaDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_category, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSalvar.setOnClickListener {
            val categoryTitle = editTextTextMultiLine.text.toString()
            val validarCategoria = categoriaDao.buscarCategoriaByName(categoryTitle)
            val validator = Validator(requireContext())

            val inputs = listOf(
                categoryTitle to "Titulo não pode ser vazio."
            )

            val additionalChecks = listOf(
                { validarCategoria == null }
            )

            val errorMessages = listOf(
                "Categoria já cadastrada com esse nome."
            )

            if (validator.validate(inputs, additionalChecks, errorMessages)) {
                val newCategoria: Categoria = Categoria(categoryTitle)
                categoriaDao.inserir(newCategoria)

                val mensagemPulo = "Categoria criada com sucesso!"
                Toast.makeText(activity, mensagemPulo, Toast.LENGTH_SHORT).show()
            }
        }
    }
}