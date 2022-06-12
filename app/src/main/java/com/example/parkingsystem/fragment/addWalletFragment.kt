package com.example.parkingsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.wallet.WalletEndpoints
import com.example.parkingsystem.model.Message
import com.example.parkingsystem.model.post.WalletRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddWalletFragment(idUser: Long) : Fragment() {

    private var idUtilizador: Long = idUser
    private lateinit var walletFragment: WalletFragment

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment)
        fragmentTransaction.commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(R.string.addpayment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_create_wallet, container, false)



        val buttonReserva: ImageView = v.findViewById(R.id.backImageViewWallet)
        buttonReserva.setOnClickListener{
            walletFragment = WalletFragment(idUtilizador)
            setFragment(walletFragment)
        }

        val insert: Button = v.findViewById(R.id.Insert)
        insert.setOnClickListener{
            val createText = v.findViewById<EditText>(R.id.textCard);
            insertMatricula(createText.text.toString())
        }


        return v
    }


    fun insertMatricula(textToInsert:String){
        val request = ServiceBuilder.buildService(WalletEndpoints::class.java)


        if(textToInsert == ""){
            Toast.makeText((activity as MainActivity), "Can't Insert a empty card", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = WalletRequest(idUtilizador,textToInsert)

        val call = request.createPagamento(requestBody)
        //Get user history
        call.enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if (response.isSuccessful){
                    Toast.makeText((activity as MainActivity), getString(R.string.matriculaInserida), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                Toast.makeText((activity as MainActivity), "Ocorreu um erro", Toast.LENGTH_SHORT).show()
            }
        })

    }



}