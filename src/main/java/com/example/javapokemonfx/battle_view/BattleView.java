package com.example.javapokemonfx.battle_view;

import com.example.javapokemonfx.PokemonService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.context.ApplicationEvent;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.List;


@Component
public class BattleView {
    public static final String viewName = "Battle";
    public static final String fxmlName = "/battleview.fxml";

    @FXML
    private Label teamLabel;

    @FXML
    private ListView<String> playerPokemonList;

    @FXML
    private Label opponentLabel;

    @FXML
    private ListView<String> opponentPokemonList;

    @Autowired
    private PokemonService pokemonService;


    @Autowired
    private ApplicationContext applicationContext;

    private List<Pokemon> opponentTeam;

    @EventListener
    public void handleStartBattle(StartBattleEvent event) {
        playerPokemonList = new ListView<>();
        List<Pokemon> playerTeam = event.getSelectedTeam();
        List<String> playerTeamNames = new ArrayList<>();
        if (playerTeam == null || playerTeam.isEmpty()) {
            System.out.println("Error: Player team is empty or null.");
            return;  // Handle the error
        }
        for (Pokemon pokemon : playerTeam) {
            playerTeamNames.add(pokemon.getName());
            playerPokemonList.getItems().add(pokemon.getName());
        }

        opponentPokemonList = new ListView<>();
        opponentTeam = getRandomOpponentTeam(playerTeam);
        List<String> opponentTeamNames = new ArrayList<>();

        for (Pokemon pokemon : opponentTeam) {
            opponentTeamNames.add(pokemon.getName());
        }

        playerPokemonList.getItems().setAll(playerTeamNames);
        opponentPokemonList.getItems().setAll(opponentTeamNames);
    }

    private List<Pokemon> getRandomOpponentTeam(List<Pokemon> playerTeam) {
        List<Pokemon> randomTeam = new ArrayList<>();
        Random rand = new Random();

        while (randomTeam.size() < 6) {
            Pokemon randomPokemon = pokemonService.getRandomPokemon();
            if (!randomTeam.contains(randomPokemon) && !playerTeam.contains(randomPokemon)) {
                randomTeam.add(randomPokemon);
            }
        }

        return randomTeam;
    }

}
