package com.example.javapokemonfx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.pokemon.Pokemon;

@Service
public class PokemonService {

    @Autowired
    private PokeApiClient pokeApiClient;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void fetchAndPrintPokemon(String pokemonName) {
        pokeApiClient.getResource(Pokemon.class, pokemonName)
                .map(pokemon -> String.format(
                        "Name: %s\nBase Experience: %d\nHeight: %d\nWeight: %d",
                        pokemon.getName(),
                        pokemon.getBaseExperience(),
                        pokemon.getHeight(),
                        pokemon.getWeight()))
                .doOnNext(info -> eventPublisher.publishEvent(new PokemonInfoEvent(info)))  // Publish the event to notify about the Pokémon info
                .subscribe();
    }

    public static class PokemonInfoEvent {
        private final String pokemonInfo;

        public PokemonInfoEvent(String pokemonInfo) {
            this.pokemonInfo = pokemonInfo;
        }

        public String getPokemonInfo() {
            return pokemonInfo;
        }
    }
}
