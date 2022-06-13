package com.example.parkingsystem.api.matricula
import com.example.parkingsystem.model.Matricula
import com.example.parkingsystem.model.Message
import com.example.parkingsystem.model.Utilizador
import com.example.parkingsystem.model.post.UtilizadorPost
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


// Refer: https://johncodeos.com/how-to-make-post-get-put-and-delete-requests-with-retrofit-using-kotlin/

interface MatriculaEndpoint {

    @GET("/api/matricula/getMatriculaUtilizador/{id}")
    fun getMatriculaUtilizador(@Path("id") id: Long): Call<List<Matricula>>

    @GET("/api/matricula/updateMatriculaSelected?idUtilizador&matricula")
    fun updateMatricula(@Query("idUtilizador") idUtilizador: Long, @Query("matricula") matricula: String): Call<List<Matricula>>

    @POST("/api/matricula/createMatricula/")
    fun addMatricula(
        @Body requestBody: RequestBody
    ): Call<Matricula>

    @DELETE("/api/matricula/apagarMatricula/{id}")
    fun delMatricula(@Path("id") id: String): Call<Message>
}