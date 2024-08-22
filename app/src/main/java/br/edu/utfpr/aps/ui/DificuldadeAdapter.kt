package br.edu.utfpr.aps.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.utfpr.aps.R
import br.edu.utfpr.aps.entidades.Categoria
import br.edu.utfpr.aps.entidades.Dificuldade
import kotlinx.android.synthetic.main.activity_item_categoria.view.*
import kotlinx.android.synthetic.main.activity_item_dificuldade.view.*

class DificuldadeAdapter(var dificuldades: List<Dificuldade>, private var listener: DificuldadeListListener) :
    RecyclerView.Adapter<DificuldadeAdapter.DificuldadeViewHolder>() {

    override fun getItemCount() = dificuldades.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DificuldadeViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.activity_item_dificuldade, parent, false)
        )

    override fun onBindViewHolder(holder: DificuldadeViewHolder, position: Int) {
        holder.preencherView(dificuldades[position])
    }

    inner class DificuldadeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun preencherView(dificuldade: Dificuldade) {
            itemView.txtDificuldade.text = dificuldade.name

            itemView.txtDificuldade.setOnClickListener {
                listener.getDificuldade(dificuldade)
            }
        }
    }
}