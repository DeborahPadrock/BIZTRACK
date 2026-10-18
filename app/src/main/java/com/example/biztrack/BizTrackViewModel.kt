package com.example.biztrack

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BizTrackViewModel : ViewModel() {

    val products = mutableStateListOf<Product>()
    val sales = mutableStateListOf<Sale>()
    val expenses = mutableStateListOf<Expense>()

    init {
        // Initialize with realistic Malawi Kwacha business sample data
        val sampleProduct1 = Product(
            id = "p1",
            name = "Rice 25kg",
            category = "Groceries",
            buyingPrice = 35000.0,
            sellingPrice = 45000.0,
            quantityPurchased = 20,
            remainingQuantity = 12,
            orderDate = "2026-02-01"
        )
        val sampleProduct2 = Product(
            id = "p2",
            name = "Cooking Oil 5L",
            category = "Groceries",
            buyingPrice = 18000.0,
            sellingPrice = 24000.0,
            quantityPurchased = 15,
            remainingQuantity = 5,
            orderDate = "2026-02-05"
        )
        val sampleProduct3 = Product(
            id = "p3",
            name = "Sugar 2kg",
            category = "Groceries",
            buyingPrice = 3500.0,
            sellingPrice = 4800.0,
            quantityPurchased = 30,
            remainingQuantity = 0,
            orderDate = "2026-01-20",
            isSoldOut = true,
            soldOutDate = "2026-02-10"
        )
        val sampleProduct4 = Product(
            id = "p4",
            name = "Bread 500g",
            category = "Bakery",
            buyingPrice = 1200.0,
            sellingPrice = 1600.0,
            quantityPurchased = 50,
            remainingQuantity = 18,
            orderDate = "2026-02-15"
        )

        products.addAll(listOf(sampleProduct1, sampleProduct2, sampleProduct3, sampleProduct4))

        sales.addAll(
            listOf(
                Sale(
                    productId = "p1",
                    productName = "Rice 25kg",
                    quantitySold = 8,
                    sellingPrice = 45000.0,
                    buyingPrice = 35000.0,
                    date = "2026-02-10"
                ),
                Sale(
                    productId = "p2",
                    productName = "Cooking Oil 5L",
                    quantitySold = 10,
                    sellingPrice = 24000.0,
                    buyingPrice = 18000.0,
                    date = "2026-02-12"
                ),
                Sale(
                    productId = "p3",
                    productName = "Sugar 2kg",
                    quantitySold = 30,
                    sellingPrice = 4800.0,
                    buyingPrice = 3500.0,
                    date = "2026-02-10"
                ),
                Sale(
                    productId = "p4",
                    productName = "Bread 500g",
                    quantitySold = 32,
                    sellingPrice = 1600.0,
                    buyingPrice = 1200.0,
                    date = "2026-02-16"
                )
            )
        )

        expenses.addAll(
            listOf(
                Expense(
                    description = "Shop Rent",
                    amount = 250000.0,
                    date = "2026-02-01"
                ),
                Expense(
                    description = "Electricity Bill",
                    amount = 65000.0,
                    date = "2026-02-05"
                ),
                Expense(
                    description = "Transport & Logistics",
                    amount = 45000.0,
                    date = "2026-02-11"
                )
            )
        )
    }

    // FR-13 Metrics
    val totalProducts: Int
        get() = products.size

    val totalProductsSold: Int
        get() = sales.sumOf { it.quantitySold }

    val soldOutProductsCount: Int
        get() = products.count { it.isSoldOut }

    val totalSalesValue: Double
        get() = sales.sumOf { it.totalValue }

    val totalExpensesValue: Double
        get() = expenses.sumOf { it.amount }

    val grossProfit: Double
        get() = sales.sumOf { it.profit }

    val netProfit: Double
        get() = grossProfit - totalExpensesValue

    // Operations
    fun addProduct(
        name: String,
        category: String,
        buyingPrice: Double,
        sellingPrice: Double,
        quantity: Int,
        date: String = getCurrentDate()
    ) {
        val newProduct = Product(
            name = name,
            category = category,
            buyingPrice = buyingPrice,
            sellingPrice = sellingPrice,
            quantityPurchased = quantity,
            remainingQuantity = quantity,
            orderDate = date
        )
        products.add(newProduct)
    }

    fun deleteProduct(productId: String) {
        products.removeAll { it.id == productId }
    }

    // FR-08 & FR-09: Stock validation and automatic inventory reduction
    fun recordSale(productId: String, quantitySold: Int, date: String = getCurrentDate()): String? {
        val index = products.indexOfFirst { it.id == productId }
        if (index == -1) return "Product not found"

        val product = products[index]
        if (quantitySold > product.remainingQuantity) {
            return "Sale failed: Sold quantity ($quantitySold) exceeds available stock (${product.remainingQuantity})"
        }

        val updatedRemaining = product.remainingQuantity - quantitySold
        val isSoldOut = updatedRemaining <= 0
        val soldOutDate = if (isSoldOut) date else product.soldOutDate

        products[index] = product.copy(
            remainingQuantity = updatedRemaining,
            isSoldOut = isSoldOut,
            soldOutDate = soldOutDate
        )

        sales.add(
            0,
            Sale(
                productId = product.id,
                productName = product.name,
                quantitySold = quantitySold,
                sellingPrice = product.sellingPrice,
                buyingPrice = product.buyingPrice,
                date = date
            )
        )

        return null // Success
    }

    fun addExpense(description: String, amount: Double, date: String = getCurrentDate()) {
        expenses.add(
            0,
            Expense(
                description = description,
                amount = amount,
                date = date
            )
        )
    }

    fun deleteExpense(expenseId: String) {
        expenses.removeAll { it.id == expenseId }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
