package com.cs407.seefood.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.screens.ConfirmIngredientsScreen
import com.cs407.seefood.ui.screens.HomeScreen
import com.cs407.seefood.ui.screens.LoginScreen
import com.cs407.seefood.ui.screens.NutritionScreen
import com.cs407.seefood.ui.screens.ProfileScreen
import com.cs407.seefood.ui.screens.RecipesScreen
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
                onProfile = { nav.navigate(NavRoutes.Profile) }
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
