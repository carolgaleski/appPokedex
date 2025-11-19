package ufpr.br.apppokedex.api

import ufpr.br.apppokedex.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class LoginRequest(
    val  user: String,
    val password: String
)
interface Endpoint {
    @Headers("Content-Type: application/json")
    @POST("/usuarios/auth")
    fun auth(@Body loginRequest: LoginRequest) : Call<Usuario>
}