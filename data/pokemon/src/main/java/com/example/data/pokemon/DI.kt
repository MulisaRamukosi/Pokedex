package com.example.data.pokemon

import com.example.data.pokemon.usecase.PokemonUseCase
import com.example.data.pokemon.usecase.PokemonUseCaseImpl
import com.example.network.ApiClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DI {

    @Singleton
    @Provides
    fun providePokemonUseCase(): PokemonUseCase = PokemonUseCaseImpl(
        pokemonRepository = ApiClientFactory.buildApiClient()
    )

}