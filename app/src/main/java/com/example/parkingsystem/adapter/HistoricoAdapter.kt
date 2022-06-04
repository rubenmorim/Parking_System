package com.example.parkingsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.model.Historico
import kotlinx.android.synthetic.main.recyclerline.view.*


class HistoricoAdapter(val historico: List<Historico>): RecyclerView.Adapter<HistoricoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recyclerline,
            parent, false)
        return HistoricoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historico.size
    }

    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        return holder.bind(historico[position])
    }
}

class HistoricoViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
    private val name: TextView = itemView.findViewById(R.id.tvNome)
    private val data:TextView = itemView.findViewById(R.id.tvData)
    private val tempo:TextView = itemView.findViewById(R.id.tvTempo)
    private val preco:TextView = itemView.findViewById(R.id.tvPreco)

    fun bind(historico: Historico) {
        name.text = historico.nomeParque
        data.text = historico.data
        tempo.text = historico.tempoUtilizacao.toString()
        preco.text = historico.preco.toString()
    }

}