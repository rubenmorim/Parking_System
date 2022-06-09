package com.example.parkingsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.model.Matricula
import kotlinx.android.synthetic.main.recyclerline.view.*


class HistoricoMatriculaAdapter(val matricula: List<Matricula>): RecyclerView.Adapter<HistoricoMatriculaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoMatriculaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recyclerlinematricula,
            parent, false)
        return HistoricoMatriculaViewHolder(view)
    }

    override fun getItemCount(): Int {
        return matricula.size
    }

    override fun onBindViewHolder(holder: HistoricoMatriculaViewHolder, position: Int) {
        return holder.bind(matricula[position])
    }
}

class HistoricoMatriculaViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
    private val nomeCarro: TextView = itemView.findViewById(R.id.nomeCarro)
    private val matriculaCarro: TextView = itemView.findViewById(R.id.matriculaCarro)

    fun bind(matricula: Matricula) {
        nomeCarro.text = matricula.nomeCarro
        matriculaCarro.text = matricula.matricula
    }

}