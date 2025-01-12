package com.example.javapokemonfx.pokemon_list_view;

import com.example.javapokemonfx.PokemonService;
import com.example.javapokemonfx.controllers.MainController;
import com.example.javapokemonfx.pokemon_details_view.PokemonDetailsView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PokemonListView {
    public static final String viewName = "Pokemon List";
    public static final String fxmlName = "/pokemonlistview.fxml";
    public Button clearFiltersButton;

    @FXML
    private TextField filterTextField;

    @FXML
    private TextField minHeightFilterTextField;
    @FXML
    private TextField maxHeightFilterTextField;

    @FXML
    private TextField minWeightFilterTextField;
    @FXML
    private TextField maxWeightFilterTextField;

    @FXML
    private TextField minExperienceFilterTextField;
    @FXML
    private TextField maxExperienceFilterTextField;

    @FXML
    private ListView<String> pokemonListView;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox root;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private ApplicationContext applicationContext;

    private List<String> allPokemonNames;
    private List<Pokemon> allPokemons;

    public void initialize() {
        allPokemons = new ArrayList<>();
        fetchPokemonList();
        filterTextField.textProperty().addListener((observable, oldText, newText) -> filterPokemonList());
        minHeightFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterPokemonList());
        maxHeightFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterPokemonList());
        minWeightFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterPokemonList());
        maxWeightFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterPokemonList());
        minExperienceFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterPokemonList());
        maxExperienceFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterPokemonList());
        pokemonListView.setOnMouseClicked(event -> handleSelection());
    }

    private void fetchPokemonList() {
        statusLabel.setText("Fetching Pokémon list...");
        pokemonService.fetchPokemonList(20, 0); // Fetch 20 Pokémon starting from offset 0
    }

    private void handleSelection() {
        String selectedPokemon = pokemonListView.getSelectionModel().getSelectedItem();
        if (selectedPokemon != null) {
            statusLabel.setText("Selected: " + selectedPokemon);
            var selectedStringSplit = selectedPokemon.split(" ");
            String pokemonName = selectedStringSplit[0];

            switchViewToPokemonDetails(pokemonName);
        }
    }

    private void switchViewToPokemonDetails(String pokemonName) {
        MainController mainController = applicationContext.getBean(MainController.class);
        mainController.switchView(PokemonDetailsView.viewName, pokemonName);
    }

    @FXML
    private void handleClearFilters() {
        filterTextField.clear();
        minHeightFilterTextField.clear();
        maxHeightFilterTextField.clear();
        minWeightFilterTextField.clear();
        maxWeightFilterTextField.clear();
        minExperienceFilterTextField.clear();
        maxExperienceFilterTextField.clear();

        filterPokemonList();
    }

    private void filterPokemonList() {
        if (allPokemons != null) {
            String nameFilter = filterTextField.getText().toLowerCase();

            int minHeight = parseInteger(minHeightFilterTextField.getText(), 0);
            int maxHeight = parseInteger(maxHeightFilterTextField.getText(), Integer.MAX_VALUE);

            int minWeight = parseInteger(minWeightFilterTextField.getText(), 0);
            int maxWeight = parseInteger(maxWeightFilterTextField.getText(), Integer.MAX_VALUE);

            int minExperience = parseInteger(minExperienceFilterTextField.getText(), 0);
            int maxExperience = parseInteger(maxExperienceFilterTextField.getText(), Integer.MAX_VALUE);

            List<String> filteredPokemon = allPokemons.stream()
                    .filter(Objects::nonNull)
                    .filter(pokemon -> pokemon.getName().toLowerCase().contains(nameFilter)) // Filter by name

                    // Filter by height if in bounds
                    .filter(pokemon -> isWithinBounds(pokemon.getHeight(), minHeight, maxHeight))

                    // Filter by weight if in bounds
                    .filter(pokemon -> isWithinBounds(pokemon.getWeight(), minWeight, maxWeight))

                    // Filter by experience if in bounds
                    .filter(pokemon -> isWithinBounds(pokemon.getBaseExperience(), minExperience, maxExperience))

                    // Format the filtered Pokémon into a string
                    .map(pokemon -> String.format("%s (Height: %d, Weight: %d, Exp: %d)",
                            pokemon.getName(), pokemon.getHeight(), pokemon.getWeight(), pokemon.getBaseExperience()))
                    .collect(Collectors.toList());

            // Update the ListView
            pokemonListView.getItems().setAll(filteredPokemon);
        }
    }

    private Integer parseInteger(String text, Integer defaultValue) {
        try {
            return text != null && !text.isEmpty() ? Integer.parseInt(text) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean isWithinBounds(Integer value, Integer min, Integer max) {
        return value != null && value >= min && value <= max;
    }

    private void PokemonsToNames() {
        List<String> pokeNames;

        if (allPokemons != null) {
            pokeNames = allPokemons.stream()
                    .filter(Objects::nonNull)
                    .map(pokemon -> String.format("%s (Height: %d, Weight: %d, Exp: %d)",
                            pokemon.getName() != null ? pokemon.getName() : "Unknown",
                            pokemon.getHeight() != null ? pokemon.getHeight() : 0,
                            pokemon.getWeight() != null ? pokemon.getWeight() : 0,
                            pokemon.getBaseExperience() != null ? pokemon.getBaseExperience() : 0))
                    .toList();
        } else {
            pokeNames = new ArrayList<>();
        }

        Platform.runLater(() -> {
            pokemonListView.getItems().clear();
            pokemonListView.getItems().addAll(pokeNames);
        });
    }


    @EventListener
    public void handlePokemonListEvent(PokemonService.PokemonListEvent event) {
        if (allPokemons == null) {
            allPokemons = new ArrayList<>();
        }
        Platform.runLater(() -> {
            allPokemonNames = event.pokemonNames();
            for(var name : event.pokemonNames()){
                pokemonService.fetchAndPrintPokemon(name);
            }
            filterPokemonList();  // Apply the current filters immediately
            statusLabel.setText("Pokémon list loaded.");
        });
    }

    @EventListener
    public void handlePokemonEvent(PokemonService.PokemonInfoEvent event)
    {
        if (allPokemons == null) {
            allPokemons = new ArrayList<>();
        }
        allPokemons.add(event.pokemon());
        Platform.runLater(this::PokemonsToNames);
    }

    public VBox getRoot() {
        return root;
    }
}
