package com.example.parkingsystem

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.parkingsystem.fragment.HomeFragment
import com.example.parkingsystem.fragment.QrCodeFragment
import com.example.parkingsystem.room.application.UsersApplication
import com.example.parkingsystem.room.viewModel.UserViewModel

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

        userViewModel.allUsers.observe(this) { users ->
            users?.let {
                //Lógica de logout (Falta)
                Log.d("userStorage", users.toString())
            }
        }
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







