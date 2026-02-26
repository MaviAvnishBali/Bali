package com.bali.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bali.shared.presentation.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCompletionScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedVillageId by remember { mutableStateOf("1") } // Default for now

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Profile") },
                actions = {
                    TextButton(
                        onClick = {
                            // TODO: handle skip action
                        }
                    ) {
                        Text(
                            text = "Skip",
                            color = Color.Red
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Final Step!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                "Please provide your details to get started with Bali.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Permanent Address / Village") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Simplistic village selection for now
            Text(
                "Select your Village Group",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Start)
            )
            
            // Placeholder for a real dropdown
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = true, onClick = {})
                Text("Varanasi Central")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.completeProfile(name, email, address, selectedVillageId)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && address.isNotBlank(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Start Exploring")
            }
        }
    }
}
