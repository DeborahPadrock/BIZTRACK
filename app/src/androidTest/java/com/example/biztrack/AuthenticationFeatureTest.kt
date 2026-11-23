package com.example.biztrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.biztrack.ui.theme.BIZTRACKTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthenticationFeatureTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun loginScreen_displaysInitialUiElements() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithText("Login to Your Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? Create one").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsValidationOnEmptyFields() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithText("Login").performClick()
        composeTestRule.onNodeWithText("Please fill in all fields").assertIsDisplayed()
    }

    @Test
    fun createAccountScreen_displaysAllRegistrationFields() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                CreateAccountScreen()
            }
        }

        composeTestRule.onNodeWithText("Create Your Account").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emailInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("usernameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmPasswordInput").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Already have an account? Log in").assertIsDisplayed()
    }

    @Test
    fun createAccountScreen_validatesPasswordMismatch() {
        composeTestRule.setContent {
            BIZTRACKTheme {
                CreateAccountScreen()
            }
        }

        composeTestRule.onNodeWithTag("emailInput").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("usernameInput").performTextInput("testuser")
        composeTestRule.onNodeWithTag("passwordInput").performTextInput("password123")
        composeTestRule.onNodeWithTag("confirmPasswordInput").performTextInput("password321")

        composeTestRule.onNodeWithText("Create Account").performClick()
        composeTestRule.onNodeWithText("Passwords do not match").assertIsDisplayed()
    }
}
