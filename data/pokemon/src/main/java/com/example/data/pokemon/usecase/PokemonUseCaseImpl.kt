package com.example.data.pokemon.usecase

import com.example.data.pokemon.model.Pokemon
import com.example.data.pokemon.model.PokemonInfo
import com.example.data.pokemon.repository.PokemonRepository
import com.example.network.delegate.CoroutineDelegate
import com.example.network.delegate.impl.CoroutineDelegateImpl
import com.example.network.model.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class PokemonUseCaseImpl(
    private val pokemonRepository: PokemonRepository
): PokemonUseCase, CoroutineDelegate by CoroutineDelegateImpl() {

    private val _pokemonsSharedFlow = MutableSharedFlow<Result<List<Pokemon>>?>()

    override fun getPokemonsListFlow(): SharedFlow<Result<List<Pokemon>>?> = _pokemonsSharedFlow.asSharedFlow()

    override fun requestPokemons() {
        runCoroutine {
            val response = pokemonRepository.getPokemons(limit = 100)
            response.body()?.let { pokemonResult ->
                _pokemonsSharedFlow.emit(value = Result(data = pokemonResult.results))
            } ?: run {
                _pokemonsSharedFlow.emit(value = Result(message = response.message()))
            }
        }
    }

    override suspend fun requestPokemonInfo(pokemonId: Int): Result<PokemonInfo> {
        val response = pokemonRepository.getPokemonInfo(pokemonId = pokemonId)
        return response.body()?.let { pokemonInfoResult ->
            Result(data = pokemonInfoResult.toPokemonInfo())
        } ?: run {
            Result(message = response.message())
        }
    }
}