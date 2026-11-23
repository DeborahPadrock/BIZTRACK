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
class SalesFeatureTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun salesScreen_displaysHeaderAndSummary() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                SalesScreen()
            }
        }

        composeTestRule.onNodeWithText("Sales Records").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Revenue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Items Sold").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sales History").assertIsDisplayed()
    }

    @Test
    fun salesScreen_opensRecordSaleDialog() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                SalesScreen()
            }
        }

        composeTestRule.onNodeWithContentDescription("Record Sale").performClick()

        composeTestRule.onNodeWithText("Record New Sale").assertIsDisplayed()
    }
}
