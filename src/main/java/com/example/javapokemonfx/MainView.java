package com.example.javapokemonfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MainView {

    public static final String viewName = "Fetch random pokemon";
    public static final String fxmlName = "/mainview.fxml";
    public Label headerLabel;

    @FXML
    private Button helloButton;

    @FXML
    private Label pokemonInfoLabel;

    @Autowired
    private PokemonService pokemonService;

    public void initialize() {
        helloButton.setOnAction(event -> fetchPokemonInfo());
    }

    public void fetchPokemonInfo() {
        pokemonService.fetchPokemonList();
        String pokemonName = pokemonService.getRandomPokemon().getName();
        pokemonService.fetchAndPrintPokemon(pokemonName);
        pokemonInfoLabel.setText("Fetching " + pokemonName + " data...");
    }


    @EventListener
    public void handlePokemonInfoEvent(PokemonService.PokemonInfoEvent event) {
        Platform.runLater(() -> pokemonInfoLabel.setText(event.pokemon().getName()));
    }
}
