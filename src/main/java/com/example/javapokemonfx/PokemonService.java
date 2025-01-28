package com.example.javapokemonfx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.resource.NamedApiResource;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.berry.Berry;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PokemonService {

    @Autowired
    private PokeApiClient pokeApiClient;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    private List<String> availablePokemonNames = new ArrayList<>();

    public void fetchAndPrintPokemon(String pokemonName) {
        pokeApiClient.getResource(Pokemon.class, pokemonName)
                .doOnNext(pokemon -> eventPublisher.publishEvent(new PokemonInfoEvent(pokemon)))
                .subscribe();
    }
    public void fetchPokemonList() {
        pokeApiClient.getResource(Pokemon.class)
                .map(NamedApiResourceList::getResults)
                .map(resources -> resources.stream()
                        .map(NamedApiResource::getName)
                        .collect(Collectors.toList()))
                .doOnNext(names -> {
                    availablePokemonNames = names;
                    eventPublisher.publishEvent(new PokemonListEvent(names));
                })
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

    public void fetchBerry(String berryName) {
        pokeApiClient.getResource(Berry.class, berryName)
                .doOnNext(berry -> eventPublisher.publishEvent(new BerryEvent(berry)))
                .subscribe();
        }

    public void fetchBerryNameList() {
        pokeApiClient.getResource(Berry.class)
                .map(NamedApiResourceList::getResults)
                .map(resources -> resources.stream()
                        .map(NamedApiResource::getName)
                        .collect(Collectors.toList()))
                .doOnNext(names -> {
                    eventPublisher.publishEvent(new BerryListEvent(names));})
                .subscribe();
    }

    public Pokemon getRandomPokemon() {
        if (availablePokemonNames.isEmpty()) {
            throw new IllegalStateException("Pokemon list is empty. Please fetch the list first.");
        }

        Random random = new Random();
        String randomPokemonName = availablePokemonNames.get(random.nextInt(availablePokemonNames.size()));

        return pokeApiClient.getResource(Pokemon.class, randomPokemonName).block();
    }


    public record PokemonInfoEvent(Pokemon pokemon) {
    }

    public record PokemonListEvent(List<String> pokemonNames) {
    }

    public record PokemonDetails(String name, String baseExperience, String height, String weight, String imageUrl) {
    }

    public record PokemonDetailsEvent(PokemonDetails details) {
    }

    public record BerryEvent(Berry berry) {}

    public record BerryListEvent(List<String> berryName) {}
}
