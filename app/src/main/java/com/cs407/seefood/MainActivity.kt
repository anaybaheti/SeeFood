package com.cs407.seefood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.cs407.seefood.ui.navigation.SeeFoodNavHost
import com.cs407.seefood.ui.theme.SeeFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeeFoodTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SeeFoodNavHost()
                }
            }
        }
    }
}
