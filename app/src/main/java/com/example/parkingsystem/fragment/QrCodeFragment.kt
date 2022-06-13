package com.example.parkingsystem.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.parkingsystem.CaptureQRCodeActivity
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.matricula.MatriculaEndpoint
import com.example.parkingsystem.global.Global
import com.example.parkingsystem.model.Matricula
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class QrCodeFragment(idUser: Long) : Fragment() {

    private lateinit var ivQRCode:                      ImageView
    private lateinit var buttonChangeCurrentVehicle:    Button
    private var idUtilizador: Long =                    idUser
    private var globalLicencePlate: String =            ""
    private lateinit var parkFragment:                  ParkFragment
    var m_Text: String =                                ""
    var m_Text2: String =                               ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_qr_code, container, false)

        // Initialize park fragment
        parkFragment = ParkFragment()

        val buttonScanQRCode = view.findViewById<Button>(R.id.buttonScanQRCode)
        buttonScanQRCode.setOnClickListener {
            lerQrCode()
        }

        val buttonChangeCurrentVehicle = view.findViewById<Button>(R.id.buttonChangeCurrentVehicle)
        buttonChangeCurrentVehicle.setOnClickListener {
            changeLicencePlate(view)
        }

        val qrCodeImageView = view.findViewById<ImageView>(R.id.qrCodeImageView)
        qrCodeImageView.setOnClickListener {
            redirectToPark(view, "1")
        }

        ivQRCode = view.findViewById(R.id.qrCodeImageView)

        // Set the default as car not found
        setQRCodeState(view, false, null)
        getMatriculaUtilizador(view, idUtilizador)

        val button2: Button = view.findViewById(R.id.buttonAddVehicle)
        button2.setOnClickListener{
            showdialog(view)
        }

        val button3: Button = view.findViewById(R.id.buttonPayments)
        button3.setOnClickListener{
            //showdialogDel(v)
        }

        return view
    }



    // Get active user enrollment
    //private fun getMatriculaUtilizador(idUtilizador: Long) {
    /**
     * Fetch all licence plates/vehicles associated with the user
     */
    private fun getMatriculaUtilizador(view: View, idUtilizador: Long) {

        val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)
        val call = request.getMatriculaUtilizador(idUtilizador)

        call.enqueue(object : Callback<List<Matricula>> {
            override fun onResponse(call: Call<List<Matricula>>, response: Response<List<Matricula>>) {

                // If the response is empty, by default the image is "Car not found"
                // set in the onCreateView, therefore we don't need to set it here again
                if(response.body()!!.isNotEmpty()) {
                    for (matriculas in response.body()!!) {
                        if(matriculas.isSelected) {
                            checkIfFragmentAttached { setQRCodeState(view,true, matriculas.matricula) }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<Matricula>>, t: Throwable) {
                checkIfFragmentAttached {
                    Toast.makeText(requireContext(), "There was an error fetching the vehicles: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * Function to generate a QR Code as Bitmap based on a given string
     */
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

    /**
     * Function to set up the dialog menu so the user can choose the licence plate/vehicle to use
     */
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
                    checkIfFragmentAttached { setQRCodeState(view, true, listItems[which]) }
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
    private fun setQRCodeState(view: View, state: Boolean, licencePlate: String?) {
        if (state) {
            if (licencePlate != null) {
                globalLicencePlate = licencePlate
            }
            view.findViewById<TextView>(R.id.textViewLicencePlate).text = licencePlate
            view.findViewById<ImageView>(R.id.qrCodeImageView)
                .setImageBitmap(setQrCode(licencePlate!!))
            view.findViewById<TextView>(R.id.textViewInfo).visibility = View.GONE
        } else {
            globalLicencePlate = ""
            view.findViewById<TextView>(R.id.textViewLicencePlate)
                .setText(R.string.no_vehicles_configured_yet)
            view.findViewById<ImageView>(R.id.qrCodeImageView)
                .setImageResource(R.drawable.ic_car_not_found)
            view.findViewById<TextView>(R.id.textViewInfo).visibility = View.VISIBLE
        }
    }

    /**
     * Function to redirect to park fragment, where the user can confirm the park and start the process of parking
     */
    private fun redirectToPark(view: View, parkID: String) {
        if(globalLicencePlate == "") {
            Toast.makeText(requireContext(), "É necessário selecionar um veículo", Toast.LENGTH_SHORT ).show()
        } else {
            (activity as MainActivity).setFragment(
                parkFragment,
                mapOf(
                    Global.PARAM_PARK_ID to parkID,
                    Global.PARAM_PARK_LICENCE_PLATE to globalLicencePlate
                )
            )

            // Change title of main activity
            (activity as MainActivity).findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(
                R.string.parking)
        }
    }

    /**
     * Function to trigger the scan of the QR Code
     */
    private fun lerQrCode() {
        val options = ScanOptions()
        options.captureActivity = CaptureQRCodeActivity::class.java
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan something")
        options.setOrientationLocked(false)
        options.setBeepEnabled(false)
        barcodeLauncher.launch(options)
    }

    /**
     *  Register the launcher and result handler
     */
    private val barcodeLauncher = registerForActivityResult(ScanContract()) {
            result: ScanIntentResult ->
        if (result.contents == null) {
            checkIfFragmentAttached { Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()  }

        } else {
            checkIfFragmentAttached {

                redirectToPark(requireView(),  result.contents)
                Toast.makeText(
                    requireContext(),
                    result.contents,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //Dialog Box to Add Vehicle
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
        jsonObject.put("idUtilizador", idUtilizador)
        jsonObject.put("nomeCarro", m_Text)
        jsonObject.put("matricula", m_Text2)

        val jsonObjectString = jsonObject.toString()

        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        val request = ServiceBuilder.buildService(MatriculaEndpoint::class.java)
        val call = request.addMatricula(requestBody)

        call.enqueue(object : Callback<Matricula>{
            override fun onResponse(call: Call<Matricula>, response: Response<Matricula>) {
                val c: Matricula = response.body()!!
                Toast.makeText(requireActivity(), c.idUtilizador.toString() + "-" + c.nomeCarro, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Matricula>, t: Throwable) {
                Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
