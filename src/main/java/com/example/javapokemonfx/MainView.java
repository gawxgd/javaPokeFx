package com.example.javapokemonfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MainView {

    @FXML
    private Button helloButton;

    @FXML
    private BorderPane root;

    @FXML
    private Label pokemonInfoLabel;  // Label to display Pokémon info

    @FXML
    private ListView<String> sidebar;  // Sidebar ListView

    @Autowired
    private PokemonService pokemonService;

    public void initialize() {
        helloButton.setOnAction(event -> fetchPokemonInfo());

        // Add listener for selection changes in the sidebar (ListView)
        sidebar.setOnMouseClicked(event -> handleSidebarSelection(event));
    }

    // Method to fetch and display Pokémon info
    public void fetchPokemonInfo() {
        String pokemonName = "pikachu";
        pokemonService.fetchAndPrintPokemon(pokemonName);

        // Update label with a placeholder message or fetched data
        pokemonInfoLabel.setText("Fetching " + pokemonName + " data...");
    }

    // Method to handle the selection of different sidebar items
    private void handleSidebarSelection(MouseEvent event) {
        String selectedItem = sidebar.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            switch (selectedItem) {
                case "Sidebar Item 1":
                    pokemonInfoLabel.setText("You selected Sidebar Item 1");
                    break;
                case "Sidebar Item 2":
                    pokemonInfoLabel.setText("You selected Sidebar Item 2");
                    break;
                case "Sidebar Item 3":
                    pokemonInfoLabel.setText("You selected Sidebar Item 3");
                    break;
                default:
                    pokemonInfoLabel.setText("Please select a Sidebar Item...");
                    break;
            }
        }
    }

    public BorderPane getRoot() {
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
