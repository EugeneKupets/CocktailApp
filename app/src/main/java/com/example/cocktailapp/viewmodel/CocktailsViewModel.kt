package com.example.cocktailapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.example.cocktailapp.api.CocktailApi
import com.example.cocktailapp.data.CocktailDetails
import com.example.cocktailapp.data.Cocktails
import com.example.cocktailapp.data.CocktailsCategory
import com.example.cocktailapp.data.toDetails
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

    private var alcoholicList: List<Cocktails>? = null
    private var nonAlcoholicList: List<Cocktails>? = null

    init {
        getCocktails(CocktailsCategory.Alco)
    }

    fun getCocktails(category: CocktailsCategory){
        viewModelScope.launch {

            val cacheData = when(category){
                CocktailsCategory.Alco -> alcoholicList
                CocktailsCategory.NonAlco -> nonAlcoholicList
            }

            if (cacheData != null){
                cocktailUiState = CocktailUiState.Success(cacheData)
                return@launch
            }

            cocktailUiState = CocktailUiState.Loading

            try {
                val response = when (category){
                    CocktailsCategory.Alco ->
                        CocktailApi.retrofitService.getAlcoholicCocktails()
                    CocktailsCategory.NonAlco ->
                        CocktailApi.retrofitService.getNonAlcoholicCocktails()
                }
                val cocktails = response.drinks?.map{
                    Cocktails(
                        id = it.idDrink,
                        name = it.strDrink,
                        it.strDrinkThumb)
                }?:emptyList()

                when(category){
                    CocktailsCategory.Alco -> alcoholicList = cocktails
                    CocktailsCategory.NonAlco -> nonAlcoholicList = cocktails
                }

                cocktailUiState = CocktailUiState.Success(cocktails)

            }   catch (e: IOException){
                cocktailUiState = CocktailUiState.Error
            }   catch (e: HttpException){
                cocktailUiState = CocktailUiState.Error
            }
        }
    }

    sealed interface CocktailDetailUiState{
        data class Success(val cocktail: CocktailDetails) : CocktailDetailUiState
        object Error : CocktailDetailUiState
        object Loading: CocktailDetailUiState
    }

    var cocktailDetailUiState: CocktailDetailUiState by
            mutableStateOf(CocktailDetailUiState.Loading)
        private set

    fun getCocktailDetails(id: String){
        viewModelScope.launch {
            cocktailDetailUiState = CocktailDetailUiState.Loading
            cocktailDetailUiState = try {

                val response = CocktailApi.retrofitService.getCocktailById(id)
                val apiCocktails = response.drinks?.firstOrNull()

                if (apiCocktails != null){
                    CocktailDetailUiState.Success(apiCocktails.toDetails())
                }else {
                    CocktailDetailUiState.Error
                }

            }catch (e: IOException){
                CocktailDetailUiState.Error
            }catch (e: HttpException){
                CocktailDetailUiState.Error
            }
        }
    }
}