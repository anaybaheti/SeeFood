package com.cs407.seefood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.seefood.ui.SeeFoodApp
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.navigation.SeeFoodNavHost
import com.cs407.seefood.ui.theme.SeeFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)

        setContent {
            SeeFoodTheme {
                // Create ONE app-scoped VM here…
                val vm: SeeFoodViewModel = viewModel(factory = factory)
                // …and pass it down
                SeeFoodApp(vm = vm)
            }
        }
    }
}
