package com.example.parkingsystem.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.adapter.HistoricoAdapter
import com.example.parkingsystem.adapter.WalletAdapter
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.wallet.WalletEndpoints
import com.example.parkingsystem.model.Historico
import com.example.parkingsystem.model.Wallet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletFragment(idUser: Long) : Fragment() {

    private var idUtilizador: Long = idUser
    private lateinit var addWallet: AddWalletFragment

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment)
        fragmentTransaction.commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(R.string.wallet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_wallet, container, false)

        val request = ServiceBuilder.buildService(WalletEndpoints::class.java)
        val recyclerView = v.findViewById<RecyclerView>(R.id.recyclerViewWallet)

        val call = request.getTipoPagamento(idUtilizador)
        //Get user history
        call.enqueue(object : Callback<List<Wallet>> {
            override fun onResponse(call: Call<List<Wallet>>, response: Response<List<Wallet>>) {
                if (response.isSuccessful){
                    recyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = WalletAdapter(response.body()!!)
                    }
                }
            }


            override fun onFailure(call: Call<List<Wallet>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

        //listener para o botão, abre um novo startActivity for result ( espera resultado)
        val fab = v.findViewById<FloatingActionButton>(R.id.addWallet)
        fab.setOnClickListener{
            addWallet = AddWalletFragment(idUtilizador)
            setFragment(addWallet)
        }

        return v
    }


}