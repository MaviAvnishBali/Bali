package com.bali.android.auth

import android.app.Activity
import com.bali.shared.data.network.BaliApi
import com.bali.shared.domain.auth.AuthRepository
import com.bali.shared.domain.auth.AuthResult
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

/**
 * Android-specific implementation of AuthRepository using Firebase Phone Auth.
 *
 * Handles:
 * - Sending OTP via Firebase
 * - Auto-detection of OTP (when supported by device)
 * - Manual OTP verification
 * - Exchanging Firebase ID token with backend for JWT
 */
class FirebaseAuthRepository(
    private val api: BaliApi
) : AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    // Stored verification ID from Firebase after OTP is sent
    private var storedVerificationId: String? = null

    // Resend token for re-requesting OTP
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    // Store the phone number and activity for resend
    private var currentPhoneNumber: String? = null
    private var currentActivity: Activity? = null

    override fun sendOtp(
        phoneNumber: String,
        activity: Any?,
        onCodeSent: () -> Unit,
        onError: (String) -> Unit
    ) {
        val androidActivity = activity as? Activity
            ?: run {
                onError("Activity context is required for phone auth")
                return
            }

        currentPhoneNumber = phoneNumber
        currentActivity = androidActivity

        val callbacks = createCallbacks(onCodeSent, onError, null)

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(androidActivity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun verifyOtp(
        otp: String,
        onSuccess: (firebaseIdToken: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val verificationId = storedVerificationId
        if (verificationId == null) {
            onError("Verification ID not found. Please request OTP again.")
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithCredential(credential, onSuccess, onError)
    }

    override suspend fun loginWithFirebaseToken(firebaseIdToken: String): AuthResult {
        return api.loginWithPhone(firebaseIdToken)
    }

    override fun resendOtp(
        onCodeSent: () -> Unit,
        onError: (String) -> Unit
    ) {
        val phone = currentPhoneNumber
        val activity = currentActivity
        val token = resendToken

        if (phone == null || activity == null) {
            onError("Cannot resend OTP. Please start over.")
            return
        }

        val callbacks = createCallbacks(onCodeSent, onError, null)

        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        // Use resend token if available for faster delivery
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    /**
     * Create Firebase PhoneAuth callbacks.
     * Handles auto-verification, OTP sent confirmation, and errors.
     */
    private fun createCallbacks(
        onCodeSent: () -> Unit,
        onError: (String) -> Unit,
        onAutoVerified: ((String) -> Unit)?
    ): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // Auto-verification succeeded (e.g., instant verification on some devices)
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if (onAutoVerified != null) {
                    signInWithCredential(credential, onAutoVerified, onError)
                }
            }

            // Verification failed
            override fun onVerificationFailed(e: FirebaseException) {
                onError(e.message ?: "Phone verification failed")
            }

            // OTP sent successfully — store verification ID
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
                onCodeSent()
            }
        }
    }

    /**
     * Sign in with a PhoneAuthCredential and retrieve the Firebase ID token.
     */
    private fun signInWithCredential(
        credential: PhoneAuthCredential,
        onSuccess: (firebaseIdToken: String) -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                authResult.user?.getIdToken(true)
                    ?.addOnSuccessListener { tokenResult ->
                        val token = tokenResult.token
                        if (token != null) {
                            onSuccess(token)
                        } else {
                            onError("Failed to get Firebase ID token")
                        }
                    }
                    ?.addOnFailureListener { e ->
                        onError("Failed to get ID token: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onError("Sign-in failed: ${e.message}")
            }
    }
}
