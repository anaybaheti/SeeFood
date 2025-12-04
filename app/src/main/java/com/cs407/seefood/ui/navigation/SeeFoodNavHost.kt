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
        composable(route = NavRoutes.Login) {
            LoginScreen(
                vm = vm,
                onLoginSuccess = {
                    nav.navigate(NavRoutes.Home) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                },
                onSignupClick = {
                    nav.navigate(NavRoutes.Signup)
                }
            )
        }

        // SIGN UP
        composable(route = NavRoutes.Signup) {
            SignupScreen(
                vm = vm,
                onSignupSuccess = {
                    nav.navigate(NavRoutes.Home) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                },
                onLoginClick = {
                    nav.popBackStack() // back to Login
                }
            )
        }

        // HOME
        composable(route = NavRoutes.Home) {
            HomeScreen(
                firstName = vm.firstName.orEmpty(),   // <- make non-null
                onScan = { nav.navigate(NavRoutes.Scan) },
                onRecipes = { nav.navigate(NavRoutes.Recipes) },
                onNutrition = { nav.navigate(NavRoutes.Nutrition) },
                onProfile = { nav.navigate(NavRoutes.Profile) }
            )
        }

        // SCAN
        composable(route = NavRoutes.Scan) {
            ScanScreen(
                vm = vm,
                onConfirm = { nav.navigate(NavRoutes.Confirm) }
            )
        }

        // CONFIRM INGREDIENTS
        composable(route = NavRoutes.Confirm) {
            ConfirmIngredientsScreen(
                vm = vm,
                onDone = { nav.navigate(NavRoutes.Recipes) }
            )
        }

        // RECIPES
        composable(route = NavRoutes.Recipes) {
            RecipesScreen(vm = vm)
        }

        // NUTRITION
        composable(route = NavRoutes.Nutrition) {
            NutritionScreen()
        }

        // PROFILE
        composable(route = NavRoutes.Profile) {
            ProfileScreen(
                firstName = vm.firstName.orEmpty(),   // <- make non-null
                lastName = vm.lastName.orEmpty(),
                email = vm.email.orEmpty(),
                onLogout = {
                    nav.navigate(NavRoutes.Login) {
                        popUpTo(NavRoutes.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}

