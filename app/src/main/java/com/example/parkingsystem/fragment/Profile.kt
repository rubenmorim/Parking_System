package com.example.parkingsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.example.parkingsystem.R


class Profile : Fragment() {

    private lateinit var historicoFragment: HistoricoFragment
    private lateinit var historicoMatricula: HistoricoMatricula

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

        //Matriculas
        val button1: Button = v.findViewById(R.id.button4)
        button1.setOnClickListener{
            historicoMatricula = HistoricoMatricula()
            setFragment(historicoMatricula)
        }

        //Historico
        val button2: Button = v.findViewById(R.id.button)
        button2.setOnClickListener{
            historicoFragment = HistoricoFragment()
            setFragment(historicoFragment)
        }

        //Wallet
        val button3: Button = v.findViewById(R.id.button3)
        button3.setOnClickListener{

        }

        //Logout
        val button4: Button = v.findViewById(R.id.logout)
        button4.setOnClickListener{

        }

        return v
    }

}
