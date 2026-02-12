package com.example.cocktailapp.data

import android.accessibilityservice.GestureDescription
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.descriptors.SerialKind
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning

enum class CocktailsCategory(
    val label: String,
    val icon: ImageVector,
    val isApiCategory: Boolean = true
) {
    Alco("Alco", Icons.Default.Warning),
    NonAlco("Non-Alco", Icons.Default.CheckCircle),
    Profile("Profile", Icons.Default.Person, false)
}