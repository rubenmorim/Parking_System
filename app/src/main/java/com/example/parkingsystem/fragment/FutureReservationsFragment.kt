package com.example.parkingsystem.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.adapter.HistoricoAdapter
import com.example.parkingsystem.adapter.ReservasAdapter
import com.example.parkingsystem.api.Reserva.ReservaEndPoints
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.historico.HistoricoEndpoint
import com.example.parkingsystem.model.Historico
import com.example.parkingsystem.model.Reservas
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FutureReservationsFragment(idUser: Long) : Fragment() {

    private var idUtilizador: Long = idUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity).findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(R.string.futureReservations)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_future_reservations, container, false)

        val request = ServiceBuilder.buildService(ReservaEndPoints::class.java)
        val call = request.getReservas(idUtilizador)
        val recyclerView = v.findViewById<RecyclerView>(R.id.recyclerViewReservations)

        //Get user history
        call.enqueue(object : Callback<List<Reservas>> {
            override fun onResponse(call: Call<List<Reservas>>, response: Response<List<Reservas>>) {
                if (response.isSuccessful){
                    recyclerView.apply {
                        setHasFixedSize(true)

                        v.findViewById<TextView>(R.id.title).text = if (response.body()!!.isEmpty()) getString(R.string.naoExisteReservas) else ""

                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = ReservasAdapter(response.body()!!)
                    }
                }
            }
            override fun onFailure(call: Call<List<Reservas>>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        return v

    }

    companion object {

    }
}