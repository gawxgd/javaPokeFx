package com.example.javapokemonfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class PokeApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML layout for the main view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainlayout.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Scene scene = new Scene(loader.load());

        // Set a larger window size
        stage.setTitle("Pokemon Info");
        stage.setScene(scene);
        stage.setWidth(600);  // Set the window width
        stage.setHeight(400); // Set the window height
        stage.show();

        // Publish event to notify stage is ready
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(JavaPokemonFxApplication.class).run();
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
}
