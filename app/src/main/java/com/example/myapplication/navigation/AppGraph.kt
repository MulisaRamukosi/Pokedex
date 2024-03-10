package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.createGraph
import com.example.myapplication.navigation.routes.PokemonListScreenRoute
import com.example.myapplication.navigation.routes.pokemonInfoScreen
import com.example.myapplication.navigation.routes.pokemonListScreen

@Composable
fun appScreensGraph(navController: NavHostController): NavGraph {
    return navController.createGraph(startDestination = PokemonListScreenRoute.path) {
        pokemonListScreen(navController = navController)
        pokemonInfoScreen(navController = navController)
    }
}