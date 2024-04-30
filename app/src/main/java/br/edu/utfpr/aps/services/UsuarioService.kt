package br.edu.utfpr.aps.services

import br.edu.utfpr.aps.entidades.LoginResponse
import br.edu.utfpr.aps.entidades.RankingResponse
import br.edu.utfpr.aps.entidades.RegistroResponse
import retrofit2.Call
import retrofit2.http.*

interface UsuarioService {
    @Headers("Accept: application/json")
    @GET("ranking")
    fun ranking(): Call<RankingResponse>


    @Headers("Accept: application/json")
    @POST("usuario/registrar")
    fun registrar(

        @Query("nome")
        nome: String,

        @Query("email")
        email: String,

        @Query("senha")
        senha: String

    ): Call<RegistroResponse>

    @Headers("Accept: application/json")
    @POST("usuario/login")
    fun logar(

        @Query("email")
        email: String,

        @Query("senha")
        senha: String

    ): Call<LoginResponse>

    @Headers("Accept: application/json")
    @PUT("usuario/pontuacao")
    fun pontuar(

        @Query("email")
        email: String,

        @Query("senha")
        senha: String,

        @Query("pontos")
        pontos: Int

    ): Call<LoginResponse>



}