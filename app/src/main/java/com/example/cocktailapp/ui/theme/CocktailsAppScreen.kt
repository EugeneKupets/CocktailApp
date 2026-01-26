package com.example.cocktailapp.ui.theme

import android.graphics.pdf.content.PdfPageGotoLinkContent
import android.service.controls.Control
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cocktailapp.viewmodel.CocktailsViewModel
import com.example.cocktailapp.data.CocktailsCategory
import com.example.cocktailapp.viewmodel.CocktailUiState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import com.example.cocktailapp.R
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.annotations.Async
import coil.compose.AsyncImage
import com.example.cocktailapp.data.Cocktails
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    cocktails: List<Cocktails>,
    modifier: Modifier = Modifier
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
      items(cocktails){cocktail ->
          CocktailCard(
              cocktails = cocktail
          )
      }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier){
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
        )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ){
        Column(
            //modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_connection_error),
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.loading_failed),
                modifier = modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
    }

    }
}

@Composable
fun NonAlco(){
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Text("NonAlco Page")
    }
}

@Composable
fun Alco(){
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Text("Alco Page")
    }
}

@Composable
fun CocktailsScreen(
    cocktailUiState: CocktailUiState,
    modifier: Modifier = Modifier
    //contentPadding: PaddingValues = PaddingValues(0.dp)
){
    //val startDestination = CocktailsCategory.Alco
    //var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    when(cocktailUiState) {
        is CocktailUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is CocktailUiState.Success -> ResultScreen(
            cocktails = cocktailUiState.cocktails,
            modifier = modifier.fillMaxSize()
        )
        is CocktailUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: CocktailsCategory,
    modifier: Modifier = Modifier
){
    NavHost(
        navController,
        startDestination = startDestination.route
    ){
        composable(CocktailsCategory.Alco.route){ Alco() }
        composable(CocktailsCategory.NonAlco.route){ NonAlco() }
    }
}

@Composable
fun CocktailsNavigationBar(
    viewModel: CocktailsViewModel = viewModel(),
    modifier: Modifier = Modifier){
    //val navController = rememberNavController()
    //val startDestination = CocktailsCategory.Alco
    val uiState = viewModel.cocktailUiState
    var selectedDestination by rememberSaveable {mutableStateOf(CocktailsCategory.Alco)}

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar{ CocktailsCategory.entries.forEachIndexed { index, category ->
                NavigationBarItem(
                    selected = selectedDestination == category,
                    onClick = {
                        viewModel.getCocktails(category)
                        selectedDestination = category
                    },
                    icon = {
                        Icon(
                            category.icon,
                             category.contentDescription
                        )
                    },
                    label = {Text(category.label)}
                )
            }
            }
        }
    ) { innerPadding ->
        CocktailsScreen(
            //navController = navController,
            //startDestination = startDestination,
            cocktailUiState = uiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CocktailCard(
    cocktails: Cocktails
){
    Card(
        modifier = Modifier
            .fillMaxSize()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
        ) {
            AsyncImage(
                model = cocktails.imgSrc,
                contentDescription = cocktails.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cocktails.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Left
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ResultScreenPreview(){
    CocktailAppTheme() {
        ResultScreen(
            cocktails = listOf(
                Cocktails("1", "Mojito Test", "https://www.thecocktaildb.com/images/media/drink/xxyywq1454511117.jpg"),
                Cocktails("2", "Long Island","https://www.thecocktaildb.com/images/media/drink/rvwrvv1468877323.jpg")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NavBarResult(){
    CocktailAppTheme() {
        CocktailsNavigationBar()
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview(){
    CocktailAppTheme() {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview(){
    CocktailAppTheme() {
        ErrorScreen()
    }
}
