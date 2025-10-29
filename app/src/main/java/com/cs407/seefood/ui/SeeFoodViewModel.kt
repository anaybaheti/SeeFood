package com.cs407.seefood.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.cs407.seefood.data.SeeFoodRepository

class SeeFoodViewModel(
    private val repo: SeeFoodRepository = SeeFoodRepository()
) : ViewModel() {
    private val _ingredients = MutableStateFlow<List<String>>(emptyList())
    val ingredients = _ingredients.asStateFlow()

    fun loadIngredients() = viewModelScope.launch {
        runCatching { repo.fetchIngredients() }
            .onSuccess { _ingredients.value = it }
    }
}


