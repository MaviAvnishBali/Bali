package com.bali.shared.domain.auth

/**
 * Sealed class representing all possible states of phone authentication flow.
 * Used by the ViewModel to update the UI reactively.
 */
sealed class AuthState {
    /** Initial state — user hasn't started authentication */
    data object Idle : AuthState()

    /** OTP has been requested, waiting for user input */
    data object OtpSent : AuthState()

    /** Currently verifying the OTP or processing authentication */
    data object Loading : AuthState()

    /** Authentication succeeded — contains the backend JWT and user info */
    data class Authenticated(
        val token: String,
        val userId: String,
        val phoneNumber: String
    ) : AuthState()

    /** User is authenticated but needs to provide more profile details */
    data class ProfileIncomplete(
        val token: String,
        val userId: String,
        val phoneNumber: String
    ) : AuthState()

    /** An error occurred during any step of authentication */
    data class Error(val message: String) : AuthState()
}
