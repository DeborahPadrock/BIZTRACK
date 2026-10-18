package com.example.biztrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.biztrack.ui.theme.BIZTRACKTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: BizTrackViewModel = remember { BizTrackViewModel() },
    onNavigateBack: () -> Unit = {}
) {
    var showRecordSaleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Records", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showRecordSaleDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Record Sale")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Summary Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Revenue",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = String.format(Locale.US, "MWK %,.2f", viewModel.totalSalesValue),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Items Sold",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "${viewModel.totalProductsSold} units",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sales History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.sales.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No sales recorded yet. Tap '+' to record a sale.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.sales, key = { it.id }) { sale ->
                        SaleItemCard(sale = sale)
                    }
                }
            }
        }
    }

    if (showRecordSaleDialog) {
        RecordSaleDialog(
            products = viewModel.products,
            onDismiss = { showRecordSaleDialog = false },
            onConfirm = { productId, qty ->
                val error = viewModel.recordSale(productId, qty)
                if (error == null) {
                    showRecordSaleDialog = false
                }
                error
            }
        )
    }
}

@Composable
fun SaleItemCard(sale: Sale) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sale.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${sale.quantitySold} units @ ${String.format(Locale.US, "MWK %,.2f", sale.sellingPrice)} • ${sale.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "MWK %,.2f", sale.totalValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = String.format(Locale.US, "Profit: +MWK %,.2f", sale.profit),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun RecordSaleDialog(
    products: List<Product>,
    onDismiss: () -> Unit,
    onConfirm: (productId: String, quantitySold: Int) -> String?
) {
    val availableProducts = products.filter { !it.isSoldOut }
    var selectedProduct by remember { mutableStateOf(availableProducts.firstOrNull()) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var quantityText by remember { mutableStateOf("1") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val quantity = quantityText.toIntOrNull() ?: 0
    val totalSaleValue = (selectedProduct?.sellingPrice ?: 0.0) * quantity

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record New Sale") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (availableProducts.isEmpty()) {
                    Text(
                        text = "No available products in stock to sell. Please add products or restock first.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text("Select Product", style = MaterialTheme.typography.labelLarge)

                    Box {
                        OutlinedTextField(
                            value = selectedProduct?.let { "${it.name} (Stock: ${it.remainingQuantity})" } ?: "Select a product",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { dropdownExpanded = true }
                        )

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            availableProducts.forEach { prod ->
                                DropdownMenuItem(
                                    text = { Text("${prod.name} - Stock: ${prod.remainingQuantity} (${String.format(Locale.US, "MWK %,.2f", prod.sellingPrice)})") },
                                    onClick = {
                                        selectedProduct = prod
                                        dropdownExpanded = false
                                        errorMessage = null
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = {
                            quantityText = it
                            errorMessage = null
                        },
                        label = { Text("Quantity Sold") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (selectedProduct != null) {
                        Text(
                            text = String.format(Locale.US, "Total Value: MWK %,.2f", totalSaleValue),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (availableProducts.isNotEmpty()) {
                Button(
                    onClick = {
                        val prod = selectedProduct
                        if (prod == null) {
                            errorMessage = "Please select a product"
                            return@Button
                        }
                        if (quantity <= 0) {
                            errorMessage = "Quantity must be greater than 0"
                            return@Button
                        }
                        // FR-09: Stock Validation
                        if (quantity > prod.remainingQuantity) {
                            errorMessage = "Cannot sell $quantity units. Available stock is only ${prod.remainingQuantity} units."
                            return@Button
                        }

                        val err = onConfirm(prod.id, quantity)
                        if (err != null) {
                            errorMessage = err
                        }
                    }
                ) {
                    Text("Confirm Sale")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SalesScreenPreview() {
    BIZTRACKTheme {
        SalesScreen()
    }
}
