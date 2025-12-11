package com.cs407.seefood.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.seefood.R
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.screens.ConfirmIngredientsScreen
import com.cs407.seefood.ui.screens.HomeScreen
import com.cs407.seefood.ui.screens.LoginScreen
import com.cs407.seefood.ui.screens.NutritionScreen
import com.cs407.seefood.ui.screens.ProfileScreen
import com.cs407.seefood.ui.screens.RecipesScreen
import com.cs407.seefood.ui.screens.SampleRecipeScreen
import com.cs407.seefood.ui.screens.ScanScreen
import com.cs407.seefood.ui.screens.SignupScreen
import com.cs407.seefood.ui.screens.SavedRecipesScreen

@Composable
fun SeeFoodNavHost(
    vm: SeeFoodViewModel,
    startDestination: String = NavRoutes.Login
) {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = startDestination
    ) {

        // LOGIN
        composable(NavRoutes.Login) {
            LoginScreen(
                vm = vm,
                onLoginSuccess = {
                    nav.navigate(NavRoutes.Home) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                },
                onGoToSignup = {
                    nav.navigate(NavRoutes.Signup)
                }
            )
        }

        // SIGNUP
        composable(NavRoutes.Signup) {
            SignupScreen(
                vm = vm,
                onSignupSuccess = {
                    nav.navigate(NavRoutes.Home) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                },
                onBackToLogin = { nav.popBackStack() }
            )
        }

        // HOME
        composable(route = NavRoutes.Home) {
            HomeScreen(
                firstName = vm.firstName.orEmpty(),
                onScan = { nav.navigate(NavRoutes.Scan) },
                // Recipes tab always shows the Saved Recipes screen
                onRecipes = { nav.navigate(NavRoutes.SavedRecipes) },
                onNutrition = { nav.navigate(NavRoutes.Nutrition) },
                onProfile = { nav.navigate(NavRoutes.Profile) },
                onOmelette = { nav.navigate(NavRoutes.Omelette) },
                onPancakes = { nav.navigate(NavRoutes.Pancakes) },
                onCaprese = { nav.navigate(NavRoutes.Caprese) },
                onPasta = { nav.navigate(NavRoutes.Pasta) }
            )
        }


        // OMELETTE
        composable(route = NavRoutes.Omelette) {
            SampleRecipeScreen(
                title = "Omelette",
                imageRes = R.drawable.omelette,
                ingredients = listOf(
                    "3 eggs",
                    "Salt & pepper",
                    "1 tbsp butter",
                    "Cheese (optional)",
                    "Veggies (optional)"
                ),
                steps = listOf(
                    "Whisk eggs with salt and pepper.",
                    "Heat butter in a nonstick pan over medium heat.",
                    "Pour in eggs and cook until edges start to set.",
                    "Add cheese/veggies, fold the omelette, and cook 1–2 more minutes.",
                    "Slide onto a plate and serve warm."
                ),
                onBack = { nav.popBackStack() }
            )
        }

        // PANCAKES
        composable(route = NavRoutes.Pancakes) {
            SampleRecipeScreen(
                title = "Pancakes",
                imageRes = R.drawable.pancakes,
                ingredients = listOf(
                    "1 cup flour",
                    "1 tbsp sugar",
                    "1 tsp baking powder",
                    "Pinch of salt",
                    "1 egg",
                    "1 cup milk",
                    "1 tbsp melted butter"
                ),
                steps = listOf(
                    "Mix flour, sugar, baking powder, and salt in a bowl.",
                    "Whisk in egg, milk, and melted butter until smooth.",
                    "Heat a lightly oiled pan over medium heat.",
                    "Pour 1/4 cup batter and cook until bubbles form on top.",
                    "Flip and cook until golden on the other side. Serve with syrup."
                ),
                onBack = { nav.popBackStack() }
            )
        }

        // CAPRESE
        composable(route = NavRoutes.Caprese) {
            SampleRecipeScreen(
                title = "Caprese Salad",
                imageRes = R.drawable.caprese,
                ingredients = listOf(
                    "2 tomatoes",
                    "Fresh mozzarella",
                    "Fresh basil leaves",
                    "2 tbsp olive oil",
                    "Salt & pepper",
                    "Balsamic glaze (optional)"
                ),
                steps = listOf(
                    "Slice tomatoes and mozzarella into even rounds.",
                    "Layer tomato, mozzarella, and basil leaves on a plate.",
                    "Drizzle with olive oil (and balsamic if using).",
                    "Season with salt and pepper to taste.",
                    "Serve immediately."
                ),
                onBack = { nav.popBackStack() }
            )
        }

        // PASTA
        composable(route = NavRoutes.Pasta) {
            SampleRecipeScreen(
                title = "Simple Tomato Pasta",
                imageRes = R.drawable.pasta,
                ingredients = listOf(
                    "200 g pasta",
                    "2 tbsp olive oil",
                    "2 cloves garlic",
                    "1 cup tomato sauce",
                    "Salt & pepper",
                    "Parmesan cheese"
                ),
                steps = listOf(
                    "Cook pasta in salted boiling water until al dente. Reserve a bit of pasta water.",
                    "In a pan, heat olive oil and sauté minced garlic until fragrant.",
                    "Add tomato sauce, salt, and pepper; simmer for a few minutes.",
                    "Toss cooked pasta with the sauce, adding a splash of pasta water if needed.",
                    "Top with grated parmesan and serve."
                ),
                onBack = { nav.popBackStack() }
            )
        }


        // SCAN
        composable(route = NavRoutes.Scan) {
            ScanScreen(
                vm = vm,
                onConfirm = { nav.navigate(NavRoutes.Confirm) },
                onBack = { nav.navigate(NavRoutes.Home) }
            )
        }

        // CONFIRM INGREDIENTS
        composable(route = NavRoutes.Confirm) {
            ConfirmIngredientsScreen(
                vm = vm,
                onDone = { nav.navigate(NavRoutes.Recipes) }
            )
        }

        // RECIPES (suggested recipes from scan)
        composable(route = NavRoutes.Recipes) {
            RecipesScreen(
                vm = vm,
                onHome = { nav.navigate(NavRoutes.Home) },
                onRecipes = { nav.navigate(NavRoutes.SavedRecipes) },
                onNutrition = { nav.navigate(NavRoutes.Nutrition) },
                onProfile = { nav.navigate(NavRoutes.Profile) }
            )
        }

        // SAVED RECIPES (tab from Home → Recipes)
        composable(route = NavRoutes.SavedRecipes) {
            SavedRecipesScreen(
                vm = vm,
                onHome = { nav.navigate(NavRoutes.Home) },
                onRecipes = { nav.navigate(NavRoutes.SavedRecipes) },
                onNutrition = { nav.navigate(NavRoutes.Nutrition) },
                onProfile = { nav.navigate(NavRoutes.Profile) }
            )
        }

        // NUTRITION
        composable(route = NavRoutes.Nutrition) {
            NutritionScreen(
                vm = vm,
                onHome = { nav.navigate(NavRoutes.Home) },
                onRecipes = { nav.navigate(NavRoutes.SavedRecipes) },
                onNutrition = { nav.navigate(NavRoutes.Nutrition) },
                onProfile = { nav.navigate(NavRoutes.Profile) }
            )
        }

        // PROFILE
        composable(route = NavRoutes.Profile) {
            ProfileScreen(
                vm = vm,
                firstName = vm.firstName.orEmpty(),
                lastName = vm.lastName.orEmpty(),
                email = vm.email.orEmpty(),
                onLogout = {
                    nav.navigate(NavRoutes.Login) {
                        popUpTo(NavRoutes.Home) { inclusive = true }
                    }
                    vm.clearUserProfile()
                },
                onHome = { nav.navigate(NavRoutes.Home) },
                onRecipes = { nav.navigate(NavRoutes.SavedRecipes) },
                onNutrition = { nav.navigate(NavRoutes.Nutrition) },
                onProfile = { nav.navigate(NavRoutes.Profile) }
            )
        }
    }
}
