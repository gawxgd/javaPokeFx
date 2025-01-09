package com.example.javapokemonfx.pokemon_list_view;

import com.example.javapokemonfx.PokemonService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PokemonListView {

    @FXML
    private ListView<String> pokemonListView;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox root;

    @Autowired
    private PokemonService pokemonService;

    public void initialize() {
        fetchPokemonList();
        pokemonListView.setOnMouseClicked(event -> handleSelection());
    }

    private void fetchPokemonList() {
        statusLabel.setText("Fetching Pokémon list...");
        pokemonService.fetchPokemonList(20, 0); // Fetch 20 Pokémon starting from offset 0
    }

    private void handleSelection() {
        String selectedPokemon = pokemonListView.getSelectionModel().getSelectedItem();
        if (selectedPokemon != null) {
            statusLabel.setText("Selected: " + selectedPokemon);
        }
    }

    @EventListener
    public void handlePokemonListEvent(PokemonService.PokemonListEvent event) {
        Platform.runLater(() -> {
            pokemonListView.getItems().clear();
            pokemonListView.getItems().addAll(event.pokemonNames());
            statusLabel.setText("Pokémon list loaded.");
        });
    }

    public VBox getRoot() {
        return root;
    }
}
