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
class ProductFeatureTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun productScreen_displaysHeaderAndEmptyState() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                ProductScreen()
            }
        }

        composeTestRule.onNodeWithText("Product Catalogue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search product name or category").assertIsDisplayed()
        composeTestRule.onNodeWithText("No products added yet. Tap '+' to add one.").assertIsDisplayed()
    }

    @Test
    fun productScreen_opensAddProductDialog() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                ProductScreen()
            }
        }

        composeTestRule.onNodeWithContentDescription("Add Product").performClick()

        composeTestRule.onNodeWithText("Add New Product").assertIsDisplayed()
        composeTestRule.onNodeWithText("Product Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Category (e.g. Groceries)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Buying Price (MWK)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Selling Price (MWK)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quantity Purchased").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save Product").assertIsDisplayed()
    }
}
