package com.example.cocktailapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.network.HttpException
import com.example.cocktailapp.api.CocktailApi
import com.example.cocktailapp.data.CocktailDetails
import com.example.cocktailapp.data.Cocktails
import com.example.cocktailapp.data.CocktailsCategory
import com.example.cocktailapp.data.toDetails
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import okio.IOException
import kotlin.collections.emptyList


sealed interface CocktailUiState{
    data class Success(val cocktails: List<Cocktails>) : CocktailUiState
    object Error : CocktailUiState
    object Loading : CocktailUiState
}

class CocktailsViewModel : ViewModel(){

    var selectedIngredient by mutableStateOf<String?>(null)
        private set

    var allIngredients = mutableStateListOf<String>()
        private set

    fun selectedIngredient(ingredient: String){
        selectedIngredient = if (selectedIngredient == ingredient) null else ingredient
    }

    fun applyIngredientFilter(){
        val ingredient = selectedIngredient

        if (ingredient == null){
            getCocktails(CocktailsCategory.Alco)
        }else {
            viewModelScope.launch {
                cocktailUiState = CocktailUiState.Loading
                try {
                    val response =
                        CocktailApi.retrofitService.getCocktailByIngredient(ingredient)

                    val cocktails = response.drinks?.map {
                        Cocktails(
                            id = it.idDrink ?: "",
                            name = it.strDrink ?: "",
                            imgSrc = it.strDrinkThumb ?: "",)
                    } ?: emptyList()

                    cocktailUiState = if (cocktails.isNotEmpty()){
                        CocktailUiState.Success(cocktails)
                    }else {
                        CocktailUiState.Error
                    }
                }catch (e: Exception){
                    cocktailUiState = CocktailUiState.Error
                }
            }
        }
    }

    fun clearFilter(){
        selectedIngredient = null
        applyIngredientFilter()
    }

    fun loadIngredients() {
        viewModelScope.launch {
            try {
                val response = CocktailApi.retrofitService.getAllIngredients()

                val ingredientsList = response.drinks?.mapNotNull { it.strIngredient1 }?.sorted() ?: emptyList()

                allIngredients.clear()
                allIngredients.addAll(ingredientsList)

            } catch (e: Exception){
                Log.e("CocktailsViewModel", "Error loading ingredients", e)
            }
        }
    }

    private var searchJob: Job?  = null

    private var lastSelectedCategory:
            CocktailsCategory = CocktailsCategory.Alco

    var searchQuery by mutableStateOf("")
        private set

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery

        searchJob?.cancel()

        if(newQuery.isEmpty()){
            getCocktails(lastSelectedCategory)
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)

            cocktailUiState = CocktailUiState.Loading

            try {
                val response = CocktailApi.retrofitService.cocktailsSearch(newQuery)
                val cocktails = response.drinks?.map {
                    Cocktails(
                        it.idDrink ?: "",
                        it.strDrink ?: "",
                        it.strDrinkThumb ?: ""
                    )
                } ?: emptyList()

                cocktailUiState = if (cocktails.isNotEmpty()){
                    CocktailUiState.Success(cocktails)
                } else{
                    CocktailUiState.Error
                }
            } catch (e: Exception){
                cocktailUiState = CocktailUiState.Error
            }
        }
    }

    var cocktailUiState: CocktailUiState by mutableStateOf(CocktailUiState.Loading)
        private set

    private var alcoholicList: List<Cocktails>? = null
    private var nonAlcoholicList: List<Cocktails>? = null

    init {
        getCocktails(CocktailsCategory.Alco)
        loadIngredients()
    }

    fun getCocktails(category: CocktailsCategory){

        lastSelectedCategory = category

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
                        id = it.idDrink ?: "",
                        name = it.strDrink ?: "",
                        it.strDrinkThumb ?: ""
                    )
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