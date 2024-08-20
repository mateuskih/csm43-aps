package br.edu.utfpr.aps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_admin.*

class AdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btCreateQuestion.setOnClickListener {
            val nav = Navigation.findNavController(this@AdminFragment.activity!!, R.id.fragmentContent);
            nav.navigate(R.id.adminToCreateQuestion);
        }

        btEditQuestion.setOnClickListener {
            val nav = Navigation.findNavController(this@AdminFragment.activity!!, R.id.fragmentContent);
            nav.navigate(R.id.adminToEditQuestion);
        }

        btCreateCategory.setOnClickListener {
            val nav = Navigation.findNavController(this@AdminFragment.activity!!, R.id.fragmentContent);
            nav.navigate(R.id.adminToCreateCategory);
        }

        btEditCategory.setOnClickListener {
            val nav = Navigation.findNavController(this@AdminFragment.activity!!, R.id.fragmentContent);
            nav.navigate(R.id.adminToEditCategory);
        }
    }
}