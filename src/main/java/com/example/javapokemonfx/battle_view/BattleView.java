package com.example.javapokemonfx.battle_view;

import com.example.javapokemonfx.PokemonService;
import com.example.javapokemonfx.controllers.MainController;
import com.example.javapokemonfx.team_creation_view.TeamCreationView;
import javafx.application.Platform;
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
    private ListView<String> playerPokemonList = new ListView<>();

    @FXML
    private Label opponentLabel;

    @FXML
    private ListView<String> opponentPokemonList = new ListView<>();

    @Autowired
    private PokemonService pokemonService;


    @Autowired
    private ApplicationContext applicationContext;

    private List<Pokemon> opponentTeam;
    private List<Pokemon> playerTeam;
    @EventListener
    public void handleStartBattle(StartBattleEvent event) {
        playerTeam = event.getSelectedTeam();
        List<String> playerTeamNames = new ArrayList<>();

        if (playerTeam == null || playerTeam.isEmpty()) {
            System.out.println("Error: Player team is empty or null.");
            return;
        }

        for (Pokemon pokemon : playerTeam) {
            playerTeamNames.add(pokemon.getName());

            //System.out.println(pokemon.getName());
        }

        //playerPokemonList.getItems().setAll(playerTeamNames);

        opponentTeam = getRandomOpponentTeam(playerTeam);
        List<String> opponentTeamNames = new ArrayList<>();
        for (Pokemon pokemon : opponentTeam) {
            opponentTeamNames.add(pokemon.getName());
        }

        //opponentPokemonList.getItems().setAll(opponentTeamNames);

        Platform.runLater(() -> {
            playerPokemonList.getItems().setAll(playerTeamNames);
            opponentPokemonList.getItems().setAll(opponentTeamNames);
        });
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

    @FXML
    private void handleFight() {
        int playerTeamExperience = calculateTotalExperience(playerPokemonList.getItems(), playerTeam);
        int opponentTeamExperience = calculateTotalExperience(opponentPokemonList.getItems(), opponentTeam);

        String result = playerTeamExperience > opponentTeamExperience ? "Player wins!" : "Opponent wins!";

        teamLabel.setText(result + " (Player Experience: " + playerTeamExperience + ", Opponent Experience: " + opponentTeamExperience + ")");
    }

    private int calculateTotalExperience(List<String> pokemonNames, List<Pokemon> team) {
        int totalExperience = 0;

        for (String name : pokemonNames) {
            Pokemon pokemon = getPokemonByName(name, team);
            if (pokemon != null) {
                totalExperience += pokemon.getBaseExperience();
            }
        }

        return totalExperience;
    }

    private Pokemon getPokemonByName(String name, List<Pokemon> team) {
        for (Pokemon pokemon : team) {
            if (pokemon.getName().equals(name)) {
                return pokemon;
            }
        }

        return null;
    }

    @FXML
    private void handleNewTeam() {
        playerTeam = new ArrayList<>();

        playerPokemonList.getItems().clear();
        opponentPokemonList.getItems().clear();

        switchToTeamCreationView();
    }

    private void switchToTeamCreationView() {
        MainController mainController = applicationContext.getBean(MainController.class);
        mainController.switchView(TeamCreationView.viewName);
    }

}