package com.example.javapokemonfx.pokemon_details_view;

import com.example.javapokemonfx.PokemonService;
import com.example.javapokemonfx.berry_list_view.BerryListView;
import com.example.javapokemonfx.controllers.MainController;
import com.example.javapokemonfx.pokemon_list_view.PokemonListView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @FXML
    private Button addToFavoritesButton;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private ApplicationContext applicationContext;

    private String pokemonNameId = "pikachu";
    private String feedingBerryName = "";
    private String baseImageUrl = "";
    private int happinessLevel = 1;

    private String feedingName = "";

    private Set<String> favoritePokemonSet = new HashSet<>();


    // Set the details for the selected Pokémon
    public void setPokemonDetails(String pokemonNameId) {
        feedingBerryName = "";
        var pokeArgs = pokemonNameId.split(" ");
        System.out.println(Arrays.toString(pokeArgs));
        if(pokeArgs.length > 1)
        {
            this.pokemonNameId = pokeArgs[0];
            feedingBerryName = pokeArgs[1];
            feedingName = this.pokemonNameId;
            pokemonService.fetchPokemonDetails(this.pokemonNameId);
        }
        else {
            this.pokemonNameId = pokemonNameId;
            feedingName = this.pokemonNameId;
            pokemonService.fetchPokemonDetails(pokemonNameId);
        }
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
            baseImageUrl = details.imageUrl();
            System.out.println("Base URL: " + baseImageUrl);
            updatePokemonDetails(details.name(), details.baseExperience(),details.height(),details.weight(),details.imageUrl());

            if(!Objects.equals(feedingBerryName, ""))
                feedPokemon();
        });
    }

    public void updatePokemonDetails(String name, String baseExperience, String height, String weight, String imageUrl) {
        pokemonName.setText(name);
        pokemonBaseExperience.setText("Base Experience: " + baseExperience);
        pokemonHeight.setText("Height: " + height + " meters");
        pokemonWeight.setText("Weight: " + weight + " kg");
        pokemonImage.setImage(new Image(imageUrl));

        //System.out.println("Generated URL: " + imageUrl);
    }

    private void switchViewToBerryList(String pokemonName) {
        MainController mainController = applicationContext.getBean(MainController.class);
        System.out.println("gowno" + pokemonName);
        mainController.switchView(BerryListView.afterFeedViewName, pokemonName);
    }

    private void feedPokemon()
    {
        happinessLevel++;
        if(happinessLevel == 4) {
            happinessLevel = 1;
        }
        String newImageUrl = generatePokemonImageUrl(baseImageUrl, happinessLevel);
        pokemonImage.setImage(new Image(newImageUrl));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feed Pokémon");
        alert.setHeaderText("You have fed the Pokémon with "+ feedingBerryName + "!");
        alert.setContentText("The Pokémon is now happier.");
        alert.showAndWait();
    }

    @FXML
    private void onFeedButtonClicked() {
        switchViewToBerryList(feedingName);
    }

    private String generatePokemonImageUrl(String baseImageUrl, int evolutionStage) {

        if (baseImageUrl == null || baseImageUrl.isEmpty()) {
            return "";
        }

        String id = extractPokemonIdFromUrl(baseImageUrl);

        String baseUrlPart = baseImageUrl.substring(0, baseImageUrl.lastIndexOf(id));

        String extensionPart = baseImageUrl.substring(baseImageUrl.lastIndexOf(".png"));

        String evolutionUrl;
        switch (evolutionStage) {
            case 1:
                // Zwykły obrazek
                evolutionUrl = baseUrlPart + id + extensionPart;
                break;
            case 2:
                // Official artwork
                evolutionUrl = baseUrlPart + "other/official-artwork/" + id + extensionPart;
                break;
            case 3:
                // Shiny version
                evolutionUrl = baseUrlPart + "shiny/" + id + extensionPart;
                break;
            default:
                // Domyślnie wracamy do zwykłego obrazka
                evolutionUrl = baseUrlPart + id + extensionPart;
        }

        return evolutionUrl;
    }

    public String extractPokemonIdFromUrl(String url) {
        try {
            System.out.println("Generated URL: " + url);

            String withoutExtension = url.substring(0, url.lastIndexOf(".png"));

            String[] urlParts = withoutExtension.split("/");
            return urlParts[urlParts.length - 1];
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: cannot find ID in the URL", e);
        }
    }

    @FXML
    private void onAddToFavoritesButtonClicked() {
        if (!favoritePokemonSet.contains(pokemonNameId)) {
            favoritePokemonSet.add(pokemonNameId);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Added to Favorites");
            alert.setHeaderText("You have added " + pokemonNameId + " to your favorites!");
            alert.setContentText("You can view it in the 'Favorite Pokémon' section.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Already a Favorite");
            alert.setHeaderText(pokemonNameId + " is already in your favorites!");
            alert.showAndWait();
        }
    }

    public Set<String> getFavoritePokemonSet() {
        return favoritePokemonSet;
    }





}
