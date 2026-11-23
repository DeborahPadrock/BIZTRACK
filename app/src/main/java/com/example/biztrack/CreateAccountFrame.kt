package com.example.biztrack

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.biztrack.ui.theme.BIZTRACKTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateAccountScreen(
    onAccountCreated: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        BizTrackLogoHeader(showSubtitle = false)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create Your Account",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("emailInput")
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it; errorMessage = null },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("usernameInput")
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth().testTag("passwordInput")
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; errorMessage = null },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth().testTag("confirmPasswordInput")
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val emailError = validateEmail(email)
                if (emailError != null) {
                    errorMessage = emailError
                    return@Button
                }
                if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = "Please fill in all fields"
                    return@Button
                }
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@Button
                }

                // Requirement: Do not allow person to register while offline
                if (!isNetworkAvailable(context)) {
                    errorMessage = "Cannot register while offline. Please connect to the internet."
                    return@Button
                }

                isLoading = true
                val auth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()
                val userUsernameInput = username.trim()
                val userEmailInput = email.trim()
                val syntheticAuthEmail = "${userUsernameInput.lowercase()}@biztrack.app"

                fun completeUserRegistration(userId: String) {
                    val userMap = mapOf(
                        "uid" to userId,
                        "username" to userUsernameInput,
                        "email" to userEmailInput,
                        "createdAt" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    )

                    val usernameIndexMap = mapOf(
                        "uid" to userId,
                        "username" to userUsernameInput,
                        "email" to userEmailInput
                    )

                    db.collection("users").document(userId).set(userMap)
                        .addOnCompleteListener {
                            db.collection("usernames").document(userUsernameInput.lowercase()).set(usernameIndexMap)
                                .addOnCompleteListener {
                                    isLoading = false
                                    onAccountCreated()
                                }
                        }
                }

                // Register with user's real email in Firebase Auth
                auth.createUserWithEmailAndPassword(userEmailInput, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: ""
                            completeUserRegistration(userId)
                        } else {
                            // Fallback to synthetic username email
                            auth.createUserWithEmailAndPassword(syntheticAuthEmail, password)
                                .addOnCompleteListener { fallbackTask ->
                                    if (fallbackTask.isSuccessful) {
                                        val userId = auth.currentUser?.uid ?: ""
                                        completeUserRegistration(userId)
                                    } else {
                                        isLoading = false
                                        errorMessage = task.exception?.localizedMessage ?: fallbackTask.exception?.localizedMessage ?: "Registration failed"
                                    }
                                }
                        }
                    }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Creating Account..." else "Create Account")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Log in")
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,}$")
    return emailRegex.matches(email.trim())
}

fun validateEmail(email: String): String? {
    if (email.isBlank()) {
        return "Please enter email"
    }
    if (!isValidEmail(email)) {
        return "Enter a correct email"
    }
    return null
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Preview(showBackground = true)
@Composable
fun CreateAccountScreenPreview() {
    BIZTRACKTheme {
        CreateAccountScreen()
    }
}
