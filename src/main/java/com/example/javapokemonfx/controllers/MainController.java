package com.example.javapokemonfx.controllers;

import com.example.javapokemonfx.MainView;
import com.example.javapokemonfx.battle_view.BattleView;
import com.example.javapokemonfx.berry_list_view.BerryListView;
import com.example.javapokemonfx.pokemon_details_view.PokemonDetailsView;
import com.example.javapokemonfx.pokemon_list_view.PokemonListView;
import com.example.javapokemonfx.team_creation_view.TeamCreationView;
import com.example.javapokemonfx.favorite_pokemon_view.FavoritePokemonView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class MainController {

    @FXML
    private ListView<String> sidebar;

    @FXML
    private StackPane contentArea;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired PokemonDetailsView pokemonDetailsView;

    @FXML
    public void initialize() {
        // Add navigation items
        sidebar.getItems().addAll(MainView.viewName, PokemonListView.viewName, BerryListView.viewName,
                TeamCreationView.viewName, FavoritePokemonView.viewName);

        // Handle sidebar selection
        sidebar.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                switchView(newSelection);
            }
        });

        // Load the default view
        switchView(MainView.viewName);
    }

    public void switchView(String viewName)
    {
        switchView(viewName,"");
    }

    public void switchView(String viewName, String args) {
        contentArea.getChildren().clear(); // Clear the current view

        switch (viewName) {
            case MainView.viewName -> {
                FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(MainView.fxmlName));
                mainLoader.setControllerFactory(applicationContext::getBean);
                try {
                    contentArea.getChildren().add(mainLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case PokemonListView.viewName -> NavigateToPokemonList();
            case PokemonDetailsView.viewName -> NavigateToPokemonDetails(args);
            case BerryListView.viewName -> NavigateToBerriesList("");
            case TeamCreationView.viewName -> NavigateToTeamCreation();
            case BattleView.viewName -> NavigateToBattle();
            case FavoritePokemonView.viewName -> NavigateToFavorite();
            case BerryListView.afterFeedViewName -> NavigateToBerriesList(args);

            default -> throw new IllegalArgumentException("Unknown view: " + viewName);
        }
    }

    private void NavigateToBattle() {
        FXMLLoader battleLoader = new FXMLLoader(getClass().getResource(BattleView.fxmlName));
        battleLoader.setControllerFactory(applicationContext::getBean);
        try {
            contentArea.getChildren().add(battleLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void NavigateToFavorite() {
        FXMLLoader battleLoader = new FXMLLoader(getClass().getResource(FavoritePokemonView.fxmlName));
        battleLoader.setControllerFactory(applicationContext::getBean);
        try {
            contentArea.getChildren().add(battleLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void NavigateToPokemonList()
    {
        FXMLLoader pokemonLoader = new FXMLLoader(getClass().getResource(PokemonListView.fxmlName));
        pokemonLoader.setControllerFactory(applicationContext::getBean);
        try {
            contentArea.getChildren().add(pokemonLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void NavigateToPokemonDetails(String args){
        System.out.println("Pokemon details are " + args);
        FXMLLoader pokemonDetailsLoader = new FXMLLoader(getClass().getResource(PokemonDetailsView.fxmlName));

        if(!Objects.equals(args, ""))
        {

            pokemonDetailsLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == PokemonDetailsView.class) {
                    PokemonDetailsView controller = applicationContext.getBean(PokemonDetailsView.class);
                    controller.setPokemonDetails(args);
                    return controller;
                }
                return null;
            });
        }
        else{
            pokemonDetailsLoader.setControllerFactory(applicationContext::getBean);
        }
        try {
            contentArea.getChildren().add(pokemonDetailsLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void NavigateToBerriesList(String args){
        FXMLLoader berriesLoader = new FXMLLoader(getClass().getResource(BerryListView.fxmlName));


            berriesLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == BerryListView.class) {
                    BerryListView controller = applicationContext.getBean(BerryListView.class);
                    controller.setPokemonToFeed(args);
                    return controller;
                }
                return null;
            });
        try {
            contentArea.getChildren().add(berriesLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void NavigateToTeamCreation() {
        FXMLLoader teamCreationLoader = new FXMLLoader(getClass().getResource(TeamCreationView.fxmlName));
        teamCreationLoader.setControllerFactory(applicationContext::getBean);

        try {
            contentArea.getChildren().add(teamCreationLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
