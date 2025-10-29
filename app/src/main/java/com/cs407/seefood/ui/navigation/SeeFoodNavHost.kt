package com.cs407.seefood.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.seefood.ui.screens.*

@Composable
fun SeeFoodNavHost(startDestination: String = NavRoutes.Login) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = startDestination) {

        composable(NavRoutes.Login) {
            LoginScreen(onLogin = { nav.navigate(NavRoutes.Home) { popUpTo(NavRoutes.Login) { inclusive = true } } })
        }

        composable(NavRoutes.Home) { HomeScreen(
            onScan = { nav.navigate(NavRoutes.Scan) },
            onRecipes = { nav.navigate(NavRoutes.Recipes) },
            onNutrition = { nav.navigate(NavRoutes.Nutrition) },
            onProfile = { nav.navigate(NavRoutes.Profile) }
        ) }

        composable(NavRoutes.Scan) { ScanScreen(onConfirm = { nav.navigate(NavRoutes.Confirm) }) }
        composable(NavRoutes.Confirm) { ConfirmIngredientsScreen(onDone = { nav.navigate(NavRoutes.Recipes) }) }
        composable(NavRoutes.Recipes) { RecipesScreen() }
        composable(NavRoutes.Nutrition) { NutritionScreen() }
        composable(NavRoutes.Profile) { ProfileScreen() }
    }
}
