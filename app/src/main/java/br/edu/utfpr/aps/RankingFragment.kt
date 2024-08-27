package br.edu.utfpr.aps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.entidades.RankingResponse
import br.edu.utfpr.aps.entidades.Usuario
import br.edu.utfpr.aps.services.UsuarioService
import br.edu.utfpr.aps.ui.RankingAdapter
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_ranking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RankingFragment : Fragment() {
    lateinit var service: UsuarioService
    lateinit var adapter: RankingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buscaRanking()
    }

    fun configuraRecyclerView(usuarios: List<Usuario>) {
        adapter = RankingAdapter(usuarios.toList())
        listRanking.adapter = adapter

        listRanking.layoutManager = LinearLayoutManager(
            activity, RecyclerView.VERTICAL, false)
    }

    private fun buscaRanking() {
        val usuarioDao = DatabaseClient.getUsuarioDao(requireContext())
        var response = usuarioDao.buscarUsuarios();
        println("aaaaaaaaa"+ response)
        val usuariosOrdenados = response.sortedByDescending { it.pontuacao }
        configuraRecyclerView(usuariosOrdenados)
    }
}
