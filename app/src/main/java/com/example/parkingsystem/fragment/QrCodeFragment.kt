package com.example.parkingsystem.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.example.parkingsystem.model.Matricula
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class QrCodeFragment(idUser: Long) : Fragment() {

    private lateinit var ivQRCode: ImageView
    private lateinit var buttonChangeCurrentVehicle: Button
    private var idUtilizador: Long = idUser
    //private lateinit var parkingDetails: ParkingDetail

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_qr_code, container, false)

        val button: Button = v.findViewById(R.id.buttonChangeCurrentVehicle)
        button.setOnClickListener {
           changeLicencePlate(v)
        }

        Toast.makeText(requireContext(), "IDUSER__: ${idUtilizador}", Toast.LENGTH_LONG).show()

        //val button2: Button = v.findViewById(R.id.buttonPayments)
        //button2.setOnClickListener{
        //    parkingDetails = ParkingDetails()
        //    setFragment(parkingDetails)
        //}

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ivQRCode = view.findViewById(R.id.qrCodeImageView)

        // Set the default as car not found
        checkIfFragmentAttached { setQRCodeState(false, null)  }
        getMatriculaUtilizador(idUtilizador)
    }

    // Get active user enrollment
    private fun getMatriculaUtilizador(idUtilizador: Long) {

        val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)
        val call = request.getMatriculaUtilizador(idUtilizador)

        call.enqueue(object : Callback<List<Matricula>> {
            override fun onResponse(call: Call<List<Matricula>>, response: Response<List<Matricula>>) {

                // If the response is empty, by default the image is "Car not found"
                // set in the onCreateView
                if(response.body()!!.isNotEmpty()) {
                    for (matriculas in response.body()!!) {
                        if(matriculas.isSelected) {
                            setQRCodeState(true, matriculas.matricula)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<Matricula>>, t: Throwable) {
                checkIfFragmentAttached {
                    Toast.makeText(requireContext(), "List of licence plate : ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    //Change QR Code
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

    // Change active vehicle
    private fun changeLicencePlate(view: View) {

        val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)
        val call = request.getMatriculaUtilizador(idUtilizador)

        call.enqueue(object : Callback<List<Matricula>> {
            override fun onResponse(call: Call<List<Matricula>>, response: Response<List<Matricula>>) {

                val listItems = arrayOfNulls<String>(response.body()!!.size)
                var number : Int = -1

                for (matriculas in response.body()!!) {
                    number++
                    listItems[number] = matriculas.matricula
                }
                print(listItems)

                val checkedItem = intArrayOf(-1)

                val alertDialog = AlertDialog.Builder(view.context)
                alertDialog.setTitle("Choose an Item")


                alertDialog.setSingleChoiceItems(
                    listItems, checkedItem[0]
                ) { dialog, which -> // update the selected item which is selected by the user

                    //put active vehicle
                    val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)
                    val call = request.updateMatricula(idUtilizador, listItems[which].toString())

                    call.enqueue(object : Callback<List<Matricula>> {
                        override fun onResponse(call: Call<List<Matricula>>, response: Response<List<Matricula>>) {
                        }
                        override fun onFailure(call: Call<List<Matricula>>, t: Throwable) {
                            checkIfFragmentAttached {
                                checkIfFragmentAttached { Toast.makeText(requireContext(), "Update licence plate: ${t.message}", Toast.LENGTH_SHORT)
                                    .show()
                                }
                            }
                        }
                    })

                    // Set the licence plate
                    setQRCodeState(true, listItems[which])
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
            override fun onFailure(call: Call<List<Matricula>>, t: Throwable) {
                checkIfFragmentAttached {
                    Toast.makeText(requireContext(), "Change licence plate (update) : ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
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
     * Function to change the layout (display QR Code instead of default "Not found", etc) of the fragment
     * based on the fact if a car is selected. state == true means a car is selecred
     */
    private fun setQRCodeState(state: Boolean, licencePlate: String?) {
        if(state) {
            requireView().findViewById<TextView>(R.id.textViewLicencePlate).text = licencePlate
            requireView().findViewById<ImageView>(R.id.qrCodeImageView)
                .setImageBitmap(setQrCode(licencePlate!!))
            requireView().findViewById<TextView>(R.id.textViewInfo).visibility = View.GONE
        } else {
            requireView().findViewById<TextView>(R.id.textViewLicencePlate).setText(R.string.no_vehicles_configured_yet)
            requireView().findViewById<ImageView>(R.id.qrCodeImageView)
                .setImageResource(R.drawable.ic_car_not_found)
            requireView().findViewById<TextView>(R.id.textViewInfo).visibility = View.VISIBLE
        }
    }
}