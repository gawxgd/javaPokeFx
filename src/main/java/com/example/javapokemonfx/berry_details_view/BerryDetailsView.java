package com.example.javapokemonfx.berry_details_view;

import com.example.javapokemonfx.PokemonService;
import com.example.javapokemonfx.berry_list_view.BerryListView;
import com.example.javapokemonfx.controllers.MainController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BerryDetailsView {
    public static final String viewName = "Berry Details";
    public static final String fxmlName = "/berrydetailsview.fxml";
    public Label berryName;
    public Label berryGrowthTime;
    public Label berryHarvestTime;
    public Label berryGiftPower;

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private ApplicationContext applicationContext;

    private String BerryName;

    public BerryDetailsView(PokemonService pokemonService) {
    }

    public void setBerryName(String BerryName)
    {
        this.BerryName = BerryName;
        if(!Objects.equals(this.BerryName, "")) {
            var berryArgs = BerryName.split(" ");
            this.BerryName = berryArgs[0];
            pokemonService.fetchBerry(this.BerryName);
        }
    }

    @EventListener
    public void handleBerryDetailsEvent(PokemonService.BerryEvent event) {
        Platform.runLater(() -> {
            var berry = event.berry();
            berryName.setText(berry.getName());
            berryGrowthTime.setText("Growth Time: " + berry.getGrowthTime());
            berryGiftPower.setText("Gift Power: " + berry.getNaturalGiftPower());
            berryHarvestTime.setText("Harvest Time: " + berry.getMaxHarvest());
        });
    }

    public void onBackButtonClicked(ActionEvent actionEvent) {
        MainController mainController = applicationContext.getBean(MainController.class);
        mainController.switchView(BerryListView.viewName, "");
    }
}
