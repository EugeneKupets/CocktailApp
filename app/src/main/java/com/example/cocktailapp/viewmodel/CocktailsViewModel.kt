package com.example.cocktailapp.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.example.cocktailapp.CocktailApp
import com.example.cocktailapp.api.BackendClient
import com.example.cocktailapp.api.CocktailApi
import com.example.cocktailapp.api.UserInteraction
import com.example.cocktailapp.data.CocktailDetails
import com.example.cocktailapp.data.Cocktails
import com.example.cocktailapp.data.CocktailsCategory
import com.example.cocktailapp.data.GoogleUser
import com.example.cocktailapp.data.toDetails
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.IOException
import kotlin.collections.emptyList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext


sealed interface CocktailUiState{
    data class Success(val cocktails: List<Cocktails>) : CocktailUiState
    object Error : CocktailUiState
    object Loading : CocktailUiState
}

class CocktailsViewModel() : ViewModel(){

    init {
        // Діагностика більше не потрібна для MongoDB, але ми залишаємо порожній init
        // або додаємо іншу стартову логіку
    }

    var user by mutableStateOf<GoogleUser?>(null)
        private set

    // 1. Збереження/Оновлення улюбленого
    fun updateUserInteraction(cocktailId: String, cocktailName: String, imgSrc: String, isFavorite: Boolean, rating: Int) {
        val currentUser = user?.email ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val interaction = UserInteraction(
                    userId = currentUser,
                    cocktailId = cocktailId,
                    cocktailName = cocktailName,
                    imgSrc = imgSrc,
                    isFavorite = isFavorite,
                    rating = rating
                )

                // Виклик вашого сервера
                BackendClient.api.saveFavorite(interaction)

                Log.d("Backend", "Дані відправлено на сервер: $cocktailName")
                fetchFavorites() // Оновлюємо список
            } catch (e: Exception) {
                Log.e("Backend", "Помилка відправки", e)
            }
        }
    }

    var favorites = mutableStateListOf<String>()
        private set

    // Нова змінна для зберігання повних об'єктів коктейлів для екрану Профілю
    var favoriteCocktailsList by mutableStateOf<List<UserInteraction>>(emptyList())
        private set

    // Функція для завантаження улюблених через Бекенд
    fun fetchFavorites() {
        val currentUser = user?.email ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Запит до вашого НОВОГО сервера на Railway
                val results = BackendClient.api.getFavorites(currentUser)

                // Оновлюємо UI в головному потоці
                withContext(Dispatchers.Main) {
                    favoriteCocktailsList = results

                    // Оновлюємо також локальний список ID, щоб сердечка показувались правильно
                    favorites.clear()
                    favorites.addAll(results.map { it.cocktailId })
                }

                Log.d("Backend", "Завантажено улюблених через сервер: ${results.size}")
            } catch (e: Exception) {
                Log.e("Backend", "Помилка завантаження улюблених", e)
            }
        }
    }

    // Функція перемикання улюбленого
    // Онови існуючу функцію toggleFavorite
    fun toggleFavorite(cocktailId: String, cocktailName: String = "Cocktail", imgSrc: String = "") {
        val isCurrentlyFavorite = favorites.contains(cocktailId)
        val newFavoriteState = !isCurrentlyFavorite

        // Оновлюємо локально для миттєвої реакції UI
        if (newFavoriteState) {
            favorites.add(cocktailId)
        } else {
            favorites.remove(cocktailId)
        }

        // Відправляємо в базу (rating = 0 поки зірочок немає)
        updateUserInteraction(
            cocktailId = cocktailId,
            cocktailName = cocktailName,
            imgSrc = imgSrc,
            isFavorite = newFavoriteState,
            rating = 0
        )
    }

    // Перевірка чи коктейль улюблений
    fun isFavorite(cocktailId: String): Boolean = favorites.contains(cocktailId)

    // У CocktailsViewModel, онови функцію setUser
    fun setUser(email: String?, name: String?, photoUrl: String?) {
        user = GoogleUser(email, name, photoUrl)
        if (email != null) {
            fetchFavorites() // Завантажуємо дані одразу після логіну!
        }
    }

    fun singOut(){
        user = null
    }

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

        if (category == CocktailsCategory.Profile) return

        lastSelectedCategory = category

        viewModelScope.launch {

            val cacheData = when(category){
                CocktailsCategory.Alco -> alcoholicList
                CocktailsCategory.NonAlco -> nonAlcoholicList
                else -> null
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
                    else -> throw IllegalStateException("API not supported for $category")
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
                    else -> {}
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

    var ingredientCocktailUiState: CocktailUiState by mutableStateOf(CocktailUiState.Loading)
        private set

    fun loadCocktailsForIngredient(ingredient: String){
        viewModelScope.launch {
            ingredientCocktailUiState = CocktailUiState.Loading
            try {
                val response = CocktailApi.retrofitService.getCocktailByIngredient(ingredient)
                val cocktails = response.drinks?.map {
                    Cocktails(
                        it.idDrink ?: "",
                        it.strDrink ?: "",
                        it.strDrinkThumb ?: ""
                    )
                } ?: emptyList()

                ingredientCocktailUiState = if (cocktails.isNotEmpty()){
                    CocktailUiState.Success(cocktails)
                }else {
                    CocktailUiState.Error
                }
            }catch (e: Exception){
                ingredientCocktailUiState = CocktailUiState.Error
            }
        }
    }
}