package com.example.feature.pokemoninfo

import com.core.example.ui.CoroutineVM
import com.core.example.ui.models.State
import com.core.example.ui.models.ViewState
import com.example.data.pokemon.model.PokemonInfo
import com.example.data.pokemon.usecase.PokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PokemonInfoVM @Inject constructor(private val pokemonUseCase: PokemonUseCase): CoroutineVM() {

    private val _pokemonInfoViewState = MutableStateFlow<ViewState<PokemonInfo>>(value = ViewState())
    val pokemonInfoViewState by lazy {
        val initPokemonInfo: (Int) -> StateFlow<ViewState<PokemonInfo>> = { pokemonId ->
            handleStateViewInit(
                state = _pokemonInfoViewState,
                initAction = {
                    updateStateWithRequestPokemonInfo(pokemonId = pokemonId)
                }
            )
        }

        initPokemonInfo
    }

    fun requestPokemonInfo(pokemonId: Int) = runCoroutine {
        updateStateWithRequestPokemonInfo(pokemonId = pokemonId)
    }

    private suspend fun updateStateWithRequestPokemonInfo(pokemonId: Int) {
        updateStateUiState(viewState = _pokemonInfoViewState, uiState = State.LOADING)

        val pokemonInfoResult = pokemonUseCase.requestPokemonInfo(pokemonId = pokemonId)

        updateStateWithResult(
            viewState = _pokemonInfoViewState,
            data = pokemonInfoResult.data,
            message = pokemonInfoResult.message,
            version = pokemonInfoResult.version
        )
    }

}