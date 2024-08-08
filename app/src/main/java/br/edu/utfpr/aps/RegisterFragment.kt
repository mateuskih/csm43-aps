package br.edu.utfpr.aps

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import br.edu.utfpr.aps.bd.AppDatabase
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.UsuarioDao
import br.edu.utfpr.aps.entidades.Usuario
import br.edu.utfpr.aps.services.UsuarioService
import br.edu.utfpr.aps.services.JogoService
import br.edu.utfpr.aps.ui.RankingAdapter
import kotlinx.android.synthetic.main.fragment_register.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RegisterFragment : Fragment() {
    lateinit var serviceJogo: JogoService
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
        val usuarioDao = DatabaseClient.getUsuarioDao(requireContext())
        var usuario: Usuario = Usuario(nome, email, senha, 0, 0, null, false);

        var response = usuarioDao.inserir(usuario);

        if(response != null){
            Toast.makeText(activity, R.string.register_confirm,Toast.LENGTH_SHORT).show()
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().clear().apply()
            prefs.edit().putString("email", email).apply()
            prefs.edit().putString("senha", senha).apply()

            val nav = Navigation.findNavController(this@RegisterFragment.activity!!, R.id.fragmentContent)
            nav.navigate(R.id.registerToLogin)
        }
        else{
            Toast.makeText(activity, R.string.register_fail,Toast.LENGTH_SHORT).show()
        }

    }
}
