package com.example.parkingsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.parkingsystem.R

class ParkingDetails : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_parking_details, container, false)

       /* val request = ServiceBuilder.buildService(EstacionamentoEndpoint::class.java)
        val call = request.getEstacionamentoByUser(utilizador.id)
        val recyclerView = v.findViewById<RecyclerView>(R.id.recyclerViewParque)

        call.enqueue(object : Callback<List<Estacionamento>> {
            override fun onResponse(call: Call<List<Estacionamento>>, response: Response<List<Estacionamento>>) {
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
        })*/


        /* //trash can button
         val button2: Button = v.findViewById(R.id.imageButton2)
         button2.setOnClickListener{

         }*/

        return v

    }

}