package com.example.biztrack

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SalesManagementUnitTest {

    @Test
    fun sales_filtersAvailableProductsForDropdown() {
        val products = listOf(
            Product(id = "1", name = "In Stock Product 1", quantityPurchased = 10, remainingQuantity = 5, isSoldOut = false),
            Product(id = "2", name = "Sold Out Product", quantityPurchased = 10, remainingQuantity = 0, isSoldOut = true),
            Product(id = "3", name = "In Stock Product 2", quantityPurchased = 20, remainingQuantity = 12, isSoldOut = false)
        )

        val availableProducts = products.filter { !it.isSoldOut && it.currentRemaining > 0 }

        assertEquals(2, availableProducts.size)
        assertTrue(availableProducts.none { it.id == "2" })
    }

    @Test
    fun sales_calculatesTotalRevenueAndItemsSold() {
        val salesList = listOf(
            Sale(quantitySold = 3, sellingPrice = 1500.0, totalValue = 4500.0),
            Sale(quantitySold = 2, sellingPrice = 3000.0, totalValue = 6000.0),
            Sale(quantitySold = 5, sellingPrice = 1000.0, totalValue = 5000.0)
        )

        val totalRevenue = salesList.sumOf { it.totalValue }
        val totalItemsSold = salesList.sumOf { it.quantitySold }

        assertEquals(15500.0, totalRevenue, 0.01)
        assertEquals(10, totalItemsSold)
    }

    @Test
    fun sales_validatesQuantityAgainstAvailableStock() {
        fun validateSale(requestedQty: Int, availableStock: Int): String? {
            if (requestedQty <= 0) return "Quantity must be greater than 0"
            if (requestedQty > availableStock) return "Quantity exceeds available stock"
            return null
        }

        assertEquals("Quantity exceeds available stock", validateSale(12, 10))
        assertEquals("Quantity must be greater than 0", validateSale(0, 10))
        assertEquals(null, validateSale(5, 10))
    }

    @Test
    fun sales_calculatesProfitPerSaleCorrectly() {
        val sale = Sale(
            quantitySold = 4,
            buyingPrice = 1000.0,
            sellingPrice = 1600.0,
            totalValue = 4 * 1600.0
        )

        // Total Revenue = 6400, Total COGS = 4000, Profit = 2400
        assertEquals(6400.0, sale.totalValue, 0.01)
        assertEquals(2400.0, sale.profit, 0.01)
    }
}
