package com.example.cocktailapp.data


data class Cocktails(
    val id: String,
    val name: String,
    val imgSrc: String,
)

data class CocktailDetails(
    val id: String,
    val name: String,
    val imgSrc: String,
    val instructions: String,
    val category: String,
    val glass: String,
    val type: String,

    val ingredients: List<Pair<String, String>>
)

fun CocktailApiCocktails.toCocktails(): Cocktails{
    return Cocktails(
        id = idDrink,
        name = strDrink,
        imgSrc = strDrinkThumb
    )
}

fun CocktailApiCocktails.toDetails(): CocktailDetails{
    val ingredientsList = mutableListOf<Pair<String, String>>()

    if (!strIngredient1.isNullOrEmpty()) ingredientsList.add(strIngredient1 to
            (strMeasure1 ?: ""))
    if (!strIngredient2.isNullOrEmpty()) ingredientsList.add(strIngredient2 to
            (strMeasure2 ?: ""))
    if (!strIngredient3.isNullOrEmpty()) ingredientsList.add(strIngredient3 to
            (strMeasure3 ?: ""))
    if (!strIngredient4.isNullOrEmpty()) ingredientsList.add(strIngredient4 to
            (strMeasure4 ?: ""))
    if (!strIngredient5.isNullOrEmpty()) ingredientsList.add(strIngredient5 to
            (strMeasure5 ?: ""))
    if (!strIngredient6.isNullOrEmpty()) ingredientsList.add(strIngredient6 to
            (strMeasure6 ?: ""))
    if (!strIngredient7.isNullOrEmpty()) ingredientsList.add(strIngredient7 to
            (strMeasure7 ?: ""))
    if (!strIngredient8.isNullOrEmpty()) ingredientsList.add(strIngredient8 to
            (strMeasure8 ?: ""))
    if (!strIngredient9.isNullOrEmpty()) ingredientsList.add(strIngredient9 to
            (strMeasure9 ?: ""))
    if (!strIngredient10.isNullOrEmpty()) ingredientsList.add(strIngredient10 to
            (strMeasure10 ?: ""))
    if (!strIngredient11.isNullOrEmpty()) ingredientsList.add(strIngredient11 to
            (strMeasure11 ?: ""))
    if (!strIngredient12.isNullOrEmpty()) ingredientsList.add(strIngredient12 to
            (strMeasure12 ?: ""))
    if (!strIngredient13.isNullOrEmpty()) ingredientsList.add(strIngredient13 to
            (strMeasure13 ?: ""))
    if (!strIngredient14.isNullOrEmpty()) ingredientsList.add(strIngredient14 to
            (strMeasure14 ?: ""))
    if (!strIngredient15.isNullOrEmpty()) ingredientsList.add(strIngredient15 to
            (strMeasure15 ?: ""))

    return CocktailDetails(
        id = idDrink,
        name = strDrink,
        imgSrc = strDrinkThumb,
        instructions = strInstructions ?: "No instructions available",
        category = strCategory ?: "Unknow",
        glass = strGlass ?: "Unknow",
        type = strAlcoholic ?: "Yes",
        ingredients = ingredientsList
    )
}