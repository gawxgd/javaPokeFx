package com.example.javapokemonfx.favorite_pokemon_view;

import com.example.javapokemonfx.pokemon_details_view.PokemonDetailsView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FavoritePokemonView {
    public static final String viewName = "Favorite Pokemon";
    public static final String fxmlName = "/favoritepokemonview.fxml";

    @FXML
    private ListView<String> favoritePokemonList;

    @Autowired
    private PokemonDetailsView pokemonDetailsView;

    @FXML
    public void initialize() {
        updateFavoritePokemonList();
    }

    public void updateFavoritePokemonList() {
        Set<String> favoritePokemons = pokemonDetailsView.getFavoritePokemonSet();
        favoritePokemonList.getItems().setAll(favoritePokemons);
    }

    @FXML
    private void onDeleteButtonClicked() {
        String selectedPokemon = favoritePokemonList.getSelectionModel().getSelectedItem();

        if (selectedPokemon == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Pokémon Selected");
            alert.setHeaderText("No Pokémon Selected");
            alert.setContentText("Please select a Pokémon to delete.");
            alert.showAndWait();
            return;
        }

        Set<String> favoritePokemons = pokemonDetailsView.getFavoritePokemonSet();
        favoritePokemons.remove(selectedPokemon);

        favoritePokemonList.getItems().remove(selectedPokemon);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pokémon Removed");
        alert.setHeaderText("Pokémon Removed");
        alert.setContentText(selectedPokemon + " has been removed from your favorites.");
        alert.showAndWait();
    }
}
