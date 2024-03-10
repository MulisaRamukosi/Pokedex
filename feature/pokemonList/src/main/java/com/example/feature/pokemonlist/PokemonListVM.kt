package com.example.feature.pokemonlist

import com.core.example.ui.CoroutineVM
import com.core.example.ui.models.State
import com.core.example.ui.models.ViewState
import com.example.data.pokemon.model.Pokemon
import com.example.data.pokemon.usecase.PokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class PokemonListVM @Inject constructor(private val pokemonUseCase: PokemonUseCase): CoroutineVM() {

    private val _pokemonsViewState = MutableStateFlow<ViewState<List<Pokemon>>>(value = ViewState())
    val pokemonsViewState by lazy {
        handleStateViewInit(
            state = _pokemonsViewState,
            initAction = {
                pokemonUseCase.requestPokemons()
                initPokemonsSubFlow()
            }
        )
    }

    fun reloadPokemons(){
        updateStateUiState(viewState = _pokemonsViewState, uiState = State.LOADING)
        pokemonUseCase.requestPokemons()
    }

    private suspend fun initPokemonsSubFlow(){
        pokemonUseCase.getPokemonsListFlow().collectLatest { result ->
            result?.let {
                updateStateWithResult(
                    viewState = _pokemonsViewState,
                    data = result.data,
                    message = result.message,
                    version = result.version
                )
            }
        }
    }
}