package com.example.parkingsystem.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.api.Reserva.ReservaEndPoints
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.user.UserEndPoints
import com.example.parkingsystem.model.Res
import com.example.parkingsystem.model.Reserva
import com.example.parkingsystem.model.post.LoginRequest
import com.example.parkingsystem.model.post.ReserveRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log


class ReserveFragment(idUser: Long, idParque: Int, titulo: String) : Fragment() {

    private var idUtilizador: Long = idUser
    private var idPark: Int = idParque
    private var title: String = titulo
    private lateinit var homeFragment: HomeFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = title
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_reserve, container, false)
        homeFragment = HomeFragment(idUtilizador)

        val backButton = v.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            (activity as MainActivity).findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(R.string.home)
            setFragment(homeFragment)
        }


        val submitButton = v.findViewById<Button>(R.id.btnSubmit)
        submitButton.setOnClickListener {
            val date = v.findViewById<DatePicker>(R.id.datePicker)
            val time = v.findViewById<TimePicker>(R.id.timePicker1)
            val day = date.year.toString() + "-" +
                    (date.month + 1).toString()  + "-" +
                    date.dayOfMonth.toString() + " " +
                    time.hour.toString() + ":" +
                    time.minute.toString()

            val request = ServiceBuilder.buildService(ReservaEndPoints::class.java)
            val req = ReserveRequest( idUtilizador = idUtilizador, dataentrada = day, tempoParque = "60", idParque = idPark)
            val call = request.creatReserva(req)

            call.enqueue(object : Callback<Reserva> {
                override fun onResponse(call: Call<Reserva>, response: Response<Reserva>) {
                    if (response.isSuccessful) {

                        val res: Reserva = response.body()!!
                        setFragment(homeFragment)
                        (activity as MainActivity).findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(R.string.home)
                        Toast.makeText((activity as MainActivity), "Reserva inserida!", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Reserva>, t: Throwable) {
                    Toast.makeText((activity as MainActivity), "Ocorreu um erro!", Toast.LENGTH_SHORT).show()
                }
            })

        }


        return v
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment)
        fragmentTransaction.commit()
    }


    companion object {

    }
}