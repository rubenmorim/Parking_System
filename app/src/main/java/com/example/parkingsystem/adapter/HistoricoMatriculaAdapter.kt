package com.example.parkingsystem.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.matricula.MatriculaEndpoint
import com.example.parkingsystem.fragment.HistoricoMatricula
import com.example.parkingsystem.model.Matricula
import kotlinx.android.synthetic.main.recyclerline.view.*
import kotlinx.android.synthetic.main.recyclerlinematricula.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    //private val id: TextView = itemView.findViewById(R.id.idCar)

    fun bind(matricula: Matricula) {
        nomeCarro.text = matricula.nomeCarro
        matriculaCarro.text = matricula.matricula
        val id = matricula.id



        //delete car button
        val button2: CardView = itemView.findViewById(R.id.buttonVehicle)
        button2.setOnClickListener{

            val builder: AlertDialog.Builder = AlertDialog.Builder(it.context)
            builder.setTitle("Delete Vehicle")

            val layout = LinearLayout(it.context)
            layout.orientation = LinearLayout.VERTICAL

            builder.setMessage("Are you sure you want to delete this vehicle?")

            builder.setView(layout)

                // Set up the buttons
                .setPositiveButton("Yes", DialogInterface.OnClickListener() { dialog, which ->
                    // Here you get get input text from the Edittext
                    val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)
                    val callDel = request.delMatricula(id)

                    callDel.enqueue(object: Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.isSuccessful) {
                                val c: String = response.body()!!
                                Log.d("TAG", "Sucesso!" + c)
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.d("TAG", "Fail onFailure!" + matricula.id + t.stackTrace)
                        }
                    })

                })
            builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }
    }

}