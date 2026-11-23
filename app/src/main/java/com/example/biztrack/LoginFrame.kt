package com.example.biztrack

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.biztrack.ui.theme.BIZTRACKTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToCreateAccount: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
        BizTrackLogoHeader()

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Login to Your Account",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = null
            },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
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
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill in all fields"
                    return@Button
                }
                isLoading = true
                val input = username.trim()
                val trimmedLower = input.lowercase()

                fun trySignInList(authEmails: List<String>) {
                    if (authEmails.isEmpty()) {
                        isLoading = false
                        errorMessage = "Incorrect username, email, or password"
                        return
                    }
                    val currentTryEmail = authEmails.first()
                    val remainingEmails = authEmails.drop(1)

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(currentTryEmail, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                isLoading = false
                                onLoginSuccess()
                            } else if (remainingEmails.isNotEmpty()) {
                                trySignInList(remainingEmails)
                            } else {
                                isLoading = false
                                errorMessage = "Incorrect username, email, or password"
                            }
                        }
                }

                val db = FirebaseFirestore.getInstance()

                if (trimmedLower.contains("@") && !trimmedLower.endsWith("@biztrack.app")) {
                    // User entered an email address like "debbie@gmail.com"
                    val syntheticEmail = "${input.substringBefore("@").lowercase()}@biztrack.app"
                    db.collection("users").whereEqualTo("email", input).get()
                        .addOnSuccessListener { querySnapshot ->
                            val foundUsername = if (!querySnapshot.isEmpty) querySnapshot.documents[0].getString("username") else null
                            val targetedSynthetic = if (!foundUsername.isNullOrBlank()) "${foundUsername.trim().lowercase()}@biztrack.app" else syntheticEmail
                            trySignInList(listOf(input, targetedSynthetic, syntheticEmail).distinct())
                        }
                        .addOnFailureListener {
                            trySignInList(listOf(input, syntheticEmail))
                        }
                } else {
                    // User entered a username like "debbie"
                    val syntheticEmail = if (trimmedLower.endsWith("@biztrack.app")) trimmedLower else "${trimmedLower}@biztrack.app"
                    db.collection("usernames").document(trimmedLower).get()
                        .addOnSuccessListener { snapshot ->
                            val realEmail = if (snapshot != null && snapshot.exists()) snapshot.getString("email") else null
                            val tryList = if (!realEmail.isNullOrBlank()) listOf(realEmail, syntheticEmail) else listOf(syntheticEmail)
                            trySignInList(tryList.distinct())
                        }
                        .addOnFailureListener {
                            trySignInList(listOf(syntheticEmail))
                        }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(if (isLoading) "Logging in..." else "Login", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToCreateAccount) {
            Text("Don't have an account? Create one")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BIZTRACKTheme {
        LoginScreen()
    }
}
