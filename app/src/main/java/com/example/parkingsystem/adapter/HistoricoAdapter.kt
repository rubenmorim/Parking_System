package com.example.parkingsystem.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.model.Historico
import kotlinx.android.synthetic.main.recyclerline.view.*
import java.text.SimpleDateFormat
import java.util.*


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
    private val dataEntrada:TextView = itemView.findViewById(R.id.tvDataEntrada)
    private val dataSaida:TextView = itemView.findViewById(R.id.tvDataSaida)
    private val priceText:TextView = itemView.findViewById(R.id.priceText)
    private val timeText:TextView = itemView.findViewById(R.id.timeText)


    fun bind(historico: Historico) {
        name.text = historico.nomeParque
        dataEntrada.text = historico.entrada
        dataSaida.text = historico.saida
        priceText.text = historico.preco.toString() + "€"
        timeText.text =  historico.tempo.toString() + "m"
    }

}