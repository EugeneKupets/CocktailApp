package com.example.cocktailapp.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

@Serializable
data class UserInteraction(
    val userId: String,
    val cocktailId: String,
    val cocktailName: String? = "Cocktail",
    val imgSrc: String? = "",
    val isFavorite: Boolean
)

interface CocktailApiService {
    @GET("favorites/{email}")
    suspend fun getFavorites(@Path("email") email: String): List<UserInteraction>

    @POST("favorites")
    suspend fun saveFavorite(@Body interaction: UserInteraction)
}

object BackendClient {
    // ВСТАВТЕ СЮДИ ВАШЕ ПОСИЛАННЯ З RAILWAY!
    private const val BASE_URL = "https://cocktailappbackend-production.up.railway.app/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val api: CocktailApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CocktailApiService::class.java)
    }
}
