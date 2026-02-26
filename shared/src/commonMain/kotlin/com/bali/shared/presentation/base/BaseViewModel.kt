package com.bali.shared.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, I : UiIntent, E : UiEffect> : ViewModel() {

    private val initialState: S by lazy { createInitialState() }
    abstract fun createInitialState(): S

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect: Channel<E> = Channel()
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _intent: MutableSharedFlow<I> = MutableSharedFlow()

    init {
        subscribeIntents()
    }

    private fun subscribeIntents() {
        viewModelScope.launch {
            _intent.collect {
                handleIntent(it)
            }
        }
    }

    fun onIntent(intent: I) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    protected abstract fun handleIntent(intent: I)

    protected fun setState(reduce: S.() -> S) {
        _uiState.value = _uiState.value.reduce()
    }

    protected fun setEffect(builder: () -> E) {
        val effectValue = builder()
        viewModelScope.launch { _uiEffect.send(effectValue) }
    }
}
