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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.cocktailapp.R


@Composable
fun ResultScreen(modifier: Modifier = Modifier){

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
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_connection_error),
            contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed),
            modifier = modifier.padding(16.dp)
        )
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
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
){

    val startDestination = CocktailsCategory.Alco
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    when(cocktailUiState) {
        is CocktailUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is CocktailUiState.Success -> ResultScreen(
            modifier = modifier.fillMaxSize()
        )
        is CocktailUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,

){

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
