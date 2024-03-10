package com.core.example.ui.models

enum class State {
    SUCCESS,
    LOADING,
    FAILED,
    DEFAULT
}

data class ViewState<T>(
    var data: T? = null,
    var isInitialLoad: Boolean = false,
    var uiState: State = State.DEFAULT,
    var message: String? = null,
    var version: Long = System.currentTimeMillis()
) {
    val isLoading: Boolean
        get() = uiState == State.LOADING
}