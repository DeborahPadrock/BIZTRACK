package com.example.biztrack

import org.junit.Assert.assertEquals
import org.junit.Test

class ReportCalculationsUnitTest {

    @Test
    fun report_calculatesFullProfitAndLossBreakdown() {
        val sales = listOf(
            Sale(quantitySold = 10, buyingPrice = 1000.0, sellingPrice = 1500.0, totalValue = 15000.0),
            Sale(quantitySold = 5, buyingPrice = 2000.0, sellingPrice = 2800.0, totalValue = 14000.0)
        )

        val expenses = listOf(
            Expense(amount = 4000.0),
            Expense(amount = 2000.0)
        )

        val totalSalesRevenue = sales.sumOf { it.totalValue } // 29,000
        val totalCostOfGoodsSold = sales.sumOf { it.buyingPrice * it.quantitySold } // (1000*10) + (2000*5) = 20,000
        val grossProfit = sales.sumOf { it.profit } // 29,000 - 20,000 = 9,000
        val totalExpenses = expenses.sumOf { it.amount } // 6,000
        val netProfit = grossProfit - totalExpenses // 9,000 - 6,000 = 3,000

        assertEquals(29000.0, totalSalesRevenue, 0.01)
        assertEquals(20000.0, totalCostOfGoodsSold, 0.01)
        assertEquals(9000.0, grossProfit, 0.01)
        assertEquals(6000.0, totalExpenses, 0.01)
        assertEquals(3000.0, netProfit, 0.01)
    }

    @Test
    fun report_calculatesNetMarginPercentage() {
        fun calculateNetMargin(revenue: Double, profit: Double): Double {
            return if (revenue > 0) (profit / revenue) * 100 else 0.0
        }

        assertEquals(20.0, calculateNetMargin(50000.0, 10000.0), 0.01)
        assertEquals(0.0, calculateNetMargin(0.0, 0.0), 0.01)
    }

    @Test
    fun report_countsSoldOutProductsCorrectly() {
        val products = listOf(
            Product(quantityPurchased = 10, remainingQuantity = 5, isSoldOut = false),
            Product(quantityPurchased = 10, remainingQuantity = 0, isSoldOut = true),
            Product(quantityPurchased = 20, remainingQuantity = 0, isSoldOut = true)
        )

        val soldOutCount = products.count { it.isSoldOut || it.currentRemaining <= 0 }

        assertEquals(2, soldOutCount)
    }
}
