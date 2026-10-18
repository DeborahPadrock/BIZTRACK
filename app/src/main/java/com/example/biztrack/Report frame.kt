package com.example.biztrack

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.biztrack.ui.theme.BIZTRACKTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: BizTrackViewModel = remember { BizTrackViewModel() },
    onNavigateBack: () -> Unit = {}
) {
    val totalRevenue = viewModel.totalSalesValue
    val costOfGoodsSold = viewModel.sales.sumOf { it.buyingPrice * it.quantitySold }
    val grossProfit = viewModel.grossProfit
    val totalExpenses = viewModel.totalExpensesValue
    val netProfit = viewModel.netProfit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Performance Report", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Net Profit Statement (FR-12, FR-14)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (netProfit >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "FINANCIAL SUMMARY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "MWK %,.2f", netProfit),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (netProfit >= 0) "Net Business Profit" else "Net Business Loss",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Revenue & Cost Breakdown
            Text(
                text = "Profit & Loss Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReportRow(
                        label = "Sales Revenue (+)",
                        value = String.format(Locale.US, "MWK %,.2f", totalRevenue),
                        isBold = false
                    )
                    ReportRow(
                        label = "Cost of Sold Goods (-)",
                        value = String.format(Locale.US, "MWK %,.2f", costOfGoodsSold),
                        isBold = false
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    ReportRow(
                        label = "Gross Profit (=)",
                        value = String.format(Locale.US, "MWK %,.2f", grossProfit),
                        isBold = true
                    )

                    ReportRow(
                        label = "Operating Expenses (-)",
                        value = String.format(Locale.US, "MWK %,.2f", totalExpenses),
                        isBold = false
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    ReportRow(
                        label = "NET PROFIT / LOSS (=)",
                        value = String.format(Locale.US, "MWK %,.2f", netProfit),
                        isBold = true,
                        valueColor = if (netProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Inventory & Sales Activity Metrics
            Text(
                text = "Inventory & Sales Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReportRow(label = "Total Product Types", value = "${viewModel.totalProducts}")
                    ReportRow(label = "In-Stock Product Types", value = "${viewModel.totalProducts - viewModel.soldOutProductsCount}")
                    ReportRow(label = "Sold-Out Product Types", value = "${viewModel.soldOutProductsCount}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    ReportRow(label = "Total Units Sold", value = "${viewModel.totalProductsSold}")
                    ReportRow(label = "Total Sales Transactions", value = "${viewModel.sales.size}")
                    ReportRow(label = "Total Expense Records", value = "${viewModel.expenses.size}")
                }
            }
        }
    }
}

@Composable
fun ReportRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    BIZTRACKTheme {
        ReportScreen()
    }
}
