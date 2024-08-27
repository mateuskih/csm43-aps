package br.edu.utfpr.aps

import android.app.AlertDialog
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
import br.edu.utfpr.aps.entidades.Categoria
import br.edu.utfpr.aps.entidades.Dificuldade
import br.edu.utfpr.aps.entidades.Validator
import br.edu.utfpr.aps.ui.CategoriaAdapter
import br.edu.utfpr.aps.ui.CategoriaListListener
import br.edu.utfpr.aps.ui.DificuldadeAdapter
import kotlinx.android.synthetic.main.fragment_create_category.*
import kotlinx.android.synthetic.main.fragment_create_category.btnSalvar
import kotlinx.android.synthetic.main.fragment_create_question.*
import kotlinx.android.synthetic.main.fragment_editar_category.*

class EditCategoryFragment : Fragment(), CategoriaListListener {

    lateinit var adapterCategoria: CategoriaAdapter
    var categ: Int = 0
    lateinit var categoriaNome: String

    private val categoriaDao: CategoriaDao by lazy { DatabaseClient.getCategoriaDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editar_category, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buscaCategoria()

        btnSalvar.setOnClickListener {
            val categoryTitle = txtSelectedElement.text.toString()
            val validator = Validator(requireContext())

            val inputs = listOf(
                categoryTitle to "Titulo não pode ser vazio."
            )

            if (validator.validate(inputs)) {
                val response = categoriaDao.alterarCategoria(categ, categoryTitle)

                if(response == 1){
                    val mensagemPulo = "Categoria alterada com sucesso!"
                    Toast.makeText(activity, mensagemPulo, Toast.LENGTH_SHORT).show()
                    buscaCategoria()
                }
            }
        }

        btnDeletar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmação")
                .setMessage("Tem certeza que deseja deletar esta categoria?")
                .setPositiveButton("Sim") { dialog, which ->
                    val categoriaDelete: Categoria = categoriaDao.buscarCategoria(categ)
                    val response = categoriaDao.apagar(categoriaDelete)

                    if(response == 1) {
                        val mensagemPulo = "Categoria deletada com sucesso!"
                        Toast.makeText(activity, mensagemPulo, Toast.LENGTH_SHORT).show()
                        buscaCategoria()
                    }
                }
                .setNegativeButton("Não") { dialog, which ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun buscaCategoria() {
        val categorias = categoriaDao.buscarCategorias();
        configuraRecyclerView(categorias)
    }

    fun configuraRecyclerView(categorias: List<Categoria>) {
        adapterCategoria = CategoriaAdapter(categorias.toList(), this)
        listElement.adapter = adapterCategoria
        listElement.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }

    override fun getCategoria(categoria: Categoria) {
        txtSelectedElement.setText(categoria.name)
        categoriaNome = categoria.name
        categ = categoria.id.toInt()
    }
}