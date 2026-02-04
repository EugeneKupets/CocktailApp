package com.example.cocktailapp.api

import com.example.cocktailapp.data.CocktailResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://www.thecocktaildb.com/api/json/v1/1/"

private val json = Json{
    ignoreUnknownKeys = true
    coerceInputValues = true
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("filter.php?a=Alcoholic")
    suspend fun getAlcoholicCocktails(): CocktailResponse

    @GET("filter.php?a=Non_Alcoholic")
    suspend fun getNonAlcoholicCocktails(): CocktailResponse

    @GET("lookup.php")
    suspend fun getCocktailById(@Query("i") id: String): CocktailResponse
}

object CocktailApi{
    val retrofitService:ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}