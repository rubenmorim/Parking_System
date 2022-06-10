package com.example.parkingsystem.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import kotlin.math.log


class ReserveFragment(idUser: Long, idParque: Int, titulo: String) : Fragment() {

    private var idUtilizador: Long = idUser
    private var idPark: Int = idParque
    private var title: String = titulo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = title
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_home, container, false)

        val backButton: ImageView = v.findViewById(R.id.backImageView)
        backButton.setOnClickListener {
            (activity as MainActivity).findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(R.string.home)

        }


        return v
    }

    companion object {

    }
}