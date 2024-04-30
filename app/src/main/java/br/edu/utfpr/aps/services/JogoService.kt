package br.edu.utfpr.aps.services

import br.edu.utfpr.aps.entidades.Categorias
import br.edu.utfpr.aps.entidades.QuestionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface JogoService {
    @Headers("Accept: application/json")
    @GET("api.php")
    fun getPerguntas(

        @Query("amount")
        amount: Int = 1,

        @Query("category")
        category: Int,

        @Query("difficulty")
        difficulty: String

    ): Call<QuestionsResponse>

    @Headers("Accept: application/json")
    @GET("api_category.php")
    fun getCategoria(): Call<Categorias>
}