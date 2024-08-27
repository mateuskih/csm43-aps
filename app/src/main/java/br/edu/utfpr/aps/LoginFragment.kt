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
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.PerguntaDao
import br.edu.utfpr.aps.bd.dao.UsuarioDao
import br.edu.utfpr.aps.entidades.Validator
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {
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

        btLogin.setOnClickListener {
            val email = txtEmail.text.toString()
            val senha = txtSenha.text.toString()
            val validator = Validator(requireContext())

            val inputs = listOf(
                email to "Email não pode ser vazio.",
                senha to "Senha não pode ser vazia."
            )

            if (validator.validate(inputs)) {
                login(email, senha)
            }
        }

        btRegistrar.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.loginToRegister)
        )

    }


    private fun login(email: String, senha: String) {
        val usuarioDao = DatabaseClient.getUsuarioDao(requireContext())
        val response = usuarioDao.login(email, senha);

        if(response != null){
            Toast.makeText(activity, "Sucesso",Toast.LENGTH_SHORT).show()
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().putString("email", response.email).apply()
            prefs.edit().putString("senha", response.senha).apply()
            prefs.edit().putString("nome", response.nome).apply()
            prefs.edit().putString("partidas", response.partidasJogadas.toString()).apply()
            prefs.edit().putString("pontos", response.pontuacao.toString()).apply()

            iniciarJogo();
        }
        else{
            Toast.makeText(activity, "email ou senha incorretos",Toast.LENGTH_SHORT).show()
        }

    }

    private fun iniciarJogo(){
        val nav = Navigation.findNavController(this@LoginFragment.activity!!, R.id.fragmentContent)
        nav.navigate(R.id.loginToMenu)
    }

}
