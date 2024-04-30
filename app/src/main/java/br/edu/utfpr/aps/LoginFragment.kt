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
import br.edu.utfpr.aps.entidades.LoginResponse
import br.edu.utfpr.aps.services.UsuarioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {
    lateinit var service: UsuarioService
    lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            if (prefs.contains("email") && prefs.contains(("senha"))){
                iniciarJogo()
            }
            else{
                configureRetrofit()
            }

        btLogin.setOnClickListener {
            val email = txtEmail.text.toString()
            val senha = txtSenha.text.toString()
            login(email, senha)

        }

        btRegistrar.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.loginToRegister)
        )

    }

    private fun configureRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://tads2019-todo-list.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(UsuarioService::class.java)

    }

    private fun login(email: String, senha: String) {
        service.logar(email, senha).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("ERRO", t.message, t)
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val resposta = response.body()!!
                if (resposta != null)
                    Toast.makeText(activity, resposta.mensagem,Toast.LENGTH_SHORT).show()
                    prefs = PreferenceManager.getDefaultSharedPreferences(activity)
                    prefs.edit().putString("email", email).apply()
                    prefs.edit().putString("senha", senha).apply()
                    prefs.edit().putString("pontos", resposta.pontuacao.toString()).apply()

                if (resposta.sucesso){
                    iniciarJogo()
                }
            }
        })
    }

    private fun iniciarJogo(){
        val nav = Navigation.findNavController(this@LoginFragment.activity!!, R.id.fragmentContent)
        nav.navigate(R.id.loginToSetting)
    }

}
