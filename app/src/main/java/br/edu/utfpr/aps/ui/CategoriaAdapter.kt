package br.edu.utfpr.aps.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.utfpr.aps.R
import br.edu.utfpr.aps.entidades.Categoria
import kotlinx.android.synthetic.main.activity_item_categoria.view.*

class CategoriaAdapter(var categorias: List<Categoria>, private var listener: CategoriaListListener) :
    RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    override fun getItemCount() = categorias.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoriaViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.activity_item_categoria, parent, false)
        )

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.preencherView(categorias[position])
    }

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun preencherView(categoria: Categoria) {
            itemView.txtCategoria.text = categoria.name

            itemView.txtCategoria.setOnClickListener {
                listener.getCategoria(categoria)
            }
        }
    }
}