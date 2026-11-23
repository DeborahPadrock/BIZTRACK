package com.example.biztrack

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BizTrackViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val localRepo: BizTrackLocalRepository by lazy {
        BizTrackLocalRepository(FirebaseApp.getInstance().applicationContext)
    }

    val products = mutableStateListOf<Product>()
    val sales = mutableStateListOf<Sale>()
    val expenses = mutableStateListOf<Expense>()

    var currentUsername by mutableStateOf("")
        private set

    var currentUserEmail by mutableStateOf("")
        private set

    private var productsListener: ListenerRegistration? = null
    private var salesListener: ListenerRegistration? = null
    private var expensesListener: ListenerRegistration? = null
    private var userProfileListener: ListenerRegistration? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    private var currentLoadedUserId: String? = null

    val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        // Add auth state listener to automatically react to user login / logout / switch
        authStateListener = FirebaseAuth.AuthStateListener {
            loadUserData()
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    fun loadUserData() {
        val userId = currentUserId
        if (userId == null) {
            currentLoadedUserId = null
            detachListeners()
            clearLocalData()
            return
        }

        // Instantly load local disk cache for instant display
        val localProducts = localRepo.getProducts(userId)
        if (localProducts.isNotEmpty()) {
            products.clear()
            products.addAll(localProducts)
        }

        val localSales = localRepo.getSales(userId)
        if (localSales.isNotEmpty()) {
            sales.clear()
            sales.addAll(localSales)
        }

        val localExpenses = localRepo.getExpenses(userId)
        if (localExpenses.isNotEmpty()) {
            expenses.clear()
            expenses.addAll(localExpenses)
        }

        // Prevent race condition teardown if already active for the current user
        if (userId == currentLoadedUserId && productsListener != null) {
            return
        }

        currentLoadedUserId = userId
        detachListeners()

        // 1. Listen to user profile document for username and email
        userProfileListener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    currentUserEmail = auth.currentUser?.email ?: ""
                    currentUsername = auth.currentUser?.email?.substringBefore("@") ?: ""
                    return@addSnapshotListener
                }
                val username = snapshot.getString("username") ?: ""
                val email = snapshot.getString("email") ?: (auth.currentUser?.email ?: "")
                currentUsername = if (username.isNotBlank()) username else (auth.currentUser?.email?.substringBefore("@") ?: "")
                currentUserEmail = email
            }

        // 2. Fetch & Listen to user-specific products collection
        productsListener = db.collection("users").document(userId).collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BizTrackViewModel", "Error fetching products listener: ${error.message}", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toProduct() }
                    products.clear()
                    products.addAll(list)
                    localRepo.saveProducts(userId, list)
                }
            }

        // 3. Fetch & Listen to user-specific sales collection
        salesListener = db.collection("users").document(userId).collection("sales")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BizTrackViewModel", "Error fetching sales listener: ${error.message}", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toSale() }
                    sales.clear()
                    sales.addAll(list.sortedByDescending { it.date })
                    localRepo.saveSales(userId, sales)
                }
            }

        // 4. Fetch & Listen to user-specific expenses collection
        expensesListener = db.collection("users").document(userId).collection("expenses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BizTrackViewModel", "Error fetching expenses listener: ${error.message}", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toExpense() }
                    expenses.clear()
                    expenses.addAll(list.sortedByDescending { it.date })
                    localRepo.saveExpenses(userId, expenses)
                }
            }
    }

    fun logout() {
        currentLoadedUserId = null
        detachListeners()
        clearLocalData()
        auth.signOut()
    }

    fun clearData() {
        currentLoadedUserId = null
        detachListeners()
        clearLocalData()
    }

    private fun detachListeners() {
        productsListener?.remove()
        salesListener?.remove()
        expensesListener?.remove()
        userProfileListener?.remove()

        productsListener = null
        salesListener = null
        expensesListener = null
        userProfileListener = null
    }

    private fun clearLocalData() {
        products.clear()
        sales.clear()
        expenses.clear()
        currentUsername = ""
        currentUserEmail = ""
    }

    override fun onCleared() {
        super.onCleared()
        detachListeners()
        authStateListener?.let { auth.removeAuthStateListener(it) }
    }

    // Metrics
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

    // Email Validation Helper
    fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return "Please enter email"
        }
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,}$")
        if (!emailRegex.matches(email.trim())) {
            return "Enter a correct email"
        }
        return null
    }

    private fun triggerBackgroundSync() {
        try {
            val context = FirebaseApp.getInstance().applicationContext
            BizTrackSyncWorker.scheduleSync(context)
        } catch (e: Exception) {
            Log.e("BizTrackViewModel", "Error scheduling WorkManager sync", e)
        }
    }

    // Operations (scoped to the currently logged in user)
    fun addProduct(
        name: String,
        category: String,
        buyingPrice: Double,
        sellingPrice: Double,
        quantity: Int,
        date: String = getCurrentDate()
    ) {
        val userId = currentUserId ?: return
        val newProduct = Product(
            name = name,
            category = category,
            buyingPrice = buyingPrice,
            sellingPrice = sellingPrice,
            quantityPurchased = quantity,
            remainingQuantity = quantity,
            orderDate = date,
            isSoldOut = quantity <= 0
        )
        products.add(newProduct)
        localRepo.saveProducts(userId, products)
        localRepo.markProductUnsynced(userId, newProduct.id)
        triggerBackgroundSync()

        db.collection("users").document(userId).collection("products").document(newProduct.id).set(newProduct.toMap())
            .addOnSuccessListener {
                localRepo.clearProductUnsynced(userId, newProduct.id)
            }
            .addOnFailureListener { e ->
                Log.e("BizTrackViewModel", "Error adding product to Firestore: ${e.message}", e)
            }
    }

    fun updateProduct(
        productId: String,
        name: String,
        category: String,
        buyingPrice: Double,
        sellingPrice: Double,
        quantity: Int
    ) {
        val userId = currentUserId ?: return
        val index = products.indexOfFirst { it.id == productId }
        if (index != -1) {
            val oldProduct = products[index]
            val diffQty = quantity - oldProduct.quantityPurchased
            val newRemaining = (oldProduct.currentRemaining + diffQty).coerceIn(0, quantity)
            val updated = oldProduct.copy(
                name = name,
                category = category,
                buyingPrice = buyingPrice,
                sellingPrice = sellingPrice,
                quantityPurchased = quantity,
                remainingQuantity = newRemaining,
                isSoldOut = newRemaining <= 0,
                soldOutDate = if (newRemaining <= 0 && oldProduct.soldOutDate == null) getCurrentDate() else if (newRemaining > 0) null else oldProduct.soldOutDate
            )
            products[index] = updated
            localRepo.saveProducts(userId, products)
            localRepo.markProductUnsynced(userId, productId)
            triggerBackgroundSync()

            db.collection("users").document(userId).collection("products").document(productId).set(updated.toMap())
                .addOnSuccessListener {
                    localRepo.clearProductUnsynced(userId, productId)
                }
                .addOnFailureListener { e ->
                    Log.e("BizTrackViewModel", "Error updating product: ${e.message}", e)
                }
        }
    }

    fun deleteProduct(productId: String) {
        val userId = currentUserId ?: return
        products.removeAll { it.id == productId }
        localRepo.saveProducts(userId, products)
        db.collection("users").document(userId).collection("products").document(productId).delete()
            .addOnFailureListener { e ->
                Log.e("BizTrackViewModel", "Error deleting product: ${e.message}", e)
            }
    }

    fun recordSale(productId: String, quantitySold: Int, date: String = getCurrentDate()): String? {
        val userId = currentUserId ?: return "User not logged in"
        val index = products.indexOfFirst { it.id == productId }
        if (index == -1) return "Product not found"

        val product = products[index]
        val currentStock = product.currentRemaining

        if (currentStock <= 0 || product.isSoldOut) {
            return "Quantity exceeds available stock"
        }

        if (quantitySold > currentStock) {
            return "Quantity exceeds available stock"
        }

        val updatedRemaining = currentStock - quantitySold
        val isSoldOut = updatedRemaining <= 0
        val soldOutDate = if (isSoldOut) date else product.soldOutDate

        val updatedProduct = product.copy(
            remainingQuantity = updatedRemaining,
            isSoldOut = isSoldOut,
            soldOutDate = soldOutDate
        )

        products[index] = updatedProduct
        localRepo.saveProducts(userId, products)
        db.collection("users").document(userId).collection("products").document(product.id).set(updatedProduct.toMap())
            .addOnFailureListener { e ->
                Log.e("BizTrackViewModel", "Error updating product stock on sale: ${e.message}", e)
            }

        // Update existing sale entry for this product if present, or add a new one
        val existingSaleIndex = sales.indexOfFirst { it.productId == productId }
        if (existingSaleIndex != -1) {
            val existingSale = sales[existingSaleIndex]
            val newQtySold = existingSale.quantitySold + quantitySold
            val newTotalValue = newQtySold * existingSale.sellingPrice
            val updatedSale = existingSale.copy(
                quantitySold = newQtySold,
                totalValue = newTotalValue,
                date = date
            )
            sales[existingSaleIndex] = updatedSale
            localRepo.saveSales(userId, sales)
            localRepo.markSaleUnsynced(userId, updatedSale.id)
            triggerBackgroundSync()

            db.collection("users").document(userId).collection("sales").document(updatedSale.id).set(updatedSale.toMap())
                .addOnSuccessListener {
                    localRepo.clearSaleUnsynced(userId, updatedSale.id)
                }
                .addOnFailureListener { e ->
                    Log.e("BizTrackViewModel", "Error updating sale: ${e.message}", e)
                }
        } else {
            val newSale = Sale(
                productId = product.id,
                productName = product.name,
                quantitySold = quantitySold,
                sellingPrice = product.sellingPrice,
                buyingPrice = product.buyingPrice,
                date = date
            )
            sales.add(0, newSale)
            localRepo.saveSales(userId, sales)
            localRepo.markSaleUnsynced(userId, newSale.id)
            triggerBackgroundSync()

            db.collection("users").document(userId).collection("sales").document(newSale.id).set(newSale.toMap())
                .addOnSuccessListener {
                    localRepo.clearSaleUnsynced(userId, newSale.id)
                }
                .addOnFailureListener { e ->
                    Log.e("BizTrackViewModel", "Error adding sale: ${e.message}", e)
                }
        }

        return null // Success
    }

    fun updateSale(saleId: String, newQuantitySold: Int): String? {
        val userId = currentUserId ?: return "User not logged in"
        val saleIndex = sales.indexOfFirst { it.id == saleId }
        if (saleIndex == -1) return "Sale not found"
        val sale = sales[saleIndex]

        val productIndex = products.indexOfFirst { it.id == sale.productId }
        if (productIndex == -1) return "Product not found"
        val product = products[productIndex]

        val qtyDiff = newQuantitySold - sale.quantitySold
        val currentStock = product.currentRemaining

        if (qtyDiff > 0 && (currentStock <= 0 || product.isSoldOut)) {
            return "Product out of stock"
        }

        if (qtyDiff > 0 && qtyDiff > currentStock) {
            return "Product out of stock"
        }

        val updatedRemaining = currentStock - qtyDiff
        val isSoldOut = updatedRemaining <= 0
        val updatedProduct = product.copy(
            remainingQuantity = updatedRemaining,
            isSoldOut = isSoldOut,
            soldOutDate = if (isSoldOut && product.soldOutDate == null) getCurrentDate() else if (updatedRemaining > 0) null else product.soldOutDate
        )
        products[productIndex] = updatedProduct
        localRepo.saveProducts(userId, products)
        db.collection("users").document(userId).collection("products").document(product.id).set(updatedProduct.toMap())
            .addOnFailureListener { e ->
                Log.e("BizTrackViewModel", "Error updating product on sale update: ${e.message}", e)
            }

        val newTotalValue = newQuantitySold * sale.sellingPrice
        val updatedSale = sale.copy(
            quantitySold = newQuantitySold,
            totalValue = newTotalValue
        )
        sales[saleIndex] = updatedSale
        localRepo.saveSales(userId, sales)
        db.collection("users").document(userId).collection("sales").document(updatedSale.id).set(updatedSale.toMap())
            .addOnFailureListener { e ->
                Log.e("BizTrackViewModel", "Error updating sale record: ${e.message}", e)
            }

        return null
    }

    fun deleteSale(saleId: String) {
        val userId = currentUserId ?: return
        val sale = sales.find { it.id == saleId }
        if (sale != null) {
            // Restore stock to the corresponding product
            val productIndex = products.indexOfFirst { it.id == sale.productId }
            if (productIndex != -1) {
                val product = products[productIndex]
                val restoredRemaining = (product.currentRemaining + sale.quantitySold).coerceAtMost(product.quantityPurchased)
                val updatedProduct = product.copy(
                    remainingQuantity = restoredRemaining,
                    isSoldOut = restoredRemaining <= 0,
                    soldOutDate = if (restoredRemaining > 0) null else product.soldOutDate
                )
                products[productIndex] = updatedProduct
                localRepo.saveProducts(userId, products)
                db.collection("users").document(userId).collection("products").document(product.id).set(updatedProduct.toMap())
                    .addOnFailureListener { e ->
                        Log.e("BizTrackViewModel", "Error restoring stock on sale deletion: ${e.message}", e)
                    }
            }
        }
        sales.removeAll { it.id == saleId }
        localRepo.saveSales(userId, sales)
        db.collection("users").document(userId).collection("sales").document(saleId).delete()
            .addOnFailureListener { e ->
                Log.e("BizTrackViewModel", "Error deleting sale: ${e.message}", e)
            }
    }

    fun addExpense(description: String, amount: Double, date: String = getCurrentDate()) {
        val userId = currentUserId ?: return
        val newExpense = Expense(
            description = description,
            amount = amount,
            date = date
        )
        expenses.add(0, newExpense)
        localRepo.saveExpenses(userId, expenses)
        localRepo.markExpenseUnsynced(userId, newExpense.id)
        triggerBackgroundSync()

        db.collection("users").document(userId).collection("expenses").document(newExpense.id).set(newExpense.toMap())
            .addOnSuccessListener {
                localRepo.clearExpenseUnsynced(userId, newExpense.id)
            }
            .addOnFailureListener { e ->
                Log.e("BizTrackViewModel", "Error adding expense: ${e.message}", e)
            }
    }

    fun updateExpense(expenseId: String, description: String, amount: Double) {
        val userId = currentUserId ?: return
        val index = expenses.indexOfFirst { it.id == expenseId }
        if (index != -1) {
            val old = expenses[index]
            val updated = old.copy(
                description = description,
                amount = amount
            )
            expenses[index] = updated
            localRepo.saveExpenses(userId, expenses)
            localRepo.markExpenseUnsynced(userId, expenseId)
            triggerBackgroundSync()

            db.collection("users").document(userId).collection("expenses").document(expenseId).set(updated.toMap())
                .addOnSuccessListener {
                    localRepo.clearExpenseUnsynced(userId, expenseId)
                }
                .addOnFailureListener { e ->
                    Log.e("BizTrackViewModel", "Error updating expense: ${e.message}", e)
                }
        }
    }

    fun deleteExpense(expenseId: String) {
        val userId = currentUserId ?: return
        expenses.removeAll { it.id == expenseId }
        localRepo.saveExpenses(userId, expenses)
        db.collection("users").document(userId).collection("expenses").document(expenseId).delete()
            .addOnFailureListener { e ->
                Log.e("BizTrackViewModel", "Error deleting expense: ${e.message}", e)
            }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
