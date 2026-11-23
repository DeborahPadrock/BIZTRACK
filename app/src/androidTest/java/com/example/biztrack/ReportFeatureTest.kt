package com.example.biztrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.biztrack.ui.theme.BIZTRACKTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportFeatureTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun reportScreen_displaysFinancialSummaryAndBreakdown() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                ReportScreen()
            }
        }

        composeTestRule.onNodeWithText("Business Performance Report").assertIsDisplayed()
        composeTestRule.onNodeWithText("FINANCIAL SUMMARY").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profit & Loss Breakdown").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sales Revenue (+)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cost of Sold Goods (-)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gross Profit (=)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Operating Expenses (-)").assertIsDisplayed()
        composeTestRule.onNodeWithText("NET PROFIT / LOSS (=)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Inventory & Sales Metrics").assertIsDisplayed()
    }
}
