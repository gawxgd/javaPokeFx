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
                .doOnNext(info -> eventPublisher.publishEvent(new PokemonInfoEvent(info)))  // Publish the event to notify about the Pokémon info
                .subscribe();
    }

    public void fetchPokemonList(int limit, int offset) {
        pokeApiClient.getResource(Pokemon.class)
                .map(NamedApiResourceList::getResults) // Extract the list of `NamedApiResource`
                .map(resources -> resources.stream()
                        .map(NamedApiResource::getName) // Extract Pokémon names
                        .collect(Collectors.toList())) // Convert to a list of names
                .doOnNext(names -> {
                    System.out.println("Fetched Pokémon List: " + names);
                    eventPublisher.publishEvent(new PokemonListEvent(names));}) // Publish the Pokémon list event
                .subscribe();
    }


    public record PokemonInfoEvent(String pokemonInfo) {
    }

    public record PokemonListEvent(List<String> pokemonNames) {
    }
}
