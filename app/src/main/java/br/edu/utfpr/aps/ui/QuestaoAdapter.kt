package br.edu.utfpr.aps.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.utfpr.aps.R
import br.edu.utfpr.aps.entidades.Categoria
import br.edu.utfpr.aps.entidades.Question
import kotlinx.android.synthetic.main.activity_item_categoria.view.*
import kotlinx.android.synthetic.main.activity_item_questao.view.*

class QuestaoAdapter(var questoes: List<Question>, private var listener: QuestaoListListener) :
    RecyclerView.Adapter<QuestaoAdapter.QuestaoViewHolder>() {

    override fun getItemCount() = questoes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuestaoViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.activity_item_questao, parent, false)
        )

    override fun onBindViewHolder(holder: QuestaoViewHolder, position: Int) {
        holder.preencherView(questoes[position])
    }

    inner class QuestaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun preencherView(questao: Question) {
            itemView.txtQuestao.text = questao.question

            itemView.txtQuestao.setOnClickListener {
                listener.getQuestao(questao)
            }
        }
    }
}