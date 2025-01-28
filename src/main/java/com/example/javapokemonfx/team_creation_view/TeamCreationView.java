package com.example.javapokemonfx.team_creation_view;

import com.example.javapokemonfx.PokemonService;
import com.example.javapokemonfx.battle_view.BattleView;
import com.example.javapokemonfx.battle_view.StartBattleEvent;
import com.example.javapokemonfx.controllers.MainController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeamCreationView {
    public static final String viewName = "Team Creation";
    public static final String fxmlName = "/teamcreationview.fxml";

    @FXML
    private ListView<String> pokemonListView;

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea teamDisplayArea;

    @FXML
    private Button battleButton;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private ApplicationContext applicationContext;

    private List<Pokemon> allPokemons = new ArrayList<>();
    private List<Pokemon> selectedPokemons;
    private List<List<Pokemon>> teams;

    public void initialize() {
        allPokemons = new ArrayList<>();
        selectedPokemons = new ArrayList<>();

        pokemonListView.setItems(FXCollections.observableArrayList());
        teams = new ArrayList<>();

        battleButton.setOnAction(event -> handleStartBattle());

        fetchPokemonList();
        pokemonListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        pokemonListView.setOnMouseClicked(event -> handleSelection());

        updatePokemonList();
        Platform.runLater(this::updatePokemonList);

    }

    private void fetchPokemonList() {
        statusLabel.setText("Fetching Pokémon list...");
        pokemonService.fetchPokemonList();
    }


    private void handleSelection() {
        List<String> selectedItems = pokemonListView.getSelectionModel().getSelectedItems();
        selectedPokemons.clear();

        for (String selectedItem : selectedItems) {
            if (selectedPokemons.size() < 6) {
                allPokemons.stream()
                        .filter(pokemon -> pokemon.getName().equals(selectedItem))
                        .findFirst().ifPresent(selectedPokemon -> selectedPokemons.add(selectedPokemon));

            }
        }
        statusLabel.setText("Selected " + selectedPokemons.size() + " Pokémon, please choose " + (6 - selectedPokemons.size()) + " Pokemon more");
    }

    @FXML
    private void handleCreateTeam() {
        if (selectedPokemons.size() == 6) {
            teams.add(new ArrayList<>(selectedPokemons));
            statusLabel.setText("Team created with 6 Pokémon!");

            StringBuilder teamInfo = new StringBuilder("Your Team:\n");
            for (Pokemon pokemon : selectedPokemons) {
                teamInfo.append(pokemon.getName()).append("\n");
            }
            Platform.runLater(() -> {
                teamDisplayArea.setText(teamInfo.toString());
                teamDisplayArea.setVisible(true);
            });

            Alert teamAlert = new Alert(Alert.AlertType.INFORMATION);
            teamAlert.setTitle("Your Team is ready to battle");
            teamAlert.setHeaderText("Here is your team of Pokémon:");
            teamAlert.setContentText(teamInfo.toString());
            teamAlert.showAndWait();
        } else {
            statusLabel.setText("Please select exactly 6 Pokémon.");
        }
        battleButton.setVisible(selectedPokemons.size() == 6);
    }

    private void updatePokemonList() {
        List<String> pokeNames = allPokemons.stream()
                .map(Pokemon::getName)
                .toList();

        Platform.runLater(() -> {
            pokemonListView.getItems().clear();
            pokemonListView.getItems().addAll(pokeNames);
        });
    }

    @EventListener
    public void handlePokemonListEvent(PokemonService.PokemonListEvent event) {
        if (allPokemons == null) {
            allPokemons = new ArrayList<>();
        }
        Platform.runLater(() -> {
            for (var name : event.pokemonNames()) {
                pokemonService.fetchAndPrintPokemon(name);
            }
            statusLabel.setText("Pokémon list loaded.");
        });
    }

    @EventListener
    public void handlePokemonEvent(PokemonService.PokemonInfoEvent event) {
        if (allPokemons == null) {
            allPokemons = new ArrayList<>();
        }
        allPokemons.add(event.pokemon());
        Platform.runLater(this::updatePokemonList);
    }

    @FXML
    private void handleStartBattle() {
        if (selectedPokemons.size() == 6) {
            applicationContext.publishEvent(new StartBattleEvent(this, selectedPokemons));
            switchViewToBattle();
        } else {
            statusLabel.setText("Please select exactly 6 Pokémon.");
        }
    }

    private void switchViewToBattle() {
        MainController mainController = applicationContext.getBean(MainController.class);
        mainController.switchView(BattleView.viewName);
    }

}
