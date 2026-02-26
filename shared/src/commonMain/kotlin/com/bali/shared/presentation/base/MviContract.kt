package com.bali.shared.presentation.base

/**
 * Represent the state of the UI at any given time.
 */
interface UiState

/**
 * Represent a user action or an intent to change the state.
 */
interface UiIntent

/**
 * Represent a one-time side effect (e.g., navigation, showing a snackbar).
 */
interface UiEffect
