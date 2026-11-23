package com.example.biztrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.biztrack.ui.theme.BIZTRACKTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExpenseFeatureTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun expenseScreen_displaysHeaderAndSummary() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                ExpenseScreen()
            }
        }

        composeTestRule.onNodeWithText("Expense Tracker").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Expenses").assertIsDisplayed()
        composeTestRule.onNodeWithText("Expense History").assertIsDisplayed()
    }

    @Test
    fun expenseScreen_opensAddExpenseDialog() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                ExpenseScreen()
            }
        }

        composeTestRule.onNodeWithContentDescription("Add Expense").performClick()

        composeTestRule.onNodeWithText("Record New Expense").assertIsDisplayed()
        composeTestRule.onNodeWithText("Expense Description (e.g. Rent, Transport)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amount (MWK)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save Expense").assertIsDisplayed()
    }
}
