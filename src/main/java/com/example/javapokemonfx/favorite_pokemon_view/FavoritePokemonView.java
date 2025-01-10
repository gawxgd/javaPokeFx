package com.example.javapokemonfx.favorite_pokemon_view;

import com.example.javapokemonfx.pokemon_details_view.PokemonDetailsView;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FavoritePokemonView {

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
    private void onBackButtonClicked() {
        // Wróć do poprzedniego widoku
    }
}
