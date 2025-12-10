package com.cs407.seefood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cs407.seefood.ui.SeeFoodApp
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.theme.SeeFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)

        setContent {
            val vm: SeeFoodViewModel = viewModel(factory = factory)
            val darkModeEnabled by vm.darkModeEnabled.collectAsState()

            SeeFoodTheme(darkTheme = darkModeEnabled) {
                SeeFoodApp(vm = vm)
            }
        }
    }
}
