package com.core.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.example.ui.models.State
import com.core.example.ui.models.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class CoroutineVM : ViewModel() {

    fun runCoroutine(
        context: CoroutineContext = Dispatchers.IO,
        action: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context = context, block = action)


    fun <T> handleStateViewInit(
        state: MutableStateFlow<ViewState<T>>,
        initAction: suspend CoroutineScope.() -> Unit
    ): StateFlow<ViewState<T>> {
        if (state.value.uiState == State.DEFAULT) {
            state.update {
                it.copy(
                    uiState = State.LOADING,
                    isInitialLoad = true
                )
            }

            runCoroutine {
                initAction()
            }
        }

        return state.asStateFlow()
    }

    fun <T> updateStateWithResult(
        viewState: MutableStateFlow<ViewState<T>>,
        data: T?,
        isInitialLoad: Boolean = false,
        uiState: State = if (data != null) State.SUCCESS else State.FAILED,
        message: String,
        version: Long
    ) {
        viewState.update { state ->
            state.copy(
                data = data,
                isInitialLoad = isInitialLoad,
                uiState = uiState,
                message = message,
                version = version
            )
        }
    }

    fun <T> updateStateUiState(
        viewState: MutableStateFlow<ViewState<T>>,
        uiState: State
    ) {
        viewState.update { state ->
            state.copy(uiState = uiState)
        }
    }

    protected val responseMessageSharedFlow = MutableSharedFlow<String>()
    val requestResponseMessageFlow = responseMessageSharedFlow.asSharedFlow()

}