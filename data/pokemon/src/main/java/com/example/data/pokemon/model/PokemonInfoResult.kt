package com.example.data.pokemon.model

import com.google.gson.annotations.SerializedName

data class PokemonInfoResult(
    val id: Int?,
    val name: String?,
    val abilities: ArrayList<Abilities>,
    val cries: Cries?,
    val sprites: Sprites?,
    val species: Species?,
    val height: Int?,
    val stats: ArrayList<Stats>
) {

    fun toPokemonInfo(): PokemonInfo = PokemonInfo(
        id = id ?: 0,
        name = name ?: "",
        abilities = abilities.map { it.ability.name ?: "" },
        frontImageUrl = sprites?.frontDefault ?: "",
        backImageUrl = sprites?.backDefault ?: "",
        species = species?.name ?: "",
        height = height ?: 0,
        stats = stats.map { StatInfo(value = it.baseStat ?: 0, name = it.stat?.name ?: "") },
        soundUrl = cries?.latest ?: ""
    )

}

data class Abilities(val ability: Ability)

data class Ability(val name: String?)

data class Cries(val latest: String?)

data class Sprites(
    @SerializedName("back_default") val backDefault: String?,
    @SerializedName("front_default") val frontDefault: String?
)

data class Species(val name: String?)

data class Stats(
    @SerializedName("base_stat") val baseStat: Int?,
    @SerializedName("stat") val stat: Stat?

)

data class Stat(val name: String?)