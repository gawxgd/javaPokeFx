package com.example.javapokemonfx.team_creation_view;

import javafx.collections.ObservableList;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.List;

public class Team {
    private ObservableList<String> pokemons;

    public Team(ObservableList<String> pokemons) {
        this.pokemons = pokemons;
    }

    public ObservableList<String> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        //this.pokemons = pokemons;
    }
}
