package com.example.javapokemonfx.team_creation_view;

import com.example.javapokemonfx.PokemonService;
import com.example.javapokemonfx.controllers.MainController;
import com.example.javapokemonfx.pokemon_details_view.PokemonDetailsView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TeamCreationView {
    public static final String viewName = "Team Creation";
    public static final String fxmlName = "/teamcreationview.fxml";

    @FXML
    private ListView<String> pokemonListView;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox root;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private ApplicationContext applicationContext;

    private List<Pokemon> allPokemons;
    private List<Pokemon> selectedPokemons;

    public void initialize() {
        allPokemons = new ArrayList<>();
        selectedPokemons = new ArrayList<>();
        pokemonListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        pokemonListView.setOnMouseClicked(event -> handleSelection());
        fetchPokemonList();
    }

    private void fetchPokemonList() {
        statusLabel.setText("Fetching Pokémon list...");
        pokemonService.fetchPokemonList(20, 0);
    }

    private void handleSelection() {
        List<String> selectedItems = pokemonListView.getSelectionModel().getSelectedItems();
        selectedPokemons.clear();

        // Check if the selected list contains up to 6 Pokémon
        for (String selectedItem : selectedItems) {
            if (selectedPokemons.size() < 6) {
                Pokemon selectedPokemon = allPokemons.stream()
                        .filter(pokemon -> pokemon.getName().equals(selectedItem))
                        .findFirst().orElse(null);

                if (selectedPokemon != null) {
                    selectedPokemons.add(selectedPokemon);
                }
            }
        }

        statusLabel.setText("Selected " + selectedPokemons.size() + " Pokémon");
    }

    @FXML
    private void handleCreateTeam() {
        if (selectedPokemons.size() == 6) {
            statusLabel.setText("Team created with 6 Pokémon!");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Team Created");
                alert.setHeaderText("You have successfully created a team of Pokémon!");
                alert.setContentText("Your team contains " + selectedPokemons.size() + " Pokémon.");
                alert.showAndWait();


        } else {
            statusLabel.setText("Please select exactly 6 Pokémon.");
        }
    }

    private void updatePokemonList() {
        List<String> pokeNames = allPokemons.stream()
                .map(pokemon -> pokemon.getName())
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            pokemonListView.getItems().clear();
            pokemonListView.getItems().addAll(pokeNames);
        });
    }

    @EventListener
    public void handlePokemonListEvent(PokemonService.PokemonListEvent event) {
        Platform.runLater(() -> {
            List<String> allPokemonNames = event.pokemonNames();
            for(var name : event.pokemonNames()){
                pokemonService.fetchAndPrintPokemon(name);
            }
            statusLabel.setText("Pokémon list loaded.");
        });
    }

    @EventListener
    public void handlePokemonEvent(PokemonService.PokemonInfoEvent event) {
        allPokemons.add(event.pokemon());
        Platform.runLater(this::updatePokemonList);
    }

    public VBox getRoot() {
        return root;
    }
}
