package com.example.parkingsystem.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.icu.util.UniversalTimeScale.toLong
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.adapter.HistoricoMatriculaAdapter
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.matricula.MatriculaEndpoint
import com.example.parkingsystem.model.Matricula
import com.example.parkingsystem.room.entity.User
import kotlinx.android.synthetic.main.recyclerlinematricula.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.global.Global

class HistoricoMatricula(user: User) : Fragment() {

    var m_Text: String =                                ""
    var m_Text2: String =                               ""
    private var utilizador: User = user

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment)
        fragmentTransaction.commit()
    }

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
        val call = request.getMatriculaUtilizador(utilizador.id)
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

        val button: ImageButton = v.findViewById(R.id.addButton)
        button.setOnClickListener{
            showdialog(v)
        }

        return v
    }

    fun showdialog(view: View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Vehicle")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val input = EditText(requireContext())
        input.setHint("Car Name")
        layout.addView(input)

        val input2 = EditText(requireContext())
        input2.setHint("License Plate")
        layout.addView(input2)

        builder.setView(layout)

            // Set up the buttons
            .setPositiveButton("Add", DialogInterface.OnClickListener() { dialog, which ->
                // Here you get get input text from the Edittext
                m_Text = input.text.toString()
                m_Text2 = input2.text.toString()
                addMatricula()
            })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    //POST Request to Add Vehicle
    private fun addMatricula(){

        val jsonObject = JSONObject()
        jsonObject.put("idUtilizador", utilizador.id)
        jsonObject.put("nomeCarro", m_Text)
        jsonObject.put("matricula", m_Text2)

        val jsonObjectString = jsonObject.toString()

        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)
        val call = request.addMatricula(requestBody)

        call.enqueue(object : Callback<Matricula>{
            override fun onResponse(call: Call<Matricula>, response: Response<Matricula>) {
                Log.d("TAG", utilizador.id.toString())
                val c: Matricula = response.body()!!
                Toast.makeText(requireActivity(), utilizador.id.toString() + "-" + c.nomeCarro, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Matricula>, t: Throwable) {
                Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }


}
//teste