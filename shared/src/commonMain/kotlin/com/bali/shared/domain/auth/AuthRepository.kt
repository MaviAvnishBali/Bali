package com.bali.shared.domain.auth

/**
 * Platform-agnostic authentication repository interface.
 *
 * Firebase Phone Auth is Android-specific, so the implementation will live
 * in androidApp. This interface defines the contract that the ViewModel
 * depends on, keeping the shared module free of Firebase dependencies.
 */
interface AuthRepository {

    /**
     * Send OTP to the given phone number.
     * The [activity] parameter is needed by Firebase on Android for reCAPTCHA.
     * On other platforms, pass null.
     *
     * @param phoneNumber phone number with country code, e.g. "+91XXXXXXXXXX"
     * @param activity platform-specific activity reference (Any? to keep it common)
     * @param onCodeSent callback when OTP is successfully sent
     * @param onError callback when OTP sending fails
     */
    fun sendOtp(
        phoneNumber: String,
        activity: Any?,
        onCodeSent: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Verify the OTP entered by the user.
     *
     * @param otp the 6-digit OTP code
     * @param onSuccess callback with Firebase ID token on successful verification
     * @param onError callback when verification fails
     */
    fun verifyOtp(
        otp: String,
        onSuccess: (firebaseIdToken: String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Send the Firebase ID token to the backend for JWT exchange.
     *
     * @param firebaseIdToken the token from Firebase after OTP verification
     * @return AuthResult containing backend JWT and user info
     */
    suspend fun loginWithFirebaseToken(firebaseIdToken: String): AuthResult

    /**
     * Resend OTP to the same phone number.
     */
    fun resendOtp(
        onCodeSent: () -> Unit,
        onError: (String) -> Unit
    )
}

/**
 * Result of the backend token exchange.
 */
data class AuthResult(
    val token: String,
    val userId: String,
    val phoneNumber: String,
    val isProfileComplete: Boolean = true
)
