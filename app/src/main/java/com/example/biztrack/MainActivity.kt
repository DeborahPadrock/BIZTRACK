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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

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

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

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
    val auth = remember { FirebaseAuth.getInstance() }
    var currentScreen by remember {
        mutableStateOf(
            if (auth.currentUser != null) BizTrackScreen.Dashboard else BizTrackScreen.Login
        )
    }
    val viewModel = remember { BizTrackViewModel() }

    when (currentScreen) {
        BizTrackScreen.Login -> {
            LoginScreen(
                onLoginSuccess = {
                    viewModel.loadUserData()
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
                    viewModel.loadUserData()
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
                    auth.signOut()
                    viewModel.clearData()
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

