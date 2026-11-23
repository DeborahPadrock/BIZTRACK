package com.example.biztrack

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: String = "",
    val buyingPrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val quantityPurchased: Int = 0,
    val remainingQuantity: Int = 0,
    val orderDate: String = "",
    @get:PropertyName("isSoldOut")
    @set:PropertyName("isSoldOut")
    @field:PropertyName("isSoldOut")
    var isSoldOut: Boolean = false,
    val soldOutDate: String? = null
) {
    @get:Exclude
    val currentRemaining: Int
        get() = remainingQuantity.coerceAtLeast(0)

    @get:Exclude
    val totalCost: Double
        get() = buyingPrice * quantityPurchased

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "category" to category,
        "buyingPrice" to buyingPrice,
        "sellingPrice" to sellingPrice,
        "quantityPurchased" to quantityPurchased,
        "remainingQuantity" to remainingQuantity,
        "orderDate" to orderDate,
        "isSoldOut" to isSoldOut,
        "soldOutDate" to soldOutDate
    )

    fun toJsonObject(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("category", category)
        put("buyingPrice", buyingPrice)
        put("sellingPrice", sellingPrice)
        put("quantityPurchased", quantityPurchased)
        put("remainingQuantity", remainingQuantity)
        put("orderDate", orderDate)
        put("isSoldOut", isSoldOut)
        put("soldOutDate", soldOutDate ?: "")
    }
}

fun JSONObject.toProductModel(): Product = Product(
    id = optString("id", UUID.randomUUID().toString()),
    name = optString("name", ""),
    category = optString("category", ""),
    buyingPrice = optDouble("buyingPrice", 0.0),
    sellingPrice = optDouble("sellingPrice", 0.0),
    quantityPurchased = optInt("quantityPurchased", 0),
    remainingQuantity = optInt("remainingQuantity", 0),
    orderDate = optString("orderDate", ""),
    isSoldOut = optBoolean("isSoldOut", false),
    soldOutDate = optString("soldOutDate").ifBlank { null }
)

data class Sale(
    val id: String = UUID.randomUUID().toString(),
    val productId: String = "",
    val productName: String = "",
    val quantitySold: Int = 0,
    val sellingPrice: Double = 0.0,
    val buyingPrice: Double = 0.0,
    val totalValue: Double = sellingPrice * quantitySold,
    val date: String = ""
) {
    @get:Exclude
    val profit: Double
        get() = totalValue - (buyingPrice * quantitySold)

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "productId" to productId,
        "productName" to productName,
        "quantitySold" to quantitySold,
        "sellingPrice" to sellingPrice,
        "buyingPrice" to buyingPrice,
        "totalValue" to totalValue,
        "date" to date
    )

    fun toJsonObject(): JSONObject = JSONObject().apply {
        put("id", id)
        put("productId", productId)
        put("productName", productName)
        put("quantitySold", quantitySold)
        put("sellingPrice", sellingPrice)
        put("buyingPrice", buyingPrice)
        put("totalValue", totalValue)
        put("date", date)
    }
}

fun JSONObject.toSaleModel(): Sale = Sale(
    id = optString("id", UUID.randomUUID().toString()),
    productId = optString("productId", ""),
    productName = optString("productName", ""),
    quantitySold = optInt("quantitySold", 0),
    sellingPrice = optDouble("sellingPrice", 0.0),
    buyingPrice = optDouble("buyingPrice", 0.0),
    totalValue = optDouble("totalValue", 0.0),
    date = optString("date", "")
)

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val description: String = "",
    val amount: Double = 0.0,
    val date: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "description" to description,
        "amount" to amount,
        "date" to date
    )

    fun toJsonObject(): JSONObject = JSONObject().apply {
        put("id", id)
        put("description", description)
        put("amount", amount)
        put("date", date)
    }
}

fun JSONObject.toExpenseModel(): Expense = Expense(
    id = optString("id", UUID.randomUUID().toString()),
    description = optString("description", ""),
    amount = optDouble("amount", 0.0),
    date = optString("date", "")
)

fun DocumentSnapshot.getDoubleAsAny(field: String): Double {
    val raw = get(field)
    return when (raw) {
        is Number -> raw.toDouble()
        is String -> raw.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }
}

fun DocumentSnapshot.getIntAsAny(field: String): Int {
    val raw = get(field)
    return when (raw) {
        is Number -> raw.toInt()
        is String -> raw.toIntOrNull() ?: 0
        else -> 0
    }
}

fun DocumentSnapshot.toProduct(): Product? {
    if (!exists()) return null
    return try {
        val docId = id
        val name = getString("name") ?: ""
        val category = getString("category") ?: ""
        val buyingPrice = getDoubleAsAny("buyingPrice")
        val sellingPrice = getDoubleAsAny("sellingPrice")
        val quantityPurchased = getIntAsAny("quantityPurchased")
        val remainingQuantity = getIntAsAny("remainingQuantity")
        val orderDate = getString("orderDate") ?: ""
        val isSoldOut = getBoolean("isSoldOut") ?: getBoolean("soldOut") ?: (remainingQuantity <= 0)
        val soldOutDate = getString("soldOutDate")
        Product(
            id = docId,
            name = name,
            category = category,
            buyingPrice = buyingPrice,
            sellingPrice = sellingPrice,
            quantityPurchased = quantityPurchased,
            remainingQuantity = remainingQuantity,
            orderDate = orderDate,
            isSoldOut = isSoldOut,
            soldOutDate = soldOutDate
        )
    } catch (e: Exception) {
        Log.e("BizTrackModels", "Error converting document $id to Product: ${e.message}", e)
        null
    }
}

fun DocumentSnapshot.toSale(): Sale? {
    if (!exists()) return null
    return try {
        val docId = id
        val productId = getString("productId") ?: ""
        val productName = getString("productName") ?: ""
        val quantitySold = getIntAsAny("quantitySold")
        val sellingPrice = getDoubleAsAny("sellingPrice")
        val buyingPrice = getDoubleAsAny("buyingPrice")
        val totalValue = getDoubleAsAny("totalValue").let { if (it > 0) it else sellingPrice * quantitySold }
        val date = getString("date") ?: ""
        Sale(
            id = docId,
            productId = productId,
            productName = productName,
            quantitySold = quantitySold,
            sellingPrice = sellingPrice,
            buyingPrice = buyingPrice,
            totalValue = totalValue,
            date = date
        )
    } catch (e: Exception) {
        Log.e("BizTrackModels", "Error converting document $id to Sale: ${e.message}", e)
        null
    }
}

fun DocumentSnapshot.toExpense(): Expense? {
    if (!exists()) return null
    return try {
        val docId = id
        val description = getString("description") ?: ""
        val amount = getDoubleAsAny("amount")
        val date = getString("date") ?: ""
        Expense(
            id = docId,
            description = description,
            amount = amount,
            date = date
        )
    } catch (e: Exception) {
        Log.e("BizTrackModels", "Error converting document $id to Expense: ${e.message}", e)
        null
    }
}
