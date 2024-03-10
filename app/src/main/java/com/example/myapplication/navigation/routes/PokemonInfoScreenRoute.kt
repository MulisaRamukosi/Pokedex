package com.example.myapplication.navigation.routes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.feature.pokemoninfo.PokemonInfoScreen
import com.example.feature.pokemoninfo.PokemonInfoVM

object PokemonInfoScreenRoutePathHelper {

    private const val main = "pokemonInfo"
    const val argPokemonId = "pokemonId"
    const val route = "$main/{$argPokemonId}"

    fun constructRoute(pokemonId: Int): String = "$main/$pokemonId"

}

object PokemonInfoScreenRoute {

    const val path = PokemonInfoScreenRoutePathHelper.route

    fun navigateTo(navController: NavController, pokemonId: Int) {
        navController.navigate(
            route = PokemonInfoScreenRoutePathHelper.constructRoute(
                pokemonId = pokemonId
            )
        )
    }

}

fun NavGraphBuilder.pokemonInfoScreen(
    navController: NavController
) {
    composable(
        route = PokemonInfoScreenRoute.path,
        arguments = listOf(
            navArgument(name = PokemonInfoScreenRoutePathHelper.argPokemonId) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        PokemonInfoScreen(
            modifier = Modifier.fillMaxSize(),
            pokemonId = backStackEntry.arguments?.getInt(PokemonInfoScreenRoutePathHelper.argPokemonId)
                ?: 0,
            pokemonInfoVM = hiltViewModel(),
            onClickHome = {
                navController.popBackStack()
            }
        )
    }
}