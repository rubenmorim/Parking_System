package com.example.parkingsystem.api.matricula
import com.example.parkingsystem.model.Matricula
import com.example.parkingsystem.model.Utilizador
import com.example.parkingsystem.model.post.UtilizadorPost
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


// Refer: https://johncodeos.com/how-to-make-post-get-put-and-delete-requests-with-retrofit-using-kotlin/

interface MatriculaEndpoint {

    @POST("/api/matricula/getMatriculaUtilizador")
    fun getMatriculaUtilizador(@Body utilizador: UtilizadorPost): Call<List<Matricula>>

    @FormUrlEncoded
    @POST("/api/matricula/createMatricula/")
    fun getUserById(): Call<Matricula>
}