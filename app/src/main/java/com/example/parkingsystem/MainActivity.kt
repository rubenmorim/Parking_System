package com.example.parkingsystem

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.activity.viewModels
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
import androidx.lifecycle.Observer
import com.example.parkingsystem.room.application.UsersApplication
import com.example.parkingsystem.room.entity.User
import com.example.parkingsystem.room.viewModel.UserViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONTokener
import java.io.IOException
import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {



    // Fragments
    private lateinit var qrCodeFragment: QrCodeFragment
    private lateinit var homeFragment: HomeFragment

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((application as UsersApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialize fragments
        qrCodeFragment = QrCodeFragment()
        homeFragment = HomeFragment()

        val res = intent.getStringArrayExtra("USER")
        //Lógica de inserir no Room (Falta)
        //val user = User( res?.get(0).toInt(), res.response.email, res.response.firstName, res.response.lastName, res.response.birthday )
        //Log.d("userStorage", user.id.toString())
        //userViewModel.insert(user)

        userViewModel.allUsers.observe(this, Observer { users ->
            users?.let {
                //Lógica de logout (Falta)
                Log.d("userStorage", users.toString())
            }
        })

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
}







