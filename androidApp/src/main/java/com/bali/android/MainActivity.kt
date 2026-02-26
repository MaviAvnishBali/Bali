package com.bali.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bali.shared.ui.App

/**
 * Main entry point of the Bali Android app.
 * Now simply hosts the shared Compose Multiplatform App.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(this)
        }
    }
}
