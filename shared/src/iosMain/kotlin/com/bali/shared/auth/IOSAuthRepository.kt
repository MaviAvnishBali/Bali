package com.bali.shared.auth

import com.bali.shared.domain.auth.AuthRepository
import com.bali.shared.domain.auth.AuthResult

/**
 * iOS-specific implementation of AuthRepository.
 * 
 * NOTE: This is a boilerplate/stub. When building on a Mac, you will need to:
 * 1. Add Firebase iOS SDK to the project.
 * 2. Implement the phone auth logic using FIRPhoneAuthProvider.
 */
class IOSAuthRepository(
    private val baliApi: com.bali.shared.data.network.BaliApi
) : AuthRepository {

    override fun sendOtp(
        phoneNumber: String,
        activity: Any?,
        onCodeSent: () -> Unit,
        onError: (String) -> Unit
    ) {
        // TODO: Implement using Firebase iOS SDK
        // Example: PhoneAuthProvider.provider().verifyPhoneNumber(phoneNumber, uiDelegate: nil) { ... }
        println("iOS: requestOtp called for $phoneNumber")
        // onCodeSent() // Call this when Firebase sends the code successfully
    }

    override fun verifyOtp(
        otp: String,
        onSuccess: (firebaseIdToken: String) -> Unit,
        onError: (String) -> Unit
    ) {
        // TODO: Implement using Firebase iOS SDK
        // Example: val credential = PhoneAuthProvider.provider().credential(withVerificationID: verificationID, verificationCode: otp)
        println("iOS: verifyOtp called for $otp")
        // onSuccess("stubbed_firebase_token") // Call this when Firebase verifies the code
    }

    override suspend fun loginWithFirebaseToken(firebaseIdToken: String): AuthResult {
        // This part is actually shared logic handled by the backend
        return baliApi.loginWithPhone(firebaseIdToken)
    }

    override fun resendOtp(onCodeSent: () -> Unit, onError: (String) -> Unit) {
        // TODO: Implement using Firebase iOS SDK
        println("iOS: resendOtp called")
    }
}
