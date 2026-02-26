package com.bali.android.di

import com.bali.android.auth.FirebaseAuthRepository
import com.bali.shared.domain.auth.AuthRepository
import org.koin.dsl.module

/**
 * Android-specific Koin dependency injection module.
 *
 * Provides:
 * - FirebaseAuthRepository as AuthRepository (singleton for state preservation)
 * - AuthViewModel (factory — new instance per request)
 * - FeedViewModel (factory)
 */
val androidModule = module {
    single<AuthRepository> { FirebaseAuthRepository(get()) }
}
