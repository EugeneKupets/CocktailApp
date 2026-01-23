package com.example.cocktailapp.data

import android.accessibilityservice.GestureDescription
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.descriptors.SerialKind
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning

enum class CocktailsCategory(
    val kind: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    Alco("alco", "Alco", Icons.Default.Warning,"Alco"),
    NonAlco("nonalco", "NonAlco", Icons.Default.CheckCircle,"NonAlco")
}