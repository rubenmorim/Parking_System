package com.example.parkingsystem.fragment

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.parkingsystem.R
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.matricula.MatriculaEndpoint
import com.example.parkingsystem.api.parque.ParqueEndpoint
import com.example.parkingsystem.model.Matricula
import com.example.parkingsystem.model.Parque
import com.example.parkingsystem.model.Utilizador
import com.example.parkingsystem.model.post.UtilizadorPost
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class QrCodeFragment : Fragment() {

    private lateinit var ivQRCode: ImageView
    private lateinit var buttonChangeCurrentVehicle: Button
    private var idUtilizador: Long = 1654173834566

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val v: View = inflater.inflate(R.layout.fragment_qr_code, container, false)

        val button: Button = v.findViewById(R.id.buttonChangeCurrentVehicle)
        button.setOnClickListener {
            this.changeLicencePlate(v);
        }
        getMatriculaUtilizador(idUtilizador)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ivQRCode = view.findViewById(R.id.qrCodeImageView)

        // Call http service for default car
        // ivQRCode.setImageBitmap(setQrCode("Os tomates do padre inácio!"))

        requireView().findViewById<TextView>(R.id.textViewLicencePlate).setText(R.string.no_vehicles_configured_yet)
        requireView().findViewById<ImageView>(R.id.qrCodeImageView)
            .setImageResource(R.drawable.ic_car_not_found)
        requireView().findViewById<TextView>(R.id.textViewInfo).visibility = View.VISIBLE
        // Hide the buttons
    }


    private fun setQrCode(licencePlate: String): Bitmap? {
        val data = licencePlate.trim()
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for(x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if(bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            return bmp
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }

    fun changeLicencePlate(view: View) {
        val checkedItem = intArrayOf(-1)

        val alertDialog = AlertDialog.Builder(view.context)
        alertDialog.setTitle("Choose an Item")

        val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)
        //val call = request.getMatriculaUtilizador()
        //val listItems = arrayOf("Ford Fiesta", "Fiat Multipla", "Mother Russia's Lada")


        //call.enqueue(object : Callback<List<Matricula>> {
            //override fun onResponse(call: Call<List<Matricula>>, response: Response<List<Matricula>>) {

                //val listItem = arrayOf(response.body()!!)
                //val matriculasList = response.body()!!
                //val listItem = mutableListOf<Matricula>()

                //for (matriculas in matriculasList) {
                    //listItem.add(matriculas)

                //}
            //}
            //override fun onFailure(call: Call<List<Matricula>>, t: Throwable) {
                //Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            //}
        //})

        val listItems = arrayOf("Ford Fiesta", "Fiat Multipla", "Mother Russia's Lada")

        alertDialog.setSingleChoiceItems(
            listItems, checkedItem[0]
        ) { dialog, which -> // update the selected item which is selected by the user

            //colocar matricula como ativa

            // Set the licence plate
            requireView().findViewById<TextView>(R.id.textViewLicencePlate).text = listItems[which]
            requireView().findViewById<ImageView>(R.id.qrCodeImageView)
                .setImageBitmap(setQrCode(listItems[which]))
            requireView().findViewById<TextView>(R.id.textViewInfo).visibility = View.GONE
            // when selected an item the dialog should be closed with the dismiss method
            dialog.dismiss()
        }
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which -> }

        // create and build the AlertDialog instance
        // with the AlertDialog builder instance
        val customAlertDialog = alertDialog.create()

        // show the alert dialog when the button is clicked
        customAlertDialog.show()
    }

    private fun getMatriculaUtilizador(idUtilizador: Long) {
        val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)

        val jsonObject = JSONObject()
        jsonObject.put("idUtilizador", idUtilizador)

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        val utilizador = UtilizadorPost(idUtilizador)

        request.getMatriculaUtilizador(utilizador).enqueue(object : Callback<List<Matricula>> {
            override fun onResponse(call: Call<List<Matricula>>, response: Response<List<Matricula>>) {
                if (response.isSuccessful){
                    val matriculaList: List<Matricula> = response.body()!!

                    for (matricula in matriculaList) {
                        Toast.makeText(requireContext(), matricula.nomeCarro + " - " + matricula.matricula, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(requireContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Matricula>>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}