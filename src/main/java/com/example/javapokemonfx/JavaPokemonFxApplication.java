package com.example.javapokemonfx;

import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaPokemonFxApplication {

    public static void main(String[] args) {
        Application.launch(PokeApplication.class, args);
    }

}
