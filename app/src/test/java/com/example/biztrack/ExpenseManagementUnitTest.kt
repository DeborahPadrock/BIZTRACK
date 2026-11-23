package com.example.biztrack

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseManagementUnitTest {

    @Test
    fun expense_calculatesTotalExpensesValue() {
        val expensesList = listOf(
            Expense(description = "Rent", amount = 50000.0),
            Expense(description = "Electricity", amount = 12000.0),
            Expense(description = "Transport", amount = 8500.0)
        )

        val totalExpenses = expensesList.sumOf { it.amount }

        assertEquals(70500.0, totalExpenses, 0.01)
    }

    @Test
    fun expense_validatesRequiredFields() {
        fun validateExpense(description: String, amountText: String): String? {
            val amount = amountText.toDoubleOrNull()
            if (description.isBlank() || amountText.isBlank() || amount == null || amount <= 0) {
                return "Please enter a valid description and positive amount"
            }
            return null
        }

        assertEquals("Please enter a valid description and positive amount", validateExpense("", "5000"))
        assertEquals("Please enter a valid description and positive amount", validateExpense("Rent", ""))
        assertEquals("Please enter a valid description and positive amount", validateExpense("Rent", "0"))
        assertEquals("Please enter a valid description and positive amount", validateExpense("Rent", "-500"))
        assertEquals("Please enter a valid description and positive amount", validateExpense("Rent", "abc"))
        assertEquals(null, validateExpense("Rent", "50000"))
    }

    @Test
    fun expense_deductsFromGrossProfitToComputeNetProfit() {
        val grossProfit = 120000.0
        val totalExpenses = 45000.0
        val netProfit = grossProfit - totalExpenses

        assertEquals(75000.0, netProfit, 0.01)
    }
}
