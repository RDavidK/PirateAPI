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
            PirateAPITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TreasureHuntScreen(apiService = RetrofitClient.api)
                }
            }
        }
    }
}

@Composable
fun TreasureHuntScreen(apiService: PirateApiService) {
    // ANDROID CLUE 1: The crew's memory washes away with every wave of Recomposition!
    // Wrap these variables in 'remember { mutableStateOf(...) }' so the UI can hold onto the treasure, errors, and digging status.
    var treasureData by remember { mutableStateOf<Treasure?>(null) }
    var isDigging by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ANDROID CLUE 2: The Captain needs a secret text field state to hold the passcode!
    // Initialize a state variable for 'passcodeInput' here (hint: mutableStateOf("")).
    var passcodeInput by remember { mutableStateOf("") }
    // ANDROID CLUE 3: Ye need a CoroutineScope to launch suspend functions from a Composable.
    // Initialize a 'rememberCoroutineScope()' here.
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
                Text(text = "🏴‍☠️ Treasure Found! 🏴‍☠️", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Coordinates: ${treasureData!!.coordinates}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Message: ${treasureData!!.secretMessage}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Booty: ${treasureData!!.goldCoins} Gold Coins")
            } else if (errorMessage != null) {
                Text(text = "Avast! Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            } else {
                Text(text = "X marks the spot. Enter the Secret Passcode:", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                // ANDROID CLUE 4: Build an OutlinedTextField here where the crew can type their secret passcode.
                // Bind its value to your passcodeInput state!
                OutlinedTextField(
                    value = passcodeInput,
                    onValueChange = { passcodeInput = it },
                    label = { Text("Secret Passcode") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    isDigging = true
                    errorMessage = null

                    // ANDROID CLUE 5: We can't chart a course on the main deck!
                    // Launch your coroutine scope here to safely execute the network request off the main thread.
                    coroutineScope.launch {
                        try {
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
                // ANDROID CLUE 6: Disable the button while digging OR if the passcode box is empty!
                enabled = !isDigging && passcodeInput.isNotBlank()
            ) {
                Text(if (isDigging) "Digging..." else "Dig for Treasure")
            }
        }
    }
}