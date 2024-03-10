package com.example.myapplication.navigation.routes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.feature.pokemonlist.PokemonListScreen
import com.example.feature.pokemonlist.PokemonListVM

object PokemonListScreenRoute {

    const val path = "pokemonList"

}

fun NavGraphBuilder.pokemonListScreen(
    navController: NavController,
) {
    composable(route = PokemonListScreenRoute.path) {
        PokemonListScreen(
            modifier = Modifier.fillMaxSize(),
            pokemonListVM = hiltViewModel(),
            onClickPokemon = { pokemonId ->
                PokemonInfoScreenRoute.navigateTo(
                    navController = navController,
                    pokemonId = pokemonId
                )
            }
        )
    }

}