package br.edu.utfpr.aps

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import br.edu.utfpr.aps.entidades.RegistroResponse
import br.edu.utfpr.aps.services.UsuarioService
import br.edu.utfpr.aps.services.JogoService
import br.edu.utfpr.aps.ui.RankingAdapter
import kotlinx.android.synthetic.main.fragment_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RegisterFragment : Fragment() {
    lateinit var retrofit: Retrofit
    lateinit var retrofitJogo: Retrofit
    lateinit var service: UsuarioService
    lateinit var serviceJogo: JogoService
    lateinit var adapter: RankingAdapter
    lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureRetrofit()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btCadastrar.setOnClickListener {
            val nome = txtRegNome.text.toString()
            val email = txtRegEmail.text.toString()
            val senha = txtRegSenha.text.toString()
            val senha2 = txtRegSenha2.text.toString()

            if (senha.equals(senha2)){
                registrar(nome, email, senha)
            }
            else{
                Toast.makeText(activity, R.string.register_confirm_password, Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun registrar(nome: String, email: String, senha: String) {
        service.registrar(nome, email, senha).enqueue(object : Callback<RegistroResponse> {
            override fun onFailure(call: Call<RegistroResponse>, t: Throwable) {
                Log.e("ERRO", t.message, t)
            }

            override fun onResponse(call: Call<RegistroResponse>, response: Response<RegistroResponse>) {
                val resposta = response.body()
                if (resposta != null)
                if (resposta!!.sucesso)
                    Toast.makeText(activity, R.string.register_confirm,Toast.LENGTH_SHORT).show()
                    prefs = PreferenceManager.getDefaultSharedPreferences(activity)
                    prefs.edit().clear().apply()
                    prefs.edit().putString("email", email).apply()
                    prefs.edit().putString("senha", senha).apply()
                    val nav = Navigation.findNavController(this@RegisterFragment.activity!!, R.id.fragmentContent)
                    nav.navigate(R.id.registerToLogin)
            }
        })
    }

    private fun configureRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tads2019-todo-list.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(UsuarioService::class.java)

        val retrofitJogo = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        serviceJogo = retrofitJogo.create(JogoService::class.java)
    }


}
