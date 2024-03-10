package com.example.data.pokemon.model

data class Pokemon(
    val name: String,
    val url: String
) {

    val id: Int
        get() = getPokemonId()

    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/${getPokemonId()}.gif"

    private fun getPokemonId(): Int {
        val urlSplit = url.split("/")
        return urlSplit[urlSplit.size - 2].toInt()
    }
}
