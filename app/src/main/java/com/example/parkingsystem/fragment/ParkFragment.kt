package com.example.parkingsystem.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.parkingsystem.R
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.parque.ParqueEndpoint
import com.example.parkingsystem.global.Global
import com.example.parkingsystem.model.Parque
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
class ParkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var argList: Map<String, String>? = null
    private var isLimited: Boolean = true
    private var limit: Int = 30
    private var licencePlate: String = ""

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

        // Get the arguments passed to the fragment
        // In this case we need the park id, which is a Long with the name Global.PARAM_PARK_ID
        val params = arguments
        val parkID: Long = params!!.get(Global.PARAM_PARK_ID).toString().toLong()
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
        TODO("Not yet implemented")
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

        val startTime = LocalDateTime.now()
        val endTime = startTime.plusMinutes(30)

        requireView().findViewById<TextView>(R.id.fragmentParkStartTime).text = getString(R.string.park_time, formatHours(startTime.hour), formatHours(startTime.minute))
        requireView().findViewById<TextView>(R.id.fragmentParkEndTime).text = getString(R.string.park_time, formatHours(endTime.hour), formatHours(endTime.minute))
        requireView().findViewById<TextView>(R.id.fragmentParkCar).text = licencePlate
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

        if(isLimited) {
            buttonLimit.setBackgroundColor(resources.getColor(R.color.button_active))
            buttonLimit.setTextColor(resources.getColor(R.color.white))
            buttonLimitUndefined.setTextColor(resources.getColor(androidx.fragment.R.color.secondary_text_default_material_light))
            buttonLimitUndefined.setBackgroundColor(resources.getColor(R.color.button_fade))

        } else {
            buttonLimit.setBackgroundColor(resources.getColor(R.color.button_fade))
            buttonLimit.setTextColor(resources.getColor(androidx.fragment.R.color.secondary_text_default_material_light))
            buttonLimitUndefined.setTextColor(resources.getColor(R.color.white))
            buttonLimitUndefined.setBackgroundColor(resources.getColor(R.color.button_active))

        }
    }
}