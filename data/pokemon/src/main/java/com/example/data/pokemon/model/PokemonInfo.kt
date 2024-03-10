package com.example.data.pokemon.model

data class PokemonInfo(
    val id: Int,
    val name: String,
    val frontImageUrl: String,
    val backImageUrl: String,
    val species: String,
    val abilities: List<String>,
    val height: Int,
    val stats: List<StatInfo>,
    val soundUrl: String
)

data class StatInfo(
    val value: Int,
    val name: String
)