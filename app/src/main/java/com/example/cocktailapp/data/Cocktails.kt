package com.example.cocktailapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cocktails(
    val id: String,
    @SerialName(value = "img_src")
    val imgSrc: String
)
