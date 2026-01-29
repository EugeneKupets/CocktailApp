package com.example.cocktailapp.ui.theme


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cocktailapp.viewmodel.CocktailsViewModel
import com.example.cocktailapp.data.CocktailsCategory
import com.example.cocktailapp.viewmodel.CocktailUiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.cocktailapp.R
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cocktailapp.data.Cocktails
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.cocktailapp.data.CocktailDetails


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
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ) {
        Image(
            painter = painterResource(R.drawable.ic_connection_error),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.loading_failed),
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CocktailsScreen(
    cocktailUiState: CocktailUiState,
    modifier: Modifier = Modifier
){

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
fun CocktailsNavigationBar(
    viewModel: CocktailsViewModel = viewModel(),
    modifier: Modifier = Modifier){
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
            cocktailUiState = uiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CocktailCard(
    cocktails: Cocktails
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
        ) {
            GlideImage(
                model = cocktails.imgSrc,
                contentDescription = cocktails.name,
                loading = placeholder(R.drawable.loading_img),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CocktailInfo(
    modifier: Modifier = Modifier,
    cocktails: CocktailDetails
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
       GlideImage(
           model = cocktails.imgSrc,
           contentDescription = cocktails.name,
           loading = placeholder(R.drawable.loading_img),
           modifier = Modifier
               .fillMaxWidth()
               .height(300.dp),
           contentScale = ContentScale.Crop
       )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = cocktails.name,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = "${cocktails.category} • ${cocktails.glass}",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = "Ingredients",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxSize()
        )

        cocktails.ingredients.forEach { (ingredient, measure) ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "• ${ingredient}",
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp
                )
            }
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

