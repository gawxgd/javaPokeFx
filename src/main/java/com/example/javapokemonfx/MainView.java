package com.example.javapokemonfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MainView {

    public static final String viewName = "Fetch random pokemon";
    public static final String fxmlName = "/mainview.fxml";
    public ImageView pokemonImage;

    @FXML
    private Button helloButton;

    @FXML
    private VBox root;

    @FXML
    private Label pokemonInfoLabel;  // Label to display Pokémon info

    @Autowired
    private PokemonService pokemonService;

    public void initialize() {
        helloButton.setOnAction(event -> fetchPokemonInfo());
    }

    public void fetchPokemonInfo() {
        pokemonService.fetchPokemonList(60, 0);
        String pokemonName = pokemonService.getRandomPokemon().getName();
        pokemonService.fetchAndPrintPokemon(pokemonName);
        pokemonInfoLabel.setText("Fetching " + pokemonName + " data...");
    }

    public VBox getRoot() {
        return root;
    }

    @EventListener
    public void handlePokemonInfoEvent(PokemonService.PokemonInfoEvent event) {
        Platform.runLater(() -> pokemonInfoLabel.setText(event.pokemon().getName()));
    }
}
