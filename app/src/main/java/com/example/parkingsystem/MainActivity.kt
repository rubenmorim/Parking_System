package com.example.parkingsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.parkingsystem.fragment.HomeFragment
import com.example.parkingsystem.fragment.Profile
import com.example.parkingsystem.fragment.QrCodeFragment
import com.example.parkingsystem.room.application.UsersApplication
import com.example.parkingsystem.room.entity.User
import com.example.parkingsystem.room.viewModel.UserViewModel

class MainActivity : AppCompatActivity() {

    // Fragments
    private lateinit var qrCodeFragment: QrCodeFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var profile: Profile

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((application as UsersApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val res = intent.getStringArrayExtra("USER")
        val isLoguedOut = intent.getBooleanExtra("LOGOUT", false)
        if (isLoguedOut) {
            userViewModel.deleteAll()
        }

        //observer do utilizador
        userViewModel.allUsers.observe(this) { users ->
            users?.let {

                //Lógica de logout
                if (it.isEmpty()) {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    // Initialize fragments
                    qrCodeFragment = QrCodeFragment(it[0].id)
                    homeFragment = HomeFragment(it[0].id)
                    profile = Profile(it[0])
                }
            }
        }

        //Lógica de inserção no Room
        if (res != null) {
            val id = res[0]?.toLong()
            val email = res[1]
            val firstName = res[2]
            val lastName = res[3]
            val birthday = res[4]

            //Lógica de inserir no Room (Falta)
            val user = User(id!!, email!!, firstName!!, lastName!!, birthday!!)
            userViewModel.insert(user)
        }

        supportActionBar?.hide()

    }

    fun setFragment(fragment: Fragment, argList: Map<String, String>?) {

        val bundle = Bundle()

        // Detect if we want to send some data to the fragment
        if (argList != null && argList.isNotEmpty()) {
            for (arg in argList) {
                bundle.putString(arg.key, arg.value)
            }

            // Set the arguments
            fragment.arguments = bundle;
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment)
        fragmentTransaction.commit()
    }

    fun redirectToHome(view: View) {
        findViewById<ImageView>(R.id.imageViewLinearLayoutTitle).setImageResource(R.drawable.ic_house_solid)
        findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(R.string.home)
        setFragment(homeFragment, mapOf())
    }

    fun redirectToQRCode(view: View) {
        findViewById<ImageView>(R.id.imageViewLinearLayoutTitle).setImageResource(R.drawable.ic_qrcode_solid)
        findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(R.string.qrCode)
        setFragment(qrCodeFragment, mapOf())
    }

    fun redirectToUser(view: View) {
        findViewById<TextView>(R.id.textViewLinearLayoutTitle).text = getString(R.string.profile)
        setFragment(profile, mapOf())
    }


}







