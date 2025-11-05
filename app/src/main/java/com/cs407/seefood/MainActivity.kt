package com.cs407.seefood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.seefood.ui.SeeFoodApp
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.theme.SeeFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModel factory that provides Application to AndroidViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)

        setContent {
            SeeFoodTheme {
                // You can create it here and pass down,
                // or let each screen use viewModel(factory = factory)
                val vm: SeeFoodViewModel = viewModel(factory = factory)
                SeeFoodApp(vm)
            }
        }
    }
}
