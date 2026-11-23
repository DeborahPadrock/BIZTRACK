package com.example.biztrack

import android.content.Context
import android.util.Log
import org.json.JSONArray

class BizTrackLocalRepository(context: Context) {

    private val prefs = context.getSharedPreferences("biztrack_local_db", Context.MODE_PRIVATE)

    fun saveProducts(userId: String, products: List<Product>) {
        try {
            val array = JSONArray()
            products.forEach { array.put(it.toJsonObject()) }
            prefs.edit().putString("products_$userId", array.toString()).apply()
        } catch (e: Exception) {
            Log.e("BizTrackLocalRepo", "Error saving products locally for $userId", e)
        }
    }

    fun getProducts(userId: String): List<Product> {
        val json = prefs.getString("products_$userId", null) ?: return emptyList()
        val list = mutableListOf<Product>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                list.add(array.getJSONObject(i).toProductModel())
            }
        } catch (e: Exception) {
            Log.e("BizTrackLocalRepo", "Error loading products locally for $userId", e)
        }
        return list
    }

    fun saveSales(userId: String, sales: List<Sale>) {
        try {
            val array = JSONArray()
            sales.forEach { array.put(it.toJsonObject()) }
            prefs.edit().putString("sales_$userId", array.toString()).apply()
        } catch (e: Exception) {
            Log.e("BizTrackLocalRepo", "Error saving sales locally for $userId", e)
        }
    }

    fun getSales(userId: String): List<Sale> {
        val json = prefs.getString("sales_$userId", null) ?: return emptyList()
        val list = mutableListOf<Sale>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                list.add(array.getJSONObject(i).toSaleModel())
            }
        } catch (e: Exception) {
            Log.e("BizTrackLocalRepo", "Error loading sales locally for $userId", e)
        }
        return list
    }

    fun saveExpenses(userId: String, expenses: List<Expense>) {
        try {
            val array = JSONArray()
            expenses.forEach { array.put(it.toJsonObject()) }
            prefs.edit().putString("expenses_$userId", array.toString()).apply()
        } catch (e: Exception) {
            Log.e("BizTrackLocalRepo", "Error saving expenses locally for $userId", e)
        }
    }

    fun getExpenses(userId: String): List<Expense> {
        val json = prefs.getString("expenses_$userId", null) ?: return emptyList()
        val list = mutableListOf<Expense>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                list.add(array.getJSONObject(i).toExpenseModel())
            }
        } catch (e: Exception) {
            Log.e("BizTrackLocalRepo", "Error loading expenses locally for $userId", e)
        }
        return list
    }

    // Unsynced Queue Tracking
    fun markProductUnsynced(userId: String, productId: String) {
        val current = getUnsyncedProductIds(userId).toMutableSet()
        current.add(productId)
        prefs.edit().putStringSet("unsynced_products_$userId", current).apply()
    }

    fun getUnsyncedProductIds(userId: String): Set<String> {
        return prefs.getStringSet("unsynced_products_$userId", emptySet()) ?: emptySet()
    }

    fun clearProductUnsynced(userId: String, productId: String) {
        val current = getUnsyncedProductIds(userId).toMutableSet()
        current.remove(productId)
        prefs.edit().putStringSet("unsynced_products_$userId", current).apply()
    }

    fun markSaleUnsynced(userId: String, saleId: String) {
        val current = getUnsyncedSaleIds(userId).toMutableSet()
        current.add(saleId)
        prefs.edit().putStringSet("unsynced_sales_$userId", current).apply()
    }

    fun getUnsyncedSaleIds(userId: String): Set<String> {
        return prefs.getStringSet("unsynced_sales_$userId", emptySet()) ?: emptySet()
    }

    fun clearSaleUnsynced(userId: String, saleId: String) {
        val current = getUnsyncedSaleIds(userId).toMutableSet()
        current.remove(saleId)
        prefs.edit().putStringSet("unsynced_sales_$userId", current).apply()
    }

    fun markExpenseUnsynced(userId: String, expenseId: String) {
        val current = getUnsyncedExpenseIds(userId).toMutableSet()
        current.add(expenseId)
        prefs.edit().putStringSet("unsynced_expenses_$userId", current).apply()
    }

    fun getUnsyncedExpenseIds(userId: String): Set<String> {
        return prefs.getStringSet("unsynced_expenses_$userId", emptySet()) ?: emptySet()
    }

    fun clearExpenseUnsynced(userId: String, expenseId: String) {
        val current = getUnsyncedExpenseIds(userId).toMutableSet()
        current.remove(expenseId)
        prefs.edit().putStringSet("unsynced_expenses_$userId", current).apply()
    }
}
