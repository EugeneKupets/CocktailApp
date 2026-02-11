@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.cocktailapp

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cocktailapp.data.CocktailsCategory
import com.example.cocktailapp.ui.theme.CocktailInfo
import com.example.cocktailapp.ui.theme.CocktailsScreen
import com.example.cocktailapp.ui.theme.ErrorScreen
import com.example.cocktailapp.ui.theme.FilterScreen
import com.example.cocktailapp.ui.theme.IngredientScreen
import com.example.cocktailapp.ui.theme.LoadingScreen
import com.example.cocktailapp.ui.theme.LoginScreen
import com.example.cocktailapp.viewmodel.CocktailUiState
import com.example.cocktailapp.viewmodel.CocktailsViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CocktailApp() {
    val navController = rememberNavController()
    val viewModel: CocktailsViewModel = viewModel()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {

            composable("login"){
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home"){
                            popUpTo("login") {inclusive = true}
                        }
                    }
                )
            }
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    animatedVisibilityScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onCocktailClick = { cocktailId ->
                        navController.navigate("details/$cocktailId")
                    },
                    onOpenFilter = { navController.navigate("filter") }
                )
            }

            composable("filter") {
                FilterScreen(
                    allIngredients = viewModel.allIngredients,
                    selectedIngredient = viewModel.selectedIngredient,
                    onIngredientSelected = {ingredient ->
                        viewModel.selectedIngredient(ingredient)
                    },
                    onApply = {
                        viewModel.applyIngredientFilter()
                        navController.popBackStack()
                    },
                    onClear = {
                        viewModel.clearFilter()
                    }
                )
            }

            composable(
                route = "ingredient/{ingredientName}",
                arguments = listOf(navArgument("ingredientName"){
                    type =
                        NavType.StringType
                })
            ){ backStackEntry ->
                val ingredientName =
                    backStackEntry.arguments?.getString("ingredientName") ?: ""

                LaunchedEffect(ingredientName) {
                    if (ingredientName.isNotEmpty()){
                        viewModel.loadCocktailsForIngredient(ingredientName)
                    }
                }

                val state = viewModel.ingredientCocktailUiState

                Surface(modifier = Modifier.fillMaxSize()) {
                    when(state){
                        is CocktailUiState.Loading -> LoadingScreen()
                        is CocktailUiState.Error -> ErrorScreen()
                        is CocktailUiState.Success -> {
                            IngredientScreen(
                                ingredientName = ingredientName,
                                cocktails = state.cocktails,
                                onBackClick = {navController.popBackStack()},
                                onCocktailClick = { cocktailId ->
                                    navController.navigate("details/$cocktailId")
                                },
                                animatedVisibilityScope = this@composable,
                                sharedTransitionScope = this@SharedTransitionLayout
                            )
                        }
                    }
                }
            }

            composable(
                route = "details/{cocktailId}",
                arguments = listOf(navArgument("cocktailId") {
                    type =
                        NavType.StringType
                })
            ) { backStackEntry ->
                val cocktailId =
                    backStackEntry.arguments?.getString("cocktailId") ?: ""

                LaunchedEffect(cocktailId) {
                    if (cocktailId.isNotEmpty()){
                        viewModel.getCocktailDetails(cocktailId)
                    }
                }

                val detailState = viewModel.cocktailDetailUiState

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (detailState) {
                        is CocktailsViewModel.CocktailDetailUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
                        is CocktailsViewModel.CocktailDetailUiState.Success ->
                            CocktailInfo(
                                cocktails = detailState.cocktail,
                                onBackClick = {navController.popBackStack()},
                                onIngredientClick = {ingredientName ->
                                    navController.navigate("ingredient/$ingredientName")
                                },
                                animatedVisibilityScope = this@composable,
                                sharedTransitionScope = this@SharedTransitionLayout
                            )
                        is CocktailsViewModel.CocktailDetailUiState.Error -> ErrorScreen(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}
 @Composable
 @OptIn(ExperimentalSharedTransitionApi::class)
 fun HomeScreen(
     viewModel: CocktailsViewModel,
     onCocktailClick: (String) -> Unit,
     animatedVisibilityScope: AnimatedVisibilityScope,
     sharedTransitionScope: SharedTransitionScope,
     onOpenFilter: () -> Unit
 ){
     val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
     var selectedDestination by rememberSaveable { mutableStateOf(CocktailsCategory.Alco) }

     Scaffold(
         modifier = Modifier
             .nestedScroll(scrollBehavior.nestedScrollConnection)
             .fillMaxSize(),
         topBar = { CocktailTopAppBar(
             scrollBehavior = scrollBehavior,
             onFilterClick = onOpenFilter,
             onSearchQueryChanged = {query ->
                 viewModel.onSearchQueryChange(query)
             }) },
         bottomBar = {
             NavigationBar {
                 CocktailsCategory.entries.forEach { category ->
                     NavigationBarItem(
                         selected = selectedDestination == category,
                         onClick = {
                             if (selectedDestination != category) {
                                 selectedDestination = category
                                 viewModel.getCocktails(category)
                             }
                         },
                         icon = { Icon(category.icon, contentDescription = null) },
                         label = { Text(category.label) }
                     )
                 }
             }
         }
     ) { innerPadding ->
         Surface(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(innerPadding)
         ) {
             CocktailsScreen(
                 cocktailUiState = viewModel.cocktailUiState,
                 onCocktailClick = onCocktailClick,
                 animatedVisibilityScope = animatedVisibilityScope,
                 sharedTransitionScope = sharedTransitionScope,
                 modifier = Modifier.fillMaxSize()
             )
         }
     }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onFilterClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
){

    var isSearchActive by rememberSaveable {mutableStateOf(false) }
    var searchQuery by rememberSaveable {mutableStateOf("") }

    if (isSearchActive){
        TopAppBar(
            scrollBehavior = scrollBehavior,
            title = {
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearchQueryChanged(it)
                    },
                    placeholder = {Text("Search...")},
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    isSearchActive = false
                    searchQuery = ""
                    onSearchQueryChanged("")
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if(searchQuery.isNotEmpty()){
                    IconButton(onClick = {
                        searchQuery = ""
                        onSearchQueryChanged("")
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear")
                    }
                }
            }
        )
    }else {

        CenterAlignedTopAppBar(
            scrollBehavior = scrollBehavior,
            title = {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                )
            },

            navigationIcon = {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter"
                    )
                }
            },

            actions = {
                IconButton(onClick = {isSearchActive = true}) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Button"
                    )
                }
            }
        )
    }
}