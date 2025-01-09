package com.example.javapokemonfx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.List;
import java.util.stream.Collectors;

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
                .doOnNext(info -> eventPublisher.publishEvent(new PokemonInfoEvent(info)))
                .subscribe();
    }

    public void fetchPokemonList(int limit, int offset) {
        pokeApiClient.getResource(Pokemon.class)
                .map(NamedApiResourceList::getResults)
                .map(resources -> resources.stream()
                        .map(NamedApiResource::getName)
                        .collect(Collectors.toList()))
                .doOnNext(names -> {
                    System.out.println("Fetched Pokémon List: " + names);
                    eventPublisher.publishEvent(new PokemonListEvent(names));})
                .subscribe();
    }

    public void fetchPokemonDetails(String pokemonId) {
        pokeApiClient.getResource(Pokemon.class, pokemonId)
                .map(pokemon -> {
                    String name = pokemon.getName();
                    String baseExperience = String.valueOf(pokemon.getBaseExperience());
                    String height = String.valueOf(pokemon.getHeight());
                    String weight = String.valueOf(pokemon.getWeight());
                    String imageUrl = pokemon.getSprites().getFrontDefault();

                    return new PokemonDetails(name, baseExperience, height, weight, imageUrl);
                })
                .doOnNext(details -> eventPublisher.publishEvent(new PokemonDetailsEvent(details)))
                .subscribe();
    }

    public record PokemonInfoEvent(String pokemonInfo) {
    }

    public record PokemonListEvent(List<String> pokemonNames) {
    }

    public record PokemonDetails(String name, String baseExperience, String height, String weight, String imageUrl) {
    }

    public record PokemonDetailsEvent(PokemonDetails details) {
    }
}
