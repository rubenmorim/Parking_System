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
import com.example.parkingsystem.api.*
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
        if (email.text.toString() == "" || password.text.toString() == "") {
            Toast.makeText(this@LoginActivity, "Preencha os campos de utilizador!", Toast.LENGTH_SHORT).show()
            return
        }
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val req = LoginRequest( email = email.text.toString(), password = password.text.toString() )
        val call = request.login(req)

        call.enqueue(object : Callback<Res> {
            override fun onResponse(call: Call<Res>, response: Response<Res>) {
                if (response.isSuccessful){
                    val res: Res = response.body()!!
                    //Guardar o utilizador na storage

                    //Ir para a página activity main


                    Log.d("call", call.toString())
                    Log.d("res", response.toString())
                    Log.d("code", response.code().toString())
                    Log.d("message", response.message().toString())
                    Log.d("body", res.toString())
                    Log.d("id", res.response.id.toString())
                    //Log.d("firstName", user.firstName)
                    //Log.d("lastName", user.lastName)
                    //Log.d("birthday", user.birthday)

                    Toast.makeText(this@LoginActivity, res.response.id.toString() + " - " + res.response.firstName, Toast.LENGTH_SHORT).show()
                }

                Toast.makeText(this@LoginActivity, "ola:" +response.toString(), Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<Res>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Email e/ou Password invalido(s)", Toast.LENGTH_SHORT).show()
            }
        })

    }
}