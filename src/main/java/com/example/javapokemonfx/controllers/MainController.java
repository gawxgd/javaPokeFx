package com.example.javapokemonfx.controllers;

import com.example.javapokemonfx.MainView;
import com.example.javapokemonfx.pokemon_list_view.PokemonListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainController {

    @FXML
    private ListView<String> sidebar;

    @FXML
    private StackPane contentArea;

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    public void initialize() {
        // Add navigation items
        sidebar.getItems().addAll("Main View", "Pokemon List");

        // Handle sidebar selection
        sidebar.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                switchView(newSelection);
            }
        });

        // Load the default view
        switchView("Main View");
    }

    private void switchView(String viewName) {
        contentArea.getChildren().clear(); // Clear the current view

        switch (viewName) {
            case "Main View" -> {
                FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/mainview.fxml"));
                mainLoader.setControllerFactory(applicationContext::getBean);
                try {
                    contentArea.getChildren().add(mainLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case "Pokemon List" -> {
                FXMLLoader pokemonLoader = new FXMLLoader(getClass().getResource("/pokemonlistview.fxml"));
                pokemonLoader.setControllerFactory(applicationContext::getBean);
                try {
                    contentArea.getChildren().add(pokemonLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            default -> throw new IllegalArgumentException("Unknown view: " + viewName);
        }
    }
}
