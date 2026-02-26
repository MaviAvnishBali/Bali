package com.bali.shared.di

import com.bali.shared.config.AppConfig
import com.bali.shared.data.ApolloClientFactory
import com.bali.shared.data.local.SessionManager
import com.bali.shared.data.network.BaliApi
import com.bali.shared.data.network.createHttpClient
import com.bali.shared.data.repository.BaliRepositoryImpl
import com.bali.shared.domain.repository.BaliRepository
import com.bali.shared.presentation.AuthViewModel
import com.bali.shared.presentation.CreatePostViewModel
import com.bali.shared.presentation.FeedViewModel
import com.bali.shared.presentation.ProfileViewModel
import com.bali.shared.presentation.SharedViewModel
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val commonModule = module {
    single { Settings() }
    single { SessionManager(get()) }
    single { createHttpClient(get()) }
    single { ApolloClientFactory() }
    single { 
        val factory: ApolloClientFactory = get()
        val sessionManager: SessionManager = get()
        val appConfig: AppConfig = get()
        factory.create(
            serverUrl = appConfig.baseUrl,
            tokenProvider = { sessionManager.getToken() }
        )
    }
    single { BaliApi(apolloClient = get()) }
    single<BaliRepository> { BaliRepositoryImpl(get()) }
    
    // SharedViewModel injected with BaliRepository
    single { SharedViewModel(repository = get()) }
    
    factory { AuthViewModel(authRepository = get(), sessionManager = get(), baliApi = get()) }
    factory { FeedViewModel(get()) }
    factory { ProfileViewModel(get()) }
    factory { CreatePostViewModel(repository = get(), sharedViewModel = get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule)
    }

// ios usage
fun initKoin() = initKoin {}
