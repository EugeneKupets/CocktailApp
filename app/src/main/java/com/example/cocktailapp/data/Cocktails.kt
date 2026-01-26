package com.example.cocktailapp.data

import com.example.cocktailapp.api.CocktailApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CocktailResponce(
    val drinks: List<CocktailApiCocktails>
)

@Serializable
data class CocktailApiCocktails(
    val idDrink: String,
    val strDrink: String,
    @SerialName("strDrinkThumb")
    val strDrinkThumb: String
)
@Serializable
data class Cocktails(
    val id: String,
    val name: String,
    @SerialName(value = "img_src")
    val imgSrc: String
)
