package com.yuvrajsinghgmx.shopsmart.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

data class Product(val id: String = UUID.randomUUID().toString(), val name: String, val amount: Double, var no_of_items: Int, val imageUrl: String? = null, val dateAdded: Long = System.currentTimeMillis())
object ShoppingList{
    val ITEMS_KEY = stringPreferencesKey("items")
}

suspend fun saveItems(context: Context, items: List<com.yuvrajsinghgmx.shopsmart.datastore.Product>) {
    context.dataStore.edit { preferences ->
        val jsonString = Gson().toJson(items)
        preferences[ShoppingList.ITEMS_KEY] = jsonString
    }
}

fun getItems(context: Context): Flow<List<Product>> = context.dataStore.data.map { preferences ->
    val json = preferences[ShoppingList.ITEMS_KEY] ?: return@map emptyList()
    Gson().fromJson(json, Array<Product>::class.java).map { product ->
        if (product.id.isNullOrBlank()) {
            product.copy(id = UUID.randomUUID().toString()) // Assign a new ID
        } else {
            product
        }
    }
}
