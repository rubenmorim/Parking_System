package com.example.parkingsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.model.Reservas

class ReservasAdapter(private val reservas: List<Reservas>): RecyclerView.Adapter<ReservasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recyclerlinereservas,
            parent, false)
        return ReservasViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reservas.size
    }

    override fun onBindViewHolder(holder: ReservasViewHolder, position: Int) {
        return holder.bind(reservas[position])
    }
}

class ReservasViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
    private val name: TextView = itemView.findViewById(R.id.tvNomeReserva)
    private val dataEntrada: TextView = itemView.findViewById(R.id.tvDataEntradaReserva)
    private val dataSaida: TextView = itemView.findViewById(R.id.tvDataSaidaReserva)

    fun bind(reserva: Reservas) {
        name.text = reserva.parque.nomeParque
        dataEntrada.text = reserva.reserva.entrada
        dataSaida.text = reserva.reserva.saida
    }
}