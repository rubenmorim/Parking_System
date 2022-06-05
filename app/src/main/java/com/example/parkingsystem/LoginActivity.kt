package com.example.parkingsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.api.EndPoints
import com.example.parkingsystem.api.LoginRequest
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



    }

    fun onClickLogin(view: View) {
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val password = findViewById<EditText>(R.id.editTextTextPassword)
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val req = LoginRequest( email = email.text.toString(), password = password.text.toString() )
        val call = request.login(req)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful){
                    val c: User = response.body()!!
                    Toast.makeText(this@LoginActivity, c.id.toString() + "-" + c.firstName, Toast.LENGTH_SHORT).show()
                }
                Log.d("tag0", response.toString())
                Toast.makeText(this@LoginActivity, response.toString(), Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}