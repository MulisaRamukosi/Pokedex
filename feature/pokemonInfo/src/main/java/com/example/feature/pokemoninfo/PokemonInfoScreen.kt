@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.feature.pokemoninfo

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.core.example.ui.components.AsyncImageView
import com.core.example.ui.models.State
import com.core.example.ui.models.ViewState
import com.core.example.ui.theme.PokedexTheme
import com.core.example.ui.views.ErrorFailedView
import com.example.data.pokemon.model.PokemonInfo
import com.example.data.pokemon.model.Stat
import com.example.data.pokemon.model.StatInfo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun PokemonInfoScreen(
    modifier: Modifier,
    pokemonId: Int,
    pokemonInfoVM: PokemonInfoVM,
    onClickHome: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        pokemonInfoVM.requestResponseMessageFlow.collectLatest { error ->
            scope.launch { snackbarHostState.showSnackbar(message = error) }
        }
    }

    val pokemonInfoViewState by pokemonInfoVM.pokemonInfoViewState(pokemonId).collectAsState()

    PokemonInfoScreenLayout(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        pokemonInfoViewState = pokemonInfoViewState,
        reloadPokemonInfo = { pokemonInfoVM.requestPokemonInfo(pokemonId = pokemonId) },
        onClickHome = onClickHome
    )
}

@Composable
private fun PokemonInfoScreenLayout(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    pokemonInfoViewState: ViewState<PokemonInfo>,
    reloadPokemonInfo: () -> Unit,
    onClickHome: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(
                        text = pokemonInfoViewState.data?.name
                            ?: stringResource(id = R.string.pokemon)
                    )
                })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        pokemonInfoViewState.data?.let { pokemonInfo ->
            PokemonInfoView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                pokemonInfo = pokemonInfo
            )
        } ?: run {
            if (!pokemonInfoViewState.isInitialLoad && pokemonInfoViewState.uiState != State.DEFAULT) {
                ErrorFailedView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                    message = pokemonInfoViewState.message,
                    isLoading = pokemonInfoViewState.isLoading,
                    onClickTryAgain = reloadPokemonInfo
                )
            } else if (pokemonInfoViewState.isLoading) {
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
private fun PokemonInfoView(
    modifier: Modifier,
    pokemonInfo: PokemonInfo
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                PokemonImageView(
                    modifier = Modifier.weight(weight = 1f),
                    url = pokemonInfo.frontImageUrl
                )
                PokemonImageView(
                    modifier = Modifier.weight(weight = 1f),
                    url = pokemonInfo.backImageUrl
                )
            }
        }

        item {
            InfoView(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.Abilities),
                content = {
                    Text(text = pokemonInfo.abilities.joinToString())
                }
            )
        }

        item {
            InfoView(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.height),
                subTitle = stringResource(id = R.string.desc_height),
                content = {
                    Text(text = pokemonInfo.height.toString())
                }
            )
        }

        item {
            InfoView(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.stat),
                subTitle = stringResource(id = R.string.desc_stat),
                content = {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                    ) {
                        items(items = pokemonInfo.stats) { stat ->
                            StatItem(
                                modifier = Modifier.width(width = 80.dp),
                                label = stat.name,
                                value = stat.value
                            )
                        }
                    }
                }
            )
        }

        item {
            val ctx = LocalContext.current

            InfoView(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.sound),
                subTitle = stringResource(id = R.string.desc_sound),
                content = {
                    Button(
                        onClick = {
                            val mediaPlayer = MediaPlayer()
                            mediaPlayer.setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build()
                            )

                            try {
                                mediaPlayer.setDataSource(ctx, Uri.parse(pokemonInfo.soundUrl))
                                mediaPlayer.prepare()
                                mediaPlayer.start()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        },
                        content = {
                            Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(width = 4.dp))
                            Text(text = stringResource(id = R.string.play_sound))
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun PokemonImageView(modifier: Modifier, url: String) {
    AsyncImageView(
        modifier = modifier
            .widthIn(min = 100.dp)
            .heightIn(min = 100.dp)
            .clip(shape = RoundedCornerShape(size = 4.dp)),
        imageUrl = url
    )
}

@Composable
private fun InfoView(
    modifier: Modifier,
    title: String,
    subTitle: String = "",
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
        if (subTitle.isNotBlank()) {
            Text(text = subTitle, style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.height(height = 4.dp))

        content()
    }
}

@Composable
private fun StatItem(modifier: Modifier, label: String, value: Int) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value.toString(), style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(height = 8.dp))

            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview
@Composable
private fun PreviewPokemonInfoScreenLayoutInLoadingState() {
    PokedexTheme {
        PokemonInfoScreenLayout(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            pokemonInfoViewState = ViewState(isInitialLoad = true, uiState = State.LOADING),
            reloadPokemonInfo = { /*TODO*/ },
            onClickHome = {}
        )
    }
}

@Preview
@Composable
private fun PreviewPokemonInfoScreenLayoutInFailedState() {
    PokedexTheme {
        PokemonInfoScreenLayout(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            pokemonInfoViewState = ViewState(
                uiState = State.FAILED,
                message = "Some error occurred from the api"
            ),
            reloadPokemonInfo = { /*TODO*/ },
            onClickHome = {}
        )
    }
}

@Preview
@Composable
private fun PreviewPokemonInfoScreenLayoutInSuccessState() {
    PokedexTheme {
        PokemonInfoScreenLayout(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = remember { SnackbarHostState() },
            pokemonInfoViewState = ViewState(
                data = PokemonInfo(
                    id = 35,
                    name = "clefairy",
                    frontImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/35.png",
                    backImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/35.png",
                    species = "clefairy",
                    abilities = listOf("friend-guard"),
                    height = 6,
                    stats = listOf(
                        StatInfo(value = 10, name = "power"),
                        StatInfo(value = 30, name = "hp"),
                    ),
                    soundUrl = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/35.ogg"
                )
            ),
            reloadPokemonInfo = { /*TODO*/ },
            onClickHome = {}
        )
    }
}