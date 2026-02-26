package com.bali.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bali.shared.domain.auth.AuthState
import com.bali.shared.ui.BaliButton

/**
 * OTP verification screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    authState: AuthState,
    onVerifyOtp: (otp: String) -> Unit,
    onResendOtp: () -> Unit,
    onBackToPhone: () -> Unit
) {
    var otp by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Verify OTP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter the 6-digit code sent to your phone",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // OTP input field
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it.filter { c -> c.isDigit() }.take(6) },
                label = { Text("OTP Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("------") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Verify button
            BaliButton(
                text = "Verify",
                onClick = { onVerifyOtp(otp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = otp.length == 6,
                isLoading = authState is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Resend OTP button
            TextButton(
                onClick = onResendOtp,
                enabled = authState !is AuthState.Loading
            ) {
                Text("Didn't receive the code? Resend OTP")
            }

            // Back to phone entry
            TextButton(onClick = onBackToPhone) {
                Text("Change phone number")
            }

            // Show error
            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
