package com.bali.shared.di

import com.bali.shared.auth.IOSAuthRepository
import com.bali.shared.domain.auth.AuthRepository
import org.koin.dsl.module

/**
 * iOS-specific Koin dependency injection module.
 */
val iosModule = module {
    single<AuthRepository> { IOSAuthRepository(get()) }
}

/**
 * Entry point for iOS to initialize Koin.
 * This should be called from the iOS App during startup (e.g., in AppDelegate).
 */
fun initKoinIos() = initKoin {
    modules(iosModule)
}
