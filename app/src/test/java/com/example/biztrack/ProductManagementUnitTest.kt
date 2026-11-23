package com.example.biztrack

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductManagementUnitTest {

    @Test
    fun product_filtersByNameAndCategory() {
        val products = listOf(
            Product(name = "Sugar", category = "Groceries"),
            Product(name = "Salt", category = "Groceries"),
            Product(name = "T-Shirt", category = "Clothing"),
            Product(name = "Jeans", category = "Clothing")
        )

        fun filterProducts(query: String): List<Product> {
            return products.filter {
                it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
            }
        }

        assertEquals(2, filterProducts("groceries").size)
        assertEquals(2, filterProducts("clothing").size)
        assertEquals(1, filterProducts("sugar").size)
        assertEquals(0, filterProducts("electronics").size)
    }

    @Test
    fun product_identifiesOutOfStockStatus() {
        val inStockProduct = Product(
            name = "In Stock Item",
            quantityPurchased = 10,
            remainingQuantity = 5,
            isSoldOut = false
        )

        val soldOutProduct = Product(
            name = "Sold Out Item",
            quantityPurchased = 10,
            remainingQuantity = 0,
            isSoldOut = true
        )

        assertFalse(inStockProduct.isSoldOut || inStockProduct.currentRemaining <= 0)
        assertTrue(soldOutProduct.isSoldOut || soldOutProduct.currentRemaining <= 0)
    }

    @Test
    fun product_calculatesMarginAndProfitPerUnit() {
        val product = Product(
            buyingPrice = 1000.0,
            sellingPrice = 1500.0
        )

        val profitPerUnit = product.sellingPrice - product.buyingPrice
        val profitMarginPercentage = (profitPerUnit / product.sellingPrice) * 100

        assertEquals(500.0, profitPerUnit, 0.01)
        assertEquals(33.33, profitMarginPercentage, 0.01)
    }

    @Test
    fun product_updatesStockCorrectly() {
        val oldProduct = Product(
            buyingPrice = 1000.0,
            sellingPrice = 1500.0,
            quantityPurchased = 10,
            remainingQuantity = 10,
            isSoldOut = false
        )

        // Simulate restocking (purchasing 5 more)
        val restockedQty = 15
        val diffQty = restockedQty - oldProduct.quantityPurchased
        val newRemaining = (oldProduct.currentRemaining + diffQty).coerceIn(0, restockedQty)
        val updatedProduct = oldProduct.copy(
            quantityPurchased = restockedQty,
            remainingQuantity = newRemaining,
            isSoldOut = newRemaining <= 0
        )

        assertEquals(15, updatedProduct.quantityPurchased)
        assertEquals(15, updatedProduct.currentRemaining)
        assertFalse(updatedProduct.isSoldOut)
    }
}
