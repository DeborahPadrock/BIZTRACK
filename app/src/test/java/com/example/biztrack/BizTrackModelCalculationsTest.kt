package com.example.biztrack

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BizTrackModelCalculationsTest {

    @Test
    fun product_calculatesTotalCostAndRemainingQuantity() {
        val product = Product(
            name = "Sugar",
            category = "Groceries",
            buyingPrice = 1200.0,
            sellingPrice = 1500.0,
            quantityPurchased = 10,
            remainingQuantity = 10,
            isSoldOut = false
        )

        assertEquals(12000.0, product.totalCost, 0.01)
        assertEquals(10, product.currentRemaining)
        assertFalse(product.isSoldOut)
    }

    @Test
    fun product_stockReducesWhenRemainingQuantityDecreases() {
        val initialProduct = Product(
            name = "Oil",
            category = "Groceries",
            buyingPrice = 3000.0,
            sellingPrice = 3800.0,
            quantityPurchased = 10,
            remainingQuantity = 10,
            isSoldOut = false
        )

        // After recording a sale of 4 units
        val updatedProduct = initialProduct.copy(
            remainingQuantity = 6,
            isSoldOut = false
        )

        assertEquals(6, updatedProduct.currentRemaining)

        // After recording another sale of 6 units (reaches 0 stock)
        val soldOutProduct = updatedProduct.copy(
            remainingQuantity = 0,
            isSoldOut = true
        )

        assertEquals(0, soldOutProduct.currentRemaining)
    }

    @Test
    fun sale_calculatesTotalValueAndProfitCorrectly() {
        val sale = Sale(
            productId = "prod1",
            productName = "Rice",
            quantitySold = 5,
            buyingPrice = 2000.0,
            sellingPrice = 2500.0,
            totalValue = 5 * 2500.0,
            date = "2025-01-01"
        )

        assertEquals(12500.0, sale.totalValue, 0.01)
        // Profit = totalValue - (buyingPrice * qty) = 12500 - (2000 * 5) = 2500
        assertEquals(2500.0, sale.profit, 0.01)
    }

    @Test
    fun expense_storesAmountCorrectly() {
        val expense = Expense(
            description = "Store Rent",
            amount = 50000.0,
            date = "2025-01-01"
        )

        assertEquals("Store Rent", expense.description)
        assertEquals(50000.0, expense.amount, 0.01)
    }
}
