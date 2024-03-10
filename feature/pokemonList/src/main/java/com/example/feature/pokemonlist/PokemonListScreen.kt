@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.feature.pokemonlist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.core.example.ui.components.AsyncImageView
import com.core.example.ui.models.State
import com.core.example.ui.models.ViewState
import com.core.example.ui.theme.PokedexTheme
import com.core.example.ui.views.ErrorFailedView
import com.example.data.pokemon.model.Pokemon
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun PokemonListScreen(
    modifier: Modifier,
    pokemonListVM: PokemonListVM,
    onClickPokemon: (pokemonId: Int) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        pokemonListVM.requestResponseMessageFlow.collectLatest { error ->
            scope.launch { snackbarHostState.showSnackbar(message = error) }
        }
    }

    val pokemonsViewState by pokemonListVM.pokemonsViewState.collectAsState()

    PokemonListScreenLayout(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        pokemonsViewState = pokemonsViewState,
        reloadPokemons = pokemonListVM::reloadPokemons,
        onClickPokemon = onClickPokemon
    )

}

@Composable
private fun PokemonListScreenLayout(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    pokemonsViewState: ViewState<List<Pokemon>>,
    reloadPokemons: () -> Unit,
    onClickPokemon: (pokemonId: Int) -> Unit
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.pokemons)) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        pokemonsViewState.data?.let { pokemons ->
            if (pokemons.isEmpty()) {
                NoPokemonsAvailable(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                    isLoading = pokemonsViewState.isLoading,
                    onClickRequestPokemons = reloadPokemons
                )
            } else {
                PokemonsList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                    pokemons = pokemons,
                    onClickPokemon = onClickPokemon
                )
            }
        } ?: run {
            if (!pokemonsViewState.isInitialLoad && pokemonsViewState.uiState != State.DEFAULT) {
                ErrorFailedView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                    message = pokemonsViewState.message,
                    isLoading = pokemonsViewState.isLoading,
                    onClickTryAgain = reloadPokemons
                )
            } else if (pokemonsViewState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun PokemonsList(
    modifier: Modifier,
    pokemons: List<Pokemon>,
    onClickPokemon: (pokemonId: Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
    ) {
        SearchPokemon(
            modifier = Modifier
                .fillMaxWidth(),
            pokemons = pokemons,
            onClickPokemon = onClickPokemon
        )

        PokemonsGridView(
            modifier = Modifier.fillMaxWidth(),
            pokemons = pokemons,
            onClickPokemon = onClickPokemon
        )

    }
}

@Composable
private fun SearchPokemon(
    modifier: Modifier,
    pokemons: List<Pokemon>,
    onClickPokemon: (pokemonId: Int) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    val foundPokemons by remember(text) {
        derivedStateOf {
            if (text.isBlank()) emptyList()
            else pokemons.filter { it.name.contains(other = text, ignoreCase = true) }
        }
    }

    SearchBar(
        modifier = modifier.animateContentSize().then(Modifier.padding(horizontal = if (active) 0.dp else 16.dp)),
        query = text,
        onQueryChange = { text = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = {
            active = it
            if (!active) {
                text = ""
            }
        },
        placeholder = { Text(text = stringResource(id = R.string.search_pokemon)) },
        leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = null) },
    ) {
        PokemonsGridView(
            modifier = Modifier.fillMaxWidth(),
            pokemons = foundPokemons,
            onClickPokemon = onClickPokemon
        )
    }
}

@Composable
private fun PokemonsGridView(
    modifier: Modifier,
    pokemons: List<Pokemon>,
    onClickPokemon: (pokemonId: Int) -> Unit
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalItemSpacing = 8.dp,
        contentPadding = PaddingValues(all = 16.dp),
        columns = StaggeredGridCells.Adaptive(minSize = 150.dp)
    ) {
        items(items = pokemons) { pokemon ->
            PokemonItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                pokemon = pokemon,
                onClickPokemon = { onClickPokemon(pokemon.id) }
            )
        }
    }
}

@Composable
private fun PokemonItem(
    modifier: Modifier,
    pokemon: Pokemon,
    onClickPokemon: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClickPokemon
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImageView(
                modifier = Modifier
                    .size(size = 100.dp)
                    .clip(shape = RoundedCornerShape(size = 4.dp)),
                imageUrl = pokemon.imageUrl
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            Text(text = pokemon.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun NoPokemonsAvailable(
    modifier: Modifier,
    isLoading: Boolean,
    onClickRequestPokemons: () -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth(), painter = painterResource(
                    id = R.drawable.empty_ball
                ),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(height = 16.dp))

            Text(
                text = stringResource(id = R.string.no_pokemons_available),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(height = 16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClickRequestPokemons,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(text = stringResource(id = R.string.request_pokemons))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPokemonListScreenLayoutInLoadingState() {
    PokedexTheme {
        PokemonListScreenLayout(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            pokemonsViewState = ViewState(isInitialLoad = true, uiState = State.LOADING),
            reloadPokemons = { /*TODO*/ },
            onClickPokemon = {}
        )
    }
}

@Preview
@Composable
private fun PreviewPokemonListScreenLayoutInFailedState() {
    PokedexTheme {
        PokemonListScreenLayout(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            pokemonsViewState = ViewState(
                data = null,
                uiState = State.FAILED,
                message = "Some error occurred from the api"
            ),
            reloadPokemons = { /*TODO*/ },
            onClickPokemon = {}
        )
    }
}

@Preview
@Composable
private fun PreviewPokemonListScreenLayoutInSuccessStateWithEmptyList() {
    PokedexTheme {
        PokemonListScreenLayout(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            pokemonsViewState = ViewState(data = listOf()),
            reloadPokemons = { /*TODO*/ },
            onClickPokemon = {}
        )
    }
}

@Preview
@Composable
private fun PreviewPokemonListScreenLayoutInSuccessStateWithPokemons() {
    PokedexTheme {
        PokemonListScreenLayout(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            pokemonsViewState = ViewState(
                data = listOf(
                    Pokemon(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/"),
                    Pokemon(name = "ivysaur", url = "https://pokeapi.co/api/v2/pokemon/2/"),
                    Pokemon(name = "venusaur", url = "https://pokeapi.co/api/v2/pokemon/3/"),
                    Pokemon(name = "charmander", url = "https://pokeapi.co/api/v2/pokemon/4/"),
                    Pokemon(name = "charmeleon", url = "https://pokeapi.co/api/v2/pokemon/5/"),
                    Pokemon(name = "charizard", url = "https://pokeapi.co/api/v2/pokemon/6/"),
                    Pokemon(name = "squirtle", url = "https://pokeapi.co/api/v2/pokemon/7/"),
                    Pokemon(name = "wartortle", url = "https://pokeapi.co/api/v2/pokemon/8/"),

                    )
            ),
            reloadPokemons = { /*TODO*/ },
            onClickPokemon = {}
        )
    }
}