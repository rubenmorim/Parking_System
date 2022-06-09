package com.example.parkingsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.adapter.HistoricoMatriculaAdapter
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.matricula.MatriculaEndpoint
import com.example.parkingsystem.model.Matricula
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoricoMatricula : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_historico_matricula, container, false)

        val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)
        val call = request.getMatriculaUtilizador(1)
        val recyclerView = v.findViewById<RecyclerView>(R.id.recyclerViewCarros)

        call.enqueue(object : Callback<List<Matricula>> {
            override fun onResponse(call: Call<List<Matricula>>, response: Response<List<Matricula>>) {
                if (response.isSuccessful){
                    recyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = HistoricoMatriculaAdapter(response.body()!!)
                    }
                }
            }
            override fun onFailure(call: Call<List<Matricula>>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        return v
    }
}