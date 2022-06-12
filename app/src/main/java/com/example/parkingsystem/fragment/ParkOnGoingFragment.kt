package com.example.parkingsystem.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.estacionamento.EstacionamentoEndpoint
import com.example.parkingsystem.global.Global
import com.example.parkingsystem.model.Estacionamento
import com.example.parkingsystem.model.EstacionamentoAtual
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ParkOnGoingFragment : Fragment() {

    private var estacionamentoAtual: EstacionamentoAtual = EstacionamentoAtual()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_park_on_going, container, false)

        val userID: Long = requireArguments().get(Global.PARAM_USER_ID).toString().toLong()
        val licencePlate: String = requireArguments().get(Global.PARAM_PARK_LICENCE_PLATE).toString()

        view.findViewById<Button>(R.id.fragmentParkButtonStopPark).setOnClickListener {
            stopPark(userID)
        }

        view.findViewById<Button>(R.id.fragmentParkButtonRenewPark).setOnClickListener {
            renewPark(userID, 30)
        }

        // Execute http request to get data from the park
        val requestParkOnGoing = ServiceBuilder.buildService(EstacionamentoEndpoint::class.java)
        val callParkOnGoing = requestParkOnGoing.getEstacionamentoAtual(userID)
        callParkOnGoing.enqueue(object : Callback<EstacionamentoAtual> {
            override fun onResponse(call: Call<EstacionamentoAtual>, response: Response<EstacionamentoAtual>) {
                if (response.isSuccessful) {
                    estacionamentoAtual = response.body()!!

                    view.findViewById<TextView>(R.id.fragmentParkName).text =
                        estacionamentoAtual.parque?.get(0)?.nomeParque ?: "Erro"
                    view.findViewById<TextView>(R.id.fragmentParkAddress).text =
                        estacionamentoAtual.parque?.get(0)?.morada ?: "Erro"
                    view.findViewById<TextView>(R.id.fragmentParkStartTime).text =
                        estacionamentoAtual.estacionamentoAtual?.entrada ?: "Erro"
                    view.findViewById<TextView>(R.id.fragmentParkEndTime).text =
                        estacionamentoAtual.estacionamentoAtual?.saida ?: "Indefinido"
                    view.findViewById<TextView>(R.id.fragmentParkCar).text =
                        licencePlate
                }
            }
            override fun onFailure(call: Call<EstacionamentoAtual>, t: Throwable) {
                checkIfFragmentAttached { Toast.makeText(requireContext(), "Não há parques a decorrer", Toast.LENGTH_LONG).show() }
            }
        })
        return view
    }


    /**
     * Function to wrap some bit of code that needs the context of the fragment NOT to be null
     */
    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

    /**
     * Function to wrap some bit of code that needs the context of the fragment NOT to be null
     */
    private fun stopPark(userID: Long) {
        // Execute http request to get data from the park
        val requestParkOnGoing = ServiceBuilder.buildService(EstacionamentoEndpoint::class.java)
        val callParkOnGoing = requestParkOnGoing.getConcluirParquimetro(userID)
        callParkOnGoing.enqueue(object : Callback<Estacionamento> {
            override fun onResponse(call: Call<Estacionamento>, response: Response<Estacionamento>) {
                if (response.isSuccessful) {
                    // Ir para a página activity main
                    checkIfFragmentAttached {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            override fun onFailure(call: Call<Estacionamento>, t: Throwable) {
                checkIfFragmentAttached { Toast.makeText(requireContext(), "Não há parques a decorrer", Toast.LENGTH_LONG).show() }
            }
        })
    }

    /**
     * Function to wrap some bit of code that needs the context of the fragment NOT to be null
     */
    private fun renewPark(userID: Long, extraTime: Long) {
        // Execute http request to get data from the park
        val request = ServiceBuilder.buildService(EstacionamentoEndpoint::class.java)
        val call = request.getRenovarParquimetro(userID, extraTime)
        call.enqueue(object : Callback<Estacionamento> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<Estacionamento>, response: Response<Estacionamento>) {
                if (response.isSuccessful) {
                    checkIfFragmentAttached { Toast.makeText(requireContext(), "The park was renewed for 30 minutes", Toast.LENGTH_LONG).show() }

                    val exitDateISO = estacionamentoAtual.estacionamentoAtual?.saida
                    val formatter: DateTimeFormatter =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    var endTime = LocalDateTime.parse(exitDateISO, formatter)
                    endTime = endTime.plusMinutes(extraTime)

                    val exitDate = endTime.toString().replace("T", " ")
                    requireView().findViewById<TextView>(R.id.fragmentParkEndTime).text = exitDate
                }
            }
            override fun onFailure(call: Call<Estacionamento>, t: Throwable) {
                checkIfFragmentAttached { Toast.makeText(requireContext(), "Não há parques a decorrer", Toast.LENGTH_LONG).show() }
            }
        })
    }
}