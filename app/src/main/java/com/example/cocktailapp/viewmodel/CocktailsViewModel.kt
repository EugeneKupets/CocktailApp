package com.example.cocktailapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.network.HttpException
import com.example.cocktailapp.api.CocktailApi
import com.example.cocktailapp.data.Cocktails
import com.example.cocktailapp.data.CocktailsCategory
import kotlinx.coroutines.launch
import okio.IOException


sealed interface CocktailUiState{
    data class Success(val cocktails: List<Cocktails>) : CocktailUiState
    object Error : CocktailUiState
    object Loading : CocktailUiState
}

class CocktailsViewModel : ViewModel(){

    var cocktailUiState: CocktailUiState by mutableStateOf(CocktailUiState.Loading)
        private set

    init {
        getCocktails(CocktailsCategory.Alco)
    }

    fun getCocktails(category: CocktailsCategory){
        viewModelScope.launch {
            cocktailUiState = CocktailUiState.Loading
            cocktailUiState = try {
                val response = when (category){
                    CocktailsCategory.Alco ->
                        CocktailApi.retrofitService.getAlcoholicCocktails()
                    CocktailsCategory.NonAlco ->
                        CocktailApi.retrofitService.getNonAlcoholicCocktails()
                }
                val cocktails = response.drinks.map{
                    Cocktails(
                        id = it.idDrink,
                        name = it.strDrink,
                        it.strDrinkThumb)
                }
                CocktailUiState.Success(cocktails)
            }   catch (e: IOException){
                CocktailUiState.Error
            }   catch (e: HttpException){
                CocktailUiState.Error
            }
        }
    }
}