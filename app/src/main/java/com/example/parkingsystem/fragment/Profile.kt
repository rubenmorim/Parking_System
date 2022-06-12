package com.example.parkingsystem.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.room.entity.User


class Profile(user: User) : Fragment() {

    private lateinit var historicoFragment: HistoricoFragment
    private lateinit var historicoMatricula: HistoricoMatricula
    private lateinit var futureReservationsFragment: FutureReservationsFragment
    private lateinit var walletFragment: WalletFragment

    private var utilizador: User = user

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_profile, container, false)


        var nomeuser: TextView = v.findViewById(R.id.nomeUser)
        nomeuser.setText(utilizador.firstName + " " + utilizador.lastName)

        var email: TextView = v.findViewById(R.id.email)
        email.setText(utilizador.email)


        //Reservas agendadas
        val buttonReserva: Button = v.findViewById(R.id.buttonReservas)
        buttonReserva.setOnClickListener{
            futureReservationsFragment = FutureReservationsFragment(utilizador.id)
            setFragment(futureReservationsFragment)
        }


        //Matriculas
        val button1: Button = v.findViewById(R.id.button4)
        button1.setOnClickListener{
            historicoMatricula = HistoricoMatricula(utilizador.id)
            setFragment(historicoMatricula)
        }

        //Historico
        val button2: Button = v.findViewById(R.id.button)
        button2.setOnClickListener{
            historicoFragment = HistoricoFragment(utilizador.id)
            setFragment(historicoFragment)
        }

        //Wallet
        val button3: Button = v.findViewById(R.id.button3)
        button3.setOnClickListener{
            walletFragment = WalletFragment(utilizador.id)
            setFragment(walletFragment)
        }

        //Logout
        val button4: Button = v.findViewById(R.id.logout)
        button4.setOnClickListener{
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("LOGOUT", true)
            }
            startActivity(intent)
        }

        return v
    }

}
