package com.example.parkingsystem.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.estacionamento.EstacionamentoEndpoint
import com.example.parkingsystem.api.parque.ParqueEndpoint
import com.example.parkingsystem.api.user.UserEndPoints
import com.example.parkingsystem.global.Global
import com.example.parkingsystem.model.post.Estacionamento
import com.example.parkingsystem.model.Parque
import com.example.parkingsystem.model.User
import com.example.parkingsystem.model.post.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [ParkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ParkFragment : Fragment() , AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var argList: Map<String, String>? = null
    private var isLimited: Boolean = true
    private var limit: Int = 30
    private var licencePlate: String = ""
    private var parkTimeSlot: Long = 0;

    private var estacionamento: Estacionamento = Estacionamento(0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_park, container, false)

        val buttonLimit = view.findViewById<Button>(R.id.fragmentParkButtonLimit)
        val buttonLimitUndefined = view.findViewById<Button>(R.id.fragmentParkButtonLimitUndefined)
        val buttonStartPark = view.findViewById<Button>(R.id.fragmentParkButtonStartPark)

        buttonLimit.setOnClickListener {
            toggleLimit(buttonLimit, buttonLimitUndefined)
        }

        buttonLimitUndefined.setOnClickListener {
            toggleLimit(buttonLimit, buttonLimitUndefined)
        }

        buttonStartPark.setOnClickListener {
            startPark()
        }

        val spinner: Spinner = view.findViewById(R.id.fragmentSpinnerTimeSlot)
        // Create an ArrayAdapter using the string array and a default spinner layout
        spinner.onItemSelectedListener = this


        checkIfFragmentAttached {
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.park_slot,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }


        // Get the arguments passed to the fragment
        // In this case we need the park id, which is a Long with the name Global.PARAM_PARK_ID
        val params = arguments
        val parkID: Long = params!!.get(Global.PARAM_PARK_ID).toString().toLong()
        estacionamento.idParque = parkID
        estacionamento.idUtilizador = 1
        licencePlate = params!!.get(Global.PARAM_PARK_LICENCE_PLATE).toString()

        // Execute http request to get data from the park
        val request = ServiceBuilder.buildService(ParqueEndpoint::class.java)
        val call = request.getParqueByID(parkID)


        call.enqueue(object : Callback<List<Parque>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<List<Parque>>, response: Response<List<Parque>>) {
                if (response.isSuccessful) {

                    // Get the details of a park and set them in the XML
                    val parque: Parque = response.body()?.get(0)!!
                    checkIfFragmentAttached { setParkData(parque) }
                }
            }
            override fun onFailure(call: Call<List<Parque>>, t: Throwable) {
                checkIfFragmentAttached {Toast.makeText(requireContext(), getString(R.string.park_detail_error) + t.message, Toast.LENGTH_LONG).show() }
            }
        })
        return view
    }

    private fun startPark() {
        val request = ServiceBuilder.buildService(EstacionamentoEndpoint::class.java)
        val req = Estacionamento(
            idUtilizador = estacionamento.idUtilizador,
            idParque = estacionamento.idParque,
            tempoParque = estacionamento.tempoParque
        )
        val call = request.postEstacionamento(req)

        call.enqueue(object : Callback<Estacionamento> {
            override fun onResponse(call: Call<Estacionamento>, response: Response<Estacionamento>) {
                if (response.isSuccessful) {

                    // Ir para a página activity main
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            }
            override fun onFailure(call: Call<Estacionamento>, t: Throwable) {
                Toast.makeText(requireContext(), "Ocorreu um erro ao registar o estacionamento ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        checkIfFragmentAttached {
            parkTimeSlot = formatTimeSlot(parent.getItemAtPosition(pos).toString())
            setParkTimeData(parkTimeSlot)
            estacionamento.tempoParque = parkTimeSlot
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
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
     * Function to set the data of park, car and time in the XML layout
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setParkData(park: Parque) {
        requireView().findViewById<TextView>(R.id.fragmentParkName).text = park.nomeParque
        requireView().findViewById<TextView>(R.id.fragmentParkAddress).text = park.morada

        setParkTimeData(parkTimeSlot)
        requireView().findViewById<TextView>(R.id.fragmentParkCar).text = licencePlate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setParkTimeData(timeSlot: Long) {
        val startTime = LocalDateTime.now()
        val endTime = startTime.plusMinutes(timeSlot)
        requireView().findViewById<TextView>(R.id.fragmentParkStartTime).text = getString(R.string.park_time, formatHours(startTime.hour), formatHours(startTime.minute))
        requireView().findViewById<TextView>(R.id.fragmentParkEndTime).text = getString(R.string.park_time, formatHours(endTime.hour), formatHours(endTime.minute))
    }
    /**
     * Format hours or minutes or seconds to show a 0 (zero) before the number if it is less than 9
     * so we don't end up having "14:3" instead of "14:03"
     */
    private fun formatHours(param: Int): String {
        return if(param <= 9) {"0$param"; } else "$param";
    }

    private fun toggleLimit(buttonLimit: Button, buttonLimitUndefined: Button) {
        isLimited = !isLimited
        val linearLayoutTimer = requireView().findViewById<LinearLayout>(R.id.fragmentMiddleDropDownSlot)

        val fragmentParkEndTimeIcon = requireView().findViewById<ImageView>(R.id.fragmentParkEndTimeIcon)
        val fragmentParkEndTime = requireView().findViewById<TextView>(R.id.fragmentParkEndTime)

        if(isLimited) {
            buttonLimit.setBackgroundColor(resources.getColor(R.color.button_active))
            buttonLimit.setTextColor(resources.getColor(R.color.white))
            buttonLimitUndefined.setTextColor(resources.getColor(androidx.fragment.R.color.secondary_text_default_material_light))
            buttonLimitUndefined.setBackgroundColor(resources.getColor(R.color.button_fade))
            linearLayoutTimer.visibility = View.VISIBLE
            fragmentParkEndTime.visibility = View.VISIBLE
            fragmentParkEndTimeIcon.visibility = View.VISIBLE
            estacionamento.tempoParque = parkTimeSlot
        } else {
            buttonLimit.setBackgroundColor(resources.getColor(R.color.button_fade))
            buttonLimit.setTextColor(resources.getColor(androidx.fragment.R.color.secondary_text_default_material_light))
            buttonLimitUndefined.setTextColor(resources.getColor(R.color.white))
            buttonLimitUndefined.setBackgroundColor(resources.getColor(R.color.button_active))
            fragmentParkEndTimeIcon.visibility = View.GONE
            linearLayoutTimer.visibility = View.GONE
            fragmentParkEndTime.visibility = View.GONE
            estacionamento.tempoParque = null
        }
    }

    private fun formatTimeSlot(timeSlot: String): Long {
        val timeSlotClean: String = timeSlot.replace("h","")
        val list = timeSlotClean.split(":")

        val hours: Int = Integer.parseInt(list[0]) * 60
        val minutes: Int = Integer.parseInt(list[1])

        // Integer.parseInt(list[0]) * 60 gives us the hour * 60, the hour in minutes
        // Integer.parseInt(list[1]) gives us the minutes already
        // Example: 01:30 => list[0] = 1 &  list[1] = 30 ----> list[0] * 60 = 60 min + list[1] = 30 => 90 minutes
        return (hours + minutes).toLong()
    }
}