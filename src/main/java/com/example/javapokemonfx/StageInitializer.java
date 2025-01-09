package com.example.javapokemonfx;

import com.example.javapokemonfx.PokeApplication;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<PokeApplication.StageReadyEvent> {
    @Override
    public void onApplicationEvent(PokeApplication.StageReadyEvent event) {
        Stage stage = event.getStage();
    }
}