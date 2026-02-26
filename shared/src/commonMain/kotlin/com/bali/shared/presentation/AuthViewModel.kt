package com.bali.shared.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bali.shared.data.local.SessionManager
import com.bali.shared.data.network.BaliApi
import com.bali.shared.domain.auth.AuthRepository
import com.bali.shared.domain.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing the entire phone authentication flow.
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val baliApi: BaliApi
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        // Check if user is already logged in on startup
        if (sessionManager.isLogged()) {
            _authState.value = AuthState.Authenticated(
                token = sessionManager.getToken() ?: "",
                userId = sessionManager.getUserId() ?: "",
                phoneNumber = sessionManager.getPhoneNumber() ?: ""
            )
        }
    }

    /** Temporarily stores the Firebase ID token for the backend exchange */
    private var pendingFirebaseToken: String? = null

    /**
     * Step 1: Request OTP for the given phone number.
     */
    fun requestOtp(phoneNumber: String, activity: Any?) {
        _authState.value = AuthState.Loading

        authRepository.sendOtp(
            phoneNumber = phoneNumber,
            activity = activity,
            onCodeSent = {
                _authState.value = AuthState.OtpSent
            },
            onError = { errorMessage ->
                _authState.value = AuthState.Error(errorMessage)
            }
        )
    }

    /**
     * Step 2: Verify the OTP entered by the user.
     */
    fun verifyOtp(otp: String) {
        _authState.value = AuthState.Loading

        authRepository.verifyOtp(
            otp = otp,
            onSuccess = { firebaseIdToken ->
                pendingFirebaseToken = firebaseIdToken
                exchangeTokenWithBackend(firebaseIdToken)
            },
            onError = { errorMessage ->
                _authState.value = AuthState.Error(errorMessage)
            }
        )
    }

    /**
     * Step 3: Exchange Firebase ID token with the backend for a JWT.
     */
    private fun exchangeTokenWithBackend(firebaseIdToken: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.loginWithFirebaseToken(firebaseIdToken)
                
                // Persist session
                sessionManager.saveSession(
                    token = result.token,
                    userId = result.userId,
                    phoneNumber = result.phoneNumber
                )

                if (result.isProfileComplete) {
                    _authState.value = AuthState.Authenticated(
                        token = result.token,
                        userId = result.userId,
                        phoneNumber = result.phoneNumber
                    )
                } else {
                    _authState.value = AuthState.ProfileIncomplete(
                        token = result.token,
                        userId = result.userId,
                        phoneNumber = result.phoneNumber
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Failed to authenticate with backend"
                )
            }
        }
    }

    /**
     * Step 4: Complete profile details.
     */
    fun completeProfile(name: String, email: String, address: String, villageId: String) {
        val currentState = _authState.value
        if (currentState !is AuthState.ProfileIncomplete) return

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val success = baliApi.completeProfile(name, email, address, villageId)
                if (success) {
                    _authState.value = AuthState.Authenticated(
                        token = currentState.token,
                        userId = currentState.userId,
                        phoneNumber = currentState.phoneNumber
                    )
                } else {
                    _authState.value = AuthState.Error("Failed to update profile")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error completing profile")
            }
        }
    }

    /**
     * Resend OTP to the same phone number.
     */
    fun resendOtp() {
        _authState.value = AuthState.Loading

        authRepository.resendOtp(
            onCodeSent = {
                _authState.value = AuthState.OtpSent
            },
            onError = { errorMessage ->
                _authState.value = AuthState.Error(errorMessage)
            }
        )
    }

    /**
     * Reset auth state to allow the user to start over.
     */
    fun resetState() {
        _authState.value = AuthState.Idle
        pendingFirebaseToken = null
    }
}
