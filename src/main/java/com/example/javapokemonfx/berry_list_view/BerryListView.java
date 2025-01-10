package com.example.javapokemonfx.berry_list_view;

import com.example.javapokemonfx.PokemonService;
import com.example.javapokemonfx.controllers.MainController;
import com.example.javapokemonfx.pokemon_details_view.PokemonDetailsView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import skaro.pokeapi.resource.berry.Berry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class BerryListView {
    public static final String viewName = "Berry List";
    public static final String fxmlName = "/berrylistview.fxml";
    public static final String afterFeedViewName = "Feeded Pokemon";

    @FXML
    private TextField filterTextField;

    @FXML
    private TextField minGrowthTimeFilterTextField;
    @FXML
    private TextField maxGrowthTimeFilterTextField;

    @FXML
    private TextField minHarvestFilterTextField;
    @FXML
    private TextField maxHarvestFilterTextField;

    @FXML
    private TextField minGiftFilterTextField;
    @FXML
    private TextField maxGiftFilterTextField;

    @FXML
    private ListView<String> berryListView;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox root;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private ApplicationContext applicationContext;

    private List<Berry> allBerries;

    private String pokemonToFeed = "";

    public void setPokemonToFeed(String PokemonName)
    {
        System.out.println("gowno4"+PokemonName);
        pokemonToFeed = PokemonName;
    }

    public void initialize() {
        allBerries = new ArrayList<>();
        fetchBerryList();
        filterTextField.textProperty().addListener((observable, oldText, newText) -> filterBerryList());
        minGrowthTimeFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterBerryList());
        maxGrowthTimeFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterBerryList());
        minHarvestFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterBerryList());
        maxHarvestFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterBerryList());
        minGiftFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterBerryList());
        maxGiftFilterTextField.textProperty().addListener((observable, oldText, newText) -> filterBerryList());
        berryListView.setOnMouseClicked(event -> handleSelection());
    }

    private void fetchBerryList() {
        statusLabel.setText("Fetching berry list...");
        pokemonService.fetchBerryNameList();
    }

    private void handleSelection() {
        String selectedBerry = berryListView.getSelectionModel().getSelectedItem();
        if (selectedBerry != null) {
            statusLabel.setText("Selected: " + selectedBerry);
            if(!Objects.equals(pokemonToFeed, ""))
            {
                var berryName = selectedBerry.split(" ")[0];

                switchViewToFeedPokemon(pokemonToFeed+" "+berryName); //add the berry name and react in pokemon details
            }
            else {
                switchViewToBerryDetails(selectedBerry);
            }
        }
    }

    private void switchViewToBerryDetails(String pokemonName) {
        MainController mainController = applicationContext.getBean(MainController.class);
       // mainController.switchView(PokemonDetailsView.viewName, pokemonName);
        Berry a = new Berry();
    }

    private void switchViewToFeedPokemon(String pokemonName) {
        MainController mainController = applicationContext.getBean(MainController.class);
        mainController.switchView(PokemonDetailsView.viewName, pokemonName);
    }

    @FXML
    private void handleClearFilters() {
        filterTextField.clear();
        minGrowthTimeFilterTextField.clear();
        maxGrowthTimeFilterTextField.clear();
        minHarvestFilterTextField.clear();
        maxHarvestFilterTextField.clear();
        minGiftFilterTextField.clear();
        maxGiftFilterTextField.clear();

        filterBerryList();
    }

    private void filterBerryList() {
        if (allBerries != null) {
            String nameFilter = filterTextField.getText().toLowerCase();

            int minGrowth = parseInteger(minGrowthTimeFilterTextField.getText(), 0);
            int maxGrowth = parseInteger(maxGrowthTimeFilterTextField.getText(), Integer.MAX_VALUE);

            int minHarvest = parseInteger(minHarvestFilterTextField.getText(), 0);
            int maxHarvest = parseInteger(maxHarvestFilterTextField.getText(), Integer.MAX_VALUE);

            int minGift = parseInteger(minGiftFilterTextField.getText(), 0);
            int maxGift = parseInteger(maxGiftFilterTextField.getText(), Integer.MAX_VALUE);

            List<String> filteredPokemon = allBerries.stream()
                    .filter(Objects::nonNull)
                    .filter(berry -> berry.getName().toLowerCase().contains(nameFilter)) // Filter by name

                    .filter(pokemon -> isWithinBounds(pokemon.getGrowthTime(), minGrowth, maxGrowth))

                    .filter(pokemon -> isWithinBounds(pokemon.getMaxHarvest(), minHarvest, maxHarvest))

                    .filter(pokemon -> isWithinBounds(pokemon.getNaturalGiftPower(), minGift, maxGift))

                    .map(pokemon -> String.format("%s (Growth: %d, Harvest: %d, Gift: %d)",
                            pokemon.getName(), pokemon.getGrowthTime(), pokemon.getMaxHarvest(), pokemon.getNaturalGiftPower()))
                    .collect(Collectors.toList());

            berryListView.getItems().setAll(filteredPokemon);
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

    private void BerriesToNames() {
        List<String> pokeNames;

        if (allBerries != null) {
            pokeNames = allBerries.stream()
                    .filter(Objects::nonNull)
                    .map(pokemon -> String.format("%s (Growth: %d, Harvest: %d, Gift: %d)",
                            pokemon.getName() != null ? pokemon.getName() : "Unknown",
                            pokemon.getGrowthTime()!= null ? pokemon.getGrowthTime() : 0,
                            pokemon.getMaxHarvest() != null ? pokemon.getMaxHarvest() : 0,
                            pokemon.getNaturalGiftPower() != null ? pokemon.getNaturalGiftPower() : 0))
                    .toList();
        } else {
            pokeNames = new ArrayList<>();
        }

        Platform.runLater(() -> {
            berryListView.getItems().clear();
            berryListView.getItems().addAll(pokeNames);
        });
    }


    @EventListener
    public void handleBerryListEvent(PokemonService.BerryListEvent event) {
        Platform.runLater(() -> {
            for(var name : event.berryName()){
                pokemonService.fetchBerry(name);
            }
            filterBerryList();
            statusLabel.setText("Berry list loaded.");
        });
    }

    @EventListener
    public void handleBerryEvent(PokemonService.BerryEvent event)
    {
        allBerries.add(event.berry());
        Platform.runLater(this::BerriesToNames);
    }

    public VBox getRoot() {
        return root;
    }
}
