package com.example.parkingsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.parkingsystem.fragment.HomeFragment
import com.example.parkingsystem.fragment.QrCodeFragment

class MainActivity : AppCompatActivity() {

    // Fragments
    private lateinit var qrCodeFragment: QrCodeFragment
    private lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        qrCodeFragment = QrCodeFragment()
        homeFragment = HomeFragment()
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment)
        fragmentTransaction.commit()
    }

    fun redirectToHome(view: View) {
        setFragment(homeFragment)
    }

    fun redirectToQRCode(view: View) {
        setFragment(qrCodeFragment)
    }
    fun redirectToUser(view: View) {}
    fun redirectToGear(view: View) {}
}