package com.example.pirateapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pirateapi.API.PirateApiService
import com.example.pirateapi.API.RetrofitClient
import com.example.pirateapi.Models.Treasure
import com.example.pirateapi.ui.theme.PirateAPITheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PirateAPITheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass the singleton Retrofit API instance into the screen
                    TreasureHuntScreen(apiService = RetrofitClient.api)
                }
            }
        }
    }
}

@Composable
fun TreasureHuntScreen(apiService: PirateApiService) {
    var treasureData by remember { mutableStateOf<Treasure?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDigging by remember { mutableStateOf(false) }

    // 1. ADD STATE FOR THE PASSCODE INPUT
    var passcodeInput by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (treasureData != null) {
                Text(
                    text = "🏴‍☠️ Treasure Found! 🏴‍☠️",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Coordinates: ${treasureData!!.coordinates}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Message: ${treasureData!!.secretMessage}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Booty: ${treasureData!!.goldCoins} Gold Coins")
            } else if (errorMessage != null) {
                Text(
                    text = "Avast! Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = "X marks the spot. Enter the Secret Passcode:",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. ADD THE TEXT FIELD FOR USERS TO TYPE THE PASSCODE
                OutlinedTextField(
                    value = passcodeInput,
                    onValueChange = { passcodeInput = it },
                    label = { Text("Secret Passcode") },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    isDigging = true
                    errorMessage = null

                    coroutineScope.launch {
                        try {
                            // 3. PASS THE TYPED PASSCODE INTO THE API CALL
                            val response = apiService.unearthTreasure(passcode = passcodeInput)

                            if (response.isSuccessful && response.body() != null) {
                                treasureData = response.body()
                            } else {
                                errorMessage = "The map was a fake! HTTP ${response.code()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "The ship sank! Network error: ${e.message}"
                        } finally {
                            isDigging = false
                        }
                    }
                },
                enabled = !isDigging && passcodeInput.isNotBlank() // Disable if empty
            ) {
                Text(if (isDigging) "Digging..." else "Dig for Treasure")
            }
        }
    }
}