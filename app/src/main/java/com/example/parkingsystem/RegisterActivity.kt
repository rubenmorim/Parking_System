package com.example.parkingsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.parkingsystem.api.ServiceBuilder
import com.example.parkingsystem.api.user.UserEndPoints
import com.example.parkingsystem.model.Res
import com.example.parkingsystem.model.User
import com.example.parkingsystem.model.post.LoginRequest
import com.example.parkingsystem.model.post.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val backView = findViewById<ImageView>(R.id.backImageView)
        backView.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun onClickRegister(view: View) {
        // person info
        val email = findViewById<EditText>(R.id.editTextEmailAddress).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        val fisrtName = findViewById<EditText>(R.id.editTextFirstName).text.toString()
        val lastName = findViewById<EditText>(R.id.editTextLastName).text.toString()
        val birthday = findViewById<EditText>(R.id.editTextBirthday).text.toString()
        // car info
        val carName = findViewById<EditText>(R.id.editTextCarName).text.toString()
        val plate = findViewById<EditText>(R.id.editTextCarPlate).text.toString()


        if (
            email == "" ||
            password == "" ||
            birthday == "" ||
            fisrtName == "" ||
            lastName == "")
        {
            Toast.makeText(this@RegisterActivity, "Preencha os campos de utilizador!", Toast.LENGTH_SHORT).show()
            return
        }
        val request = ServiceBuilder.buildService(UserEndPoints::class.java)
        val req = RegisterRequest(
            email = email,
            password = password,
            firstName = fisrtName,
            lastName = lastName,
            birthday = birthday,
            matricula = plate,
            nomeCarro = carName
        )
        val call = request.register(req)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {

                    val res: User = response.body()!!
                    val user = arrayOf( res.id.toString(), res.email, res.firstName, res.lastName, res.birthday )

                    // Ir para a página activity main
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java).apply {
                        putExtra("USER", user)
                    }
                    startActivity(intent)
                    Toast.makeText(this@RegisterActivity, res.id.toString() + " - " + res.firstName, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Email já existe!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}