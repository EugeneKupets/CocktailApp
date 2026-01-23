package com.example.cocktailapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

sealed interface CocktailUiState{
    data class Success(val icon: String) : CocktailUiState
    object Error : CocktailUiState
    object Loading : CocktailUiState
}

class CocktailsViewModel : ViewModel(){
    var cocktailUiState: CocktailUiState by mutableStateOf(CocktailUiState.Loading)
        private set
}