package com.example.cocktailapp.ui.theme


import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cocktailapp.viewmodel.CocktailUiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.cocktailapp.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cocktailapp.data.Cocktails
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.findFirstRoot
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.cocktailapp.api.GoogleAuthClient
import com.example.cocktailapp.data.CocktailDetails
import com.example.cocktailapp.viewmodel.CocktailsViewModel
import kotlinx.coroutines.coroutineScope


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ResultScreen(
    cocktails: List<Cocktails>,
    onCocktailClick: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
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
              cocktails = cocktail,
              onClick = { onCocktailClick(cocktail.id) },
              animatedVisibilityScope = animatedVisibilityScope,
              sharedTransitionScope = sharedTransitionScope,
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
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CocktailsScreen(
    cocktailUiState: CocktailUiState,
    onCocktailClick: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier
){
    when(cocktailUiState) {
        is CocktailUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is CocktailUiState.Success -> ResultScreen(
            cocktails = cocktailUiState.cocktails,
            onCocktailClick = onCocktailClick,
            animatedVisibilityScope = animatedVisibilityScope,
            sharedTransitionScope = sharedTransitionScope,
            modifier = modifier.fillMaxSize()
        )
        is CocktailUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CocktailCard(
    cocktails: Cocktails,
    onClick: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
){
    with(sharedTransitionScope){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clickable { onClick(cocktails.id) }
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
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key =
                            "image-${cocktails.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
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
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CocktailInfo(
    modifier: Modifier = Modifier,
    cocktails: CocktailDetails,
    onBackClick: () -> Unit,
    onIngredientClick: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
){
    with(sharedTransitionScope){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ){
                GlideImage(
                    model = cocktails.imgSrc,
                    contentDescription = cocktails.name,
                    loading = placeholder(R.drawable.loading_img),
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "image-${cocktails.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .statusBarsPadding()
                        .background(color = Color.Black.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

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
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            cocktails.ingredients.forEach { (ingredient, measure) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,

                    modifier = Modifier
                        .fillMaxSize()
                        .clickable{onIngredientClick(ingredient)}
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    GlideImage(
                        model =
                            "https://www.thecocktaildb.com/images/ingredients/$ingredient-Medium.png",
                        contentDescription = ingredient,
                        loading = placeholder(R.drawable.loading_img),

                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    if (measure.isNotEmpty()){
                        Text(
                            text = "$measure   ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Text(
                        text = ingredient,
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Instructions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            Text(
                text = cocktails.instructions,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            )
        }
    }
}


@Composable
fun FilterScreen(
    allIngredients: List<String>,
    selectedIngredient: String?,
    onIngredientSelected: (String) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit
){
    Column(modifier = Modifier
        .padding(8.dp)
        .navigationBarsPadding()) {
        Text("Filter by Ingredients", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable{onClear()}
                .padding(vertical = 8.dp)
        ) {
            RadioButton(
                selected = selectedIngredient == null,
                onClick = onClear
            )
            Text(text = "Show All (No Filter)", modifier = Modifier.padding(start = 8.dp))
        }

        Divider()

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(allIngredients) {ingredient ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable{onIngredientSelected(ingredient)}
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedIngredient == ingredient,
                        onClick = {onIngredientSelected(ingredient)}
                    )
                    Text(text = ingredient, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Button(
                onClick = onClear,
                modifier = Modifier

            ) {
                Text("Clear")
            }

            Button(
                onClick = onApply,
                modifier = Modifier
            ) {
                Text("Apply Filter")
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun IngredientScreen(
    ingredientName: String,
    cocktails: List<Cocktails>,
    onCocktailClick: (String) -> Unit,
    onBackClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text(ingredientName)},
                navigationIcon ={
                    IconButton(onClick = onBackClick){
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(paddingValues)
            ){
                item(span = { GridItemSpan(2) }){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        GlideImage(
                            model =
                                "https://www.thecocktaildb.com/images/ingredients/$ingredientName.png",
                            contentDescription = ingredientName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(bottom = 16.dp),
                            contentScale = ContentScale.Fit
                        )

                        Text(
                            text = "Cocktails with ${ingredientName}",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                items(cocktails){cocktail ->
                    CocktailCard(
                        cocktails = cocktail,
                        onClick = { onCocktailClick(cocktail.id) },
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope
                    )
                }
            }
        }
    }

@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, name: String?, photoUrl: String?) -> Unit
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuth = remember { GoogleAuthClient(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        GoogleSignInButton(
            onClick = {
                googleAuth.singIn(
                    coroutineScope = scope,
                    onSuccess = {email, name, photoUrl ->
                        Toast.makeText(context, "Login is complete: $email",
                            Toast.LENGTH_LONG).show()
                        onLoginSuccess(email, name, photoUrl)
                    },
                    onError = {error ->
                        Toast.makeText(context, "Error: $error",
                            Toast.LENGTH_LONG).show()
                    }
                )
            }
        )
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Continue with Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileScreen(
    viewModel: CocktailsViewModel,
    onSignOut: () -> Unit
){
    val userData = viewModel.user

    Column(
        modifier = Modifier
            .fillMaxSize()
           .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (userData?.profilePictureUrl != null){
            GlideImage(
                model = userData.profilePictureUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userData?.name ?: "User",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = userData?.email ?: "No email",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(onClick = { }) {
            Text("Favorite drinks")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.singOut()
                onSignOut()
            },
            colors = ButtonDefaults.buttonColors(containerColor =
            MaterialTheme.colorScheme.error)
        ) {
            Text("Log out of account")
        }
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



