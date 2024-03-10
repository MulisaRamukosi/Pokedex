package com.example.data.pokemon.usecase

import com.example.data.pokemon.model.Pokemon
import com.example.data.pokemon.model.PokemonInfo
import com.example.network.model.Result
import kotlinx.coroutines.flow.SharedFlow

interface PokemonUseCase {

    fun getPokemonsListFlow(): SharedFlow<Result<List<Pokemon>>?>

    fun requestPokemons()

    suspend fun requestPokemonInfo(pokemonId: Int): Result<PokemonInfo>
}