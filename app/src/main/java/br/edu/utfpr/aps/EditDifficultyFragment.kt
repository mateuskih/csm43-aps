package br.edu.utfpr.aps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.DificuldadeDao
import br.edu.utfpr.aps.entidades.Categoria
import br.edu.utfpr.aps.entidades.Dificuldade
import br.edu.utfpr.aps.ui.CategoriaAdapter
import br.edu.utfpr.aps.ui.DificuldadeAdapter
import br.edu.utfpr.aps.ui.DificuldadeListListener
import kotlinx.android.synthetic.main.fragment_create_category.*
import kotlinx.android.synthetic.main.fragment_create_category.btnSalvar
import kotlinx.android.synthetic.main.fragment_create_question.*
import kotlinx.android.synthetic.main.fragment_editar_category.*

class EditDifficultyFragment : Fragment(), DificuldadeListListener {
    lateinit var adapterDificuldade: DificuldadeAdapter
    var dificuldadeId: Int = 0
    lateinit var dificuldadeNome: String

    private val dificuldadeDao: DificuldadeDao by lazy { DatabaseClient.getDificuldadeDao(requireContext()) }

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
            val difficultyTitle = txtSelectedElement.text.toString()

            val response = dificuldadeDao.alterarDificuldade(dificuldadeId, difficultyTitle)

            if(response == 1){
                val mensagemPulo = "Dificuldade alterada com sucesso!"
                Toast.makeText(activity, mensagemPulo, Toast.LENGTH_SHORT).show()
                buscaCategoria()
            }
        }

        btnDeletar.setOnClickListener {
            val dificuldadeDelete: Dificuldade = dificuldadeDao.buscarDificuldade(dificuldadeNome)
            val response = dificuldadeDao.apagar(dificuldadeDelete)

            if(response == 1) {
                val mensagemPulo = "Dificuldade deletada com sucesso!"
                Toast.makeText(activity, mensagemPulo, Toast.LENGTH_SHORT).show()
                buscaCategoria()
            }
        }
    }

    fun configuraRecyclerView(dificuldades: List<Dificuldade>) {
        adapterDificuldade = DificuldadeAdapter(dificuldades.toList(), this)
        listElement.adapter = adapterDificuldade
        listElement.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
    }

    override fun getDificuldade(dificuldade: Dificuldade) {
        txtSelectedElement.setText(dificuldade.name)
        dificuldadeNome = dificuldade.name
        dificuldadeId = dificuldade.id.toInt()
    }

    private fun buscaCategoria() {
        val dificuldades = dificuldadeDao.buscarDificuldades();
        println("rambo "+dificuldades)
        configuraRecyclerView(dificuldades)
    }
}