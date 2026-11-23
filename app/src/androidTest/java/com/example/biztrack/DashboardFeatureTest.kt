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
class DashboardFeatureTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun dashboardScreen_displaysHeaderAndSummaryMetrics() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                DashboardScreen()
            }
        }

        composeTestRule.onNodeWithText("BIZTRACK Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Net Profit").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Sales").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Expenses").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Products").assertIsDisplayed()
        composeTestRule.onNodeWithText("Products Sold").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quick Actions").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_opensAccountProfileAndLogoutDialog() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                DashboardScreen()
            }
        }

        composeTestRule.onNodeWithContentDescription("User Profile & Logout").performClick()

        composeTestRule.onNodeWithText("Account Profile & Logout").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email Address:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Are you sure you want to log out of BIZTRACK?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }
}
