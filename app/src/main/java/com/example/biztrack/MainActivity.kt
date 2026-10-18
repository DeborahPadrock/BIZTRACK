package com.example.biztrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.biztrack.ui.theme.BIZTRACKTheme

enum class BizTrackScreen {
    Login,
    CreateAccount,
    Dashboard,
    Products,
    Sales,
    Expenses,
    Reports
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            BIZTRACKTheme {
                BizTrackApp()
            }
        }
    }
}

@Composable
fun BizTrackApp() {
    var currentScreen by remember { mutableStateOf(BizTrackScreen.Login) }
    val viewModel = remember { BizTrackViewModel() }

    when (currentScreen) {
        BizTrackScreen.Login -> {
            LoginScreen(
                onLoginSuccess = {
                    currentScreen = BizTrackScreen.Dashboard
                },
                onNavigateToCreateAccount = {
                    currentScreen = BizTrackScreen.CreateAccount
                }
            )
        }
        BizTrackScreen.CreateAccount -> {
            CreateAccountScreen(
                onAccountCreated = {
                    currentScreen = BizTrackScreen.Dashboard
                },
                onNavigateToLogin = {
                    currentScreen = BizTrackScreen.Login
                }
            )
        }
        BizTrackScreen.Dashboard -> {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToProducts = {
                    currentScreen = BizTrackScreen.Products
                },
                onNavigateToSales = {
                    currentScreen = BizTrackScreen.Sales
                },
                onNavigateToExpenses = {
                    currentScreen = BizTrackScreen.Expenses
                },
                onNavigateToReports = {
                    currentScreen = BizTrackScreen.Reports
                },
                onLogout = {
                    currentScreen = BizTrackScreen.Login
                }
            )
        }
        BizTrackScreen.Products -> {
            ProductScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    currentScreen = BizTrackScreen.Dashboard
                }
            )
        }
        BizTrackScreen.Sales -> {
            SalesScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    currentScreen = BizTrackScreen.Dashboard
                }
            )
        }
        BizTrackScreen.Expenses -> {
            ExpenseScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    currentScreen = BizTrackScreen.Dashboard
                }
            )
        }
        BizTrackScreen.Reports -> {
            ReportScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    currentScreen = BizTrackScreen.Dashboard
                }
            )
        }
    }
}
