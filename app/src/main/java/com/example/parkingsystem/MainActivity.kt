package com.example.parkingsystem

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.location.Address
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.parkingsystem.fragment.HomeFragment
import com.example.parkingsystem.fragment.QrCodeFragment
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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


class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {

    private var mMap: GoogleMap? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 111
    private lateinit var locationAtual : Location
    private val hander = Handler()

    // Fragments
    private lateinit var qrCodeFragment: QrCodeFragment
    private lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        run("http://192.168.1.78:3000/api/parques/allParques")



        getLocation();
        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val btnsearch = findViewById<Button>(R.id.proc)
        btnsearch.setOnClickListener {
            val locationSearch: EditText = findViewById(R.id.et_search)
            var location: String
            location = locationSearch.text.toString().trim()
            var addressList: List<Address>? = null

            if (location == null || location == ""){
                Toast.makeText(this, "provide location", Toast.LENGTH_SHORT).show()
            }else{
                val geoCoder = Geocoder(this)
                try {
                    addressList = geoCoder.getFromLocationName(location, 1)
                }catch (e: IOException){
                    e.printStackTrace()
                }

                val address = addressList!![0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            }
        }

        // Initialize fragments
        qrCodeFragment = QrCodeFragment()
        homeFragment = HomeFragment()

        supportActionBar?.hide()
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment)
        fragmentTransaction.commit()
    }

    fun redirectToHome(view: View) {
        findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = "Home"
        setFragment(homeFragment)
    }

    fun redirectToQRCode(view: View) {
        findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = "QR Code"
        setFragment(qrCodeFragment)
    }
    fun redirectToUser(view: View) {}
    fun redirectToGear(view: View) {}


    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
    }


    fun getDirectionURL(origin:LatLng, dest:LatLng) : String{
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

    public fun decodePolyline(encoded: String): List<LatLng> {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ){
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        }else{
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
    }

    protected fun buildGoogleApiClient(){
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onLocationChanged(location: Location) {
        locationAtual = location
        moveCamera(LatLng(location.latitude,location.longitude), 15f, "My Location")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        this.mMap!!.isMyLocationEnabled = true

    }
    private fun moveCamera(latlng : LatLng, zoom : Float, title : String){
        CoroutineScope(Main).launch {
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom))
            if (!title.equals("My Location")) mMap!!.addMarker(MarkerOptions().position(latlng).title(title))
        }
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onInfoWindowClick(p0: Marker) {
        val lat = p0.position.latitude
        val lng = p0.position.longitude
        val rl : RelativeLayout = findViewById(R.id.rl)
        rl.visibility = View.VISIBLE
        val tv1 : TextView = findViewById(R.id.tv1)
        tv1.setText(p0.title)
        val tv2 : TextView = findViewById(R.id.tv2)
        tv2.setText(p0.snippet + " " + lat + " " + lng)
        val btn_rota = findViewById<Button>(R.id.btnrota)
        btn_rota.setOnClickListener {
            val location1 = LatLng(lat, lng)
            mMap!!.addMarker(MarkerOptions().position(LatLng(locationAtual.latitude, locationAtual.longitude)).title("XXXX").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locationAtual.latitude, locationAtual.longitude), 15f))
            val URL = getDirectionURL(LatLng(locationAtual.latitude, locationAtual.longitude), location1)
            GetDirection(URL).execute()
        }
        val btn_exit = findViewById<Button>(R.id.btnexit)
        btn_exit.setOnClickListener {
            rl.visibility = View.INVISIBLE
        }
    }

    fun reservar(view: View) {}


    fun run(url: String) {

                val client = OkHttpClient()

                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.i("Falha", e.toString())
                    }
                    override fun onResponse(call: Call, response: Response) {

                        val json= response.body?.string()

                        val jsonArray = JSONTokener(json).nextValue() as JSONArray

                            for (i in 0 until jsonArray.length()) {

                                val x = jsonArray.getJSONObject(i).getString("nomeParque")
                                val y = jsonArray.getJSONObject(i).getString("morada")
                                val lat = jsonArray.getJSONObject(i).getString("latitude").toDouble()
                                val lng = jsonArray.getJSONObject(i).getString("longitude").toDouble()

                                hander.post(Runnable() {
                                    mMap!!.addMarker(MarkerOptions().position(LatLng(lat, lng)).title(x).snippet(y))
                                })
                            }
                    }
                } )
    }

}







