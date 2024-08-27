package br.edu.utfpr.aps


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.CategoriaDao
import br.edu.utfpr.aps.entidades.Categoria
import br.edu.utfpr.aps.entidades.Categorias
import br.edu.utfpr.aps.services.JogoService
import br.edu.utfpr.aps.ui.CategoriaAdapter
import br.edu.utfpr.aps.ui.CategoriaListListener
import kotlinx.android.synthetic.main.fragment_setting.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class SettingFragment : Fragment(), CategoriaListListener {

    lateinit var serviceJogo: JogoService
    lateinit var adapterCategoria: CategoriaAdapter
    lateinit var prefs: SharedPreferences
    lateinit var dificuldade: String
    var categ: Int = 0
    lateinit var categoriaName: String

    private val categoriaDao: CategoriaDao by lazy { DatabaseClient.getCategoriaDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureRetrofit()
        buscaCategoria()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSettingJogar.setOnClickListener {
            dificuldade = radioButtonCkeck()
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().putString("dificuldade", dificuldade).apply()
            prefs.edit().putString("categoria", categ.toString()).apply()
            prefs.edit().putString("categoriaName", categoriaName).apply()
            prefs.edit().putString("condicaoJogo", "web").apply()

            val nav = Navigation.findNavController(this@SettingFragment.activity!!, R.id.fragmentContent)
            nav.navigate(R.id.settingToJogo)
        }

        btnSettingAleatorio.setOnClickListener {
            var random = Random()
            var numeroRandom = random.nextInt((32 - 9) + 1) + 9
            if (numeroRandom == 0){
                numeroRandom =  random.nextInt((32 - 9) + 1) + 9
            }
            val dificuldadeRandom = listOf("easy", "medium", "hard").shuffled()

            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().putString("dificuldade", dificuldadeRandom[0]).apply()
            prefs.edit().putString("categoria", numeroRandom.toString() ).apply()
            prefs.edit().putString("condicaoJogo", "web").apply()
            val nav = Navigation.findNavController(this@SettingFragment.activity!!, R.id.fragmentContent)
            nav.navigate(R.id.settingToJogo)

        }
    }

    private fun configureRetrofit() {
        val retrofitJogo = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        serviceJogo = retrofitJogo.create(JogoService::class.java)
    }

    private fun buscaCategoria() {
        serviceJogo.getCategoria().enqueue(object : Callback<Categorias> {
            override fun onFailure(call: Call<Categorias>, t: Throwable) {
                Log.e("ERRO", t.message, t)
            }

            override fun onResponse(call: Call<Categorias>, response: Response<Categorias>) {
                val resposta = response.body()
                if (resposta != null){
                    val categorias = categoriaDao.buscarCategorias();
                    configuraRecyclerView(resposta.categoria + categorias)
                }
            }
        })
    }

    private fun radioButtonCkeck(): String {
        return if (rbFacil.isChecked){
            "easy"
        }else if(rbMedio.isChecked){
            "medium"
        } else{
            "hard"
        }
    }

    fun configuraRecyclerView(categorias: List<Categoria>) {
        adapterCategoria = CategoriaAdapter(categorias.toList(), this)
        listCategories.adapter = adapterCategoria

        listCategories.layoutManager = LinearLayoutManager(
            activity, RecyclerView.VERTICAL, false)
    }

    override fun getCategoria(categoria: Categoria) {
        txtSelectCategoria.text = categoria.name
        categ = categoria.id.toInt()
        categoriaName = categoria.name
    }
}
