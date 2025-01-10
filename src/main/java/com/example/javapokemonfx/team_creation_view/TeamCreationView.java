package com.example.javapokemonfx.team_creation_view;

import com.example.javapokemonfx.PokemonService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamCreationView {
    public static final String viewName = "Team Creation";
    public static final String fxmlName = "/teamcreationview.fxml";

    @FXML
    private ListView<String> pokemonListView;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox root;

    @FXML
    private TextArea teamDisplayArea;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private ApplicationContext applicationContext;

    private List<Pokemon> allPokemons = new ArrayList<>();
    private List<Pokemon> selectedPokemons;
    private List<List<Pokemon>> teams;

    public void initialize() {
        allPokemons = new ArrayList<>();
        selectedPokemons = new ArrayList<>();

        pokemonListView.setItems(FXCollections.observableArrayList());
        teams = new ArrayList<>();

        fetchPokemonList();
        pokemonListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        pokemonListView.setOnMouseClicked(event -> handleSelection());

        updatePokemonList();
        Platform.runLater(this::updatePokemonList);

    }

    private void fetchPokemonList() {
        statusLabel.setText("Fetching Pokémon list...");
        pokemonService.fetchPokemonList(20, 0);
    }


    private void handleSelection() {
        List<String> selectedItems = pokemonListView.getSelectionModel().getSelectedItems();
        selectedPokemons.clear();

        for (String selectedItem : selectedItems) {
            if (selectedPokemons.size() < 6) {
                Pokemon selectedPokemon = allPokemons.stream()
                        .filter(pokemon -> pokemon.getName().equals(selectedItem))
                        .findFirst().orElse(null);

                if (selectedPokemon != null) {
                    selectedPokemons.add(selectedPokemon);
                }
            }
        }

        statusLabel.setText("Selected " + selectedPokemons.size() + " Pokémon");
    }

    @FXML
    private void handleCreateTeam() {
        if (selectedPokemons.size() == 6) {
            teams.add(new ArrayList<>(selectedPokemons));
            statusLabel.setText("Team created with 6 Pokémon!");

            StringBuilder teamInfo = new StringBuilder("Your Team:\n");
            for (Pokemon pokemon : selectedPokemons) {
                teamInfo.append(pokemon.getName()).append("\n");
            }
            Platform.runLater(() -> {
                teamDisplayArea.setText(teamInfo.toString());
                teamDisplayArea.setVisible(true);  // Ensure TextArea is visible
            });

            Alert teamAlert = new Alert(Alert.AlertType.INFORMATION);
            teamAlert.setTitle("Your Team");
            teamAlert.setHeaderText("Here is your team of Pokémon:");
            teamAlert.setContentText(teamInfo.toString());
            teamAlert.showAndWait();
        } else {
            statusLabel.setText("Please select exactly 6 Pokémon.");
        }
    }

    private void updatePokemonList() {
        List<String> pokeNames = allPokemons.stream()
                .map(pokemon -> pokemon.getName())
                .collect(Collectors.toList());

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
        //System.out.println("Received PokemonListEvent with names: " + event.pokemonNames());
        Platform.runLater(() -> {
            for (var name : event.pokemonNames()) {
                pokemonService.fetchAndPrintPokemon(name);
            }
            statusLabel.setText("Pokémon list loaded.");
        });
    }

    @EventListener
    public void handlePokemonEvent(PokemonService.PokemonInfoEvent event) {
        if (allPokemons == null) {
            allPokemons = new ArrayList<>();
        }
        //System.out.println("Received PokemonInfoEvent for: " + event.pokemon().getName());
        allPokemons.add(event.pokemon());
        Platform.runLater(this::updatePokemonList);
    }

    public VBox getRoot() {
        return root;
    }
}
