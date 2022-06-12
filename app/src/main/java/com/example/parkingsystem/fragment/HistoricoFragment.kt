package com.example.parkingsystem.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.adapter.HistoricoAdapter
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.historico.HistoricoEndpoint
import com.example.parkingsystem.model.Historico
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoricoFragment(idUser: Long) : Fragment() {

    private var idUtilizador: Long = idUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_historico, container, false)


        val request = ServiceBuilder.buildService(HistoricoEndpoint::class.java)
        val call = request.getHistoricoByUser(idUtilizador)
        val recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView)

        //Get user history
        call.enqueue(object : Callback<List<Historico>> {
            override fun onResponse(call: Call<List<Historico>>, response: Response<List<Historico>>) {
                if (response.isSuccessful){
                    recyclerView.apply {
                        //Change HistoricoAdapter
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = HistoricoAdapter(response.body()!!)
                    }
                }
            }
            override fun onFailure(call: Call<List<Historico>>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        return v
    }


}