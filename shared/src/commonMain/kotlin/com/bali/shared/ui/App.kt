package com.bali.shared.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bali.shared.domain.auth.AuthState
import com.bali.shared.presentation.AuthViewModel
import com.bali.shared.presentation.SharedViewModel
import com.bali.shared.ui.theme.BaliTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 * Shared entry point for the Bali application.
 * Manages the high-level navigation flow between Auth and Main screens.
 */
@Composable
fun App(platformContext: Any? = null) {
    BaliTheme {
        Surface {
            val authViewModel: AuthViewModel = koinViewModel()
            val sharedViewModel: SharedViewModel = koinViewModel()
            val authState by authViewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                sharedViewModel.fetchProfile()
            }
            }
            when (authState) {
                is AuthState.ProfileIncomplete -> {
                    ProfileCompletionScreen(
                        viewModel = authViewModel,
                        onBack = { authViewModel.resetState() }
                    )
                }
                is AuthState.Authenticated -> {
                    MainScreen()
                }
                is AuthState.OtpSent -> {
                    OtpVerificationScreen(
                        authState = authState,
                        onVerifyOtp = { otp -> authViewModel.verifyOtp(otp) },
                        onResendOtp = { authViewModel.resendOtp() },
                        onBackToPhone = { authViewModel.resetState() }
                    )
                }
                else -> {
                    PhoneLoginScreen(
                        authState = authState,
                        onRequestOtp = { phoneNumber ->
                            authViewModel.requestOtp(phoneNumber, platformContext)
                        }
                    )
                }
            }
        }
    }
}
