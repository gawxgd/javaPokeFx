package com.example.javapokemonfx;

import com.example.javapokemonfx.pokemon_list_view.PokemonListView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MainView {

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
        String pokemonName = "pikachu";
        pokemonService.fetchAndPrintPokemon(pokemonName);

        pokemonInfoLabel.setText("Fetching " + pokemonName + " data...");
    }

    public VBox getRoot() {
        return root;
    }

    @EventListener
    public void handlePokemonInfoEvent(PokemonService.PokemonInfoEvent event) {
        Platform.runLater(() -> {
            pokemonInfoLabel.setText(event.pokemonInfo());
        });
    }
}
