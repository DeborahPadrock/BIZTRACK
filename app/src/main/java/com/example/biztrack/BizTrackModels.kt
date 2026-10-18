package com.example.biztrack

import java.util.UUID

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val buyingPrice: Double,
    val sellingPrice: Double,
    val quantityPurchased: Int,
    val remainingQuantity: Int = quantityPurchased,
    val orderDate: String,
    val isSoldOut: Boolean = remainingQuantity <= 0,
    val soldOutDate: String? = null
) {
    val totalCost: Double
        get() = buyingPrice * quantityPurchased
}

data class Sale(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val productName: String,
    val quantitySold: Int,
    val sellingPrice: Double,
    val buyingPrice: Double,
    val totalValue: Double = sellingPrice * quantitySold,
    val date: String
) {
    val profit: Double
        get() = totalValue - (buyingPrice * quantitySold)
}

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val amount: Double,
    val date: String
)
