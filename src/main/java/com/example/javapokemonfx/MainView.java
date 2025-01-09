package com.example.javapokemonfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MainView {

    @FXML
    private Button helloButton;

    @FXML
    private StackPane root;

    @FXML
    private Label pokemonInfoLabel;  // Label to display Pokémon info

    @Autowired
    private PokemonService pokemonService;

    public void initialize() {
        helloButton.setOnAction(event -> fetchPokemonInfo());
    }

    // Method to fetch and display Pokémon info
    public void fetchPokemonInfo() {
        String pokemonName = "pikachu";
        pokemonService.fetchAndPrintPokemon(pokemonName);

        // Update label with a placeholder message or fetched data
        pokemonInfoLabel.setText("Fetching " + pokemonName + " data...");
    }

    public StackPane getRoot() {
        return root;
    }

    @EventListener
    public void handlePokemonInfoEvent(PokemonService.PokemonInfoEvent event) {
        // Ensure the label update happens on the JavaFX Application thread
        Platform.runLater(() -> {
            pokemonInfoLabel.setText(event.getPokemonInfo());
        });
    }
}
