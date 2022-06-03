package com.example.parkingsystem.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.location.Address
import android.location.LocationListener
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingsystem.GoogleMapDTO
import com.example.parkingsystem.R
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.parque.ParqueEndpoint
import com.example.parkingsystem.model.Matricula
import com.example.parkingsystem.model.Parque
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONTokener
import java.io.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener {

    private var mMap: GoogleMap? = null
    private val hander = Handler()

    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                Log.d("**** SARA", p0.toString())
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                Log.d("**** SARA", lastLocation.toString())
                var loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))
            }
        }

        createLocationRequest()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = com.google.android.gms.location.LocationRequest()
        // interval specifies the rate at which your app will like to receive updates.
        locationRequest.interval = 60000
        locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val request = ServiceBuilder.buildService(ParqueEndpoint::class.java)
        val call = request.getParques()

        call.enqueue(object : Callback<List<Parque>> {
            override fun onResponse(call: Call<List<Parque>>, response: Response<List<Parque>>) {

                val parqueList: List<Parque> = response.body()!!

                for (parque in parqueList) {

                    hander.post(Runnable() {
                        mMap!!.addMarker(MarkerOptions().position(LatLng(parque.latitude, parque.longitude)).title(parque.nomeParque).snippet(parque.morada))
                    })
                }
            }
            override fun onFailure(call: Call<List<Parque>>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_home, container, false)

        val btnsearch = v.findViewById<Button>(R.id.proc)

        btnsearch.setOnClickListener {
            val locationSearch: EditText = v.findViewById(R.id.et_search)
            val location: String = locationSearch.text.toString().trim()
            var addressList: List<Address>? = null

            if (location == ""){
                // Toast.makeText(v, "provide location", Toast.LENGTH_SHORT).show()
            } else {
                val geoCoder = Geocoder(requireContext())
                try {
                    addressList = geoCoder.getFromLocationName(location, 1)
                }catch (e: java.io.IOException){
                    e.printStackTrace()
                }

                val address = addressList!![0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            }
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mapFragment = childFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        /**
        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        if(mapFragment != null) {
            Toast.makeText(requireContext(), "Tomates", Toast.LENGTH_SHORT).show()
        }
        mapFragment.getMapAsync(this) */

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    private fun getDirectionURL(origin: LatLng, dest: LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving&key=AIzaSyBmtZXVwPpAqRuMkOOfQlsrCUD-RsIBT_0"
    }

    inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg p0: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()
            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path = ArrayList<LatLng>()

                for(i in 0..(respObj.routes[0].legs[0].steps.size-1)){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e:Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for(i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            mMap?.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while(b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while(b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;

        mMap!!.setOnInfoWindowClickListener(this)

    }

    override fun onInfoWindowClick(p0: Marker) {


        val lat = p0.position.latitude
        val lng = p0.position.longitude
        val rl : RelativeLayout = requireView().findViewById(R.id.rl)
        rl.visibility = View.VISIBLE
        val tv1 : TextView = requireView().findViewById(R.id.tv1)
        tv1.setText(p0.title)
        val tv2 : TextView = requireView().findViewById(R.id.tv2)
        tv2.setText(p0.snippet + " " + lat + " " + lng)
        val btn_rota = requireView().findViewById<Button>(R.id.btnrota)
        btn_rota.setOnClickListener {
            val location1 = LatLng(lat, lng)
            mMap!!.addMarker(
                MarkerOptions().position(LatLng(lastLocation.latitude, lastLocation.longitude)).title("XXXX").icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), 15f))
            val URL = getDirectionURL(LatLng(lastLocation.latitude, lastLocation.longitude), location1)
            GetDirection(URL).execute()
        }
        val btn_exit = requireView().findViewById<Button>(R.id.btnexit)
        btn_exit.setOnClickListener {
            rl.visibility = View.INVISIBLE
        }
    }

    fun reservar(view: View) {}
    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }

}