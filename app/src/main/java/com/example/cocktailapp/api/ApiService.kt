package com.example.cocktailapp.api

import com.example.cocktailapp.data.CocktailResponce
import com.example.cocktailapp.data.Cocktails
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL =
    "https://www.thecocktaildb.com/api/json/v1/1/"

private val json = Json{
    ignoreUnknownKeys = true
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("filter.php?a=Alcoholic")
    suspend fun getAlcoholicCocktails(): CocktailResponce

    @GET("filter.php?a=Non_Alcoholic")
    suspend fun getNonAlcoholicCocktails(): CocktailResponce
}

object CocktailApi{
    val retrofitService:ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}