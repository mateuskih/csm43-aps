package br.edu.utfpr.aps

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_jogo.*
import kotlinx.android.synthetic.main.fragment_menu.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MenuFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MenuFragment : Fragment() {

    lateinit var prefs: SharedPreferences
    lateinit var nome: String
    lateinit var email: String
    lateinit var senha: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        nome = prefs.getString("nome", "")!!
        email = prefs.getString("email", "")!!
        senha = prefs.getString("senha", "")!!

        txtUserName.text = "email:"+email + " nome: "+nome + " senha: "+senha

        btProxJogo.setOnClickListener {
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().putString("condicaoJogo", "web").apply()
            val nav = Navigation.findNavController(this@MenuFragment.activity!!, R.id.fragmentContent)
            nav.navigate(R.id.menuToJogo)
        }

        btResponderLocal.setOnClickListener {
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().putString("condicaoJogo", "local").apply()
            val nav = Navigation.findNavController(this@MenuFragment.activity!!, R.id.fragmentContent)
            nav.navigate(R.id.menuToJogo)
        }

        btLogout.setOnClickListener {
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().clear().apply()
            val nav = Navigation.findNavController(this@MenuFragment.activity!!, R.id.fragmentContent)
            nav.navigate(R.id.menuToLogin)
        }

        btRanking.setOnClickListener {
            val nav = Navigation.findNavController(this@MenuFragment.activity!!, R.id.fragmentContent)
            nav.navigate(R.id.menuToRanking)
        }

        btSettings.setOnClickListener {
            val nav = Navigation.findNavController(this@MenuFragment.activity!!, R.id.fragmentContent)
            nav.navigate(R.id.menuToSetting)
        }

        btAdmin.setOnClickListener {
            val nav = Navigation.findNavController(this@MenuFragment.activity!!, R.id.fragmentContent);
            nav.navigate(R.id.menuToAdmin);
        }

    }


}
