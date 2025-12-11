package com.cs407.seefood.ui

import androidx.compose.runtime.Composable
import com.cs407.seefood.ui.navigation.SeeFoodNavHost

@Composable
fun SeeFoodApp(vm: SeeFoodViewModel) {
    SeeFoodNavHost(vm = vm)
}

