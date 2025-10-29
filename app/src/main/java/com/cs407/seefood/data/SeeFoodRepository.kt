package com.cs407.seefood.data

import com.cs407.seefood.network.Network

class SeeFoodRepository {
    suspend fun fetchIngredients() = Network.api.getIngredients().ingredients
}


