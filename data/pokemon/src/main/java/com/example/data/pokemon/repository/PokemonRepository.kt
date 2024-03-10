package com.example.data.pokemon.repository

import com.example.data.pokemon.model.PokemonInfoResult
import com.example.data.pokemon.model.PokemonResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonRepository {

    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int): Response<PokemonResult>

    @GET("pokemon/{id}")
    suspend fun getPokemonInfo(@Path("id") pokemonId: Int): Response<PokemonInfoResult>
}