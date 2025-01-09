package com.example.javapokemonfx.pokemon_details_view;

import com.example.javapokemonfx.PokemonService;
import com.example.javapokemonfx.controllers.MainController;
import com.example.javapokemonfx.pokemon_list_view.PokemonListView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PokemonDetailsView {
    public static final String viewName = "Pokemon Details";
    public static final String fxmlName = "/pokemondetailsview.fxml";

    @FXML
    private ImageView pokemonImage;

    @FXML
    private Label pokemonName;

    @FXML
    private Label pokemonBaseExperience;

    @FXML
    private Label pokemonHeight;

    @FXML
    private Label pokemonWeight;

    @FXML
    private Button backButton;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private ApplicationContext applicationContext;

    private String pokemonNameId = "pikachu";

    // Set the details for the selected Pokémon
    public void setPokemonDetails(String pokemonNameId) {
        this.pokemonNameId = pokemonNameId;
        pokemonService.fetchPokemonDetails(pokemonNameId);
    }

    @FXML
    private void onBackButtonClicked() {
        MainController mainController = applicationContext.getBean(MainController.class);
        mainController.switchView(PokemonListView.viewName);
    }

    @EventListener
    public void handlePokemonDetailsEvent(PokemonService.PokemonDetailsEvent event) {
        Platform.runLater(() -> {
            var details = event.details();
           updatePokemonDetails(details.name(), details.baseExperience(),details.height(),details.weight(),details.imageUrl());
        });
    }

    public void updatePokemonDetails(String name, String baseExperience, String height, String weight, String imageUrl) {
        pokemonName.setText(name);
        pokemonBaseExperience.setText("Base Experience: " + baseExperience);
        pokemonHeight.setText("Height: " + height + " meters");
        pokemonWeight.setText("Weight: " + weight + " kg");
        pokemonImage.setImage(new Image(imageUrl));
    }
}
