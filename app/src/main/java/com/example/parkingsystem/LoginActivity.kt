package com.example.parkingsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.user.UserEndPoints
import com.example.parkingsystem.model.Res
import com.example.parkingsystem.model.post.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signUpView = findViewById<View>(R.id.signUp)

        signUpView.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }


    }

    fun onClickLogin(view: View) {
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val password = findViewById<EditText>(R.id.editTextTextPassword)

        if (email.text.toString() == "" || password.text.toString() == "") {
            Toast.makeText(this@LoginActivity, "Preencha os campos de utilizador!", Toast.LENGTH_SHORT).show()
            return
        }
        val request = ServiceBuilder.buildService(UserEndPoints::class.java)
        val req = LoginRequest( email = email.text.toString(), password = password.text.toString() )
        val call = request.login(req)

        call.enqueue(object : Callback<Res> {
            override fun onResponse(call: Call<Res>, response: Response<Res>) {
                if (response.isSuccessful) {

                    val res: Res = response.body()!!
                    val user = arrayOf(res.response.id.toString(), res.response.email, res.response.firstName, res.response.lastName, res.response.birthday )

                    // Ir para a página activity main
                    val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                        putExtra("USER", user)
                    }
                    startActivity(intent)
                    Toast.makeText(this@LoginActivity, res.response.id.toString() + " - " + res.response.firstName, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Res>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Email ou password incorreto(s)", Toast.LENGTH_SHORT).show()
            }
        })
    }
}