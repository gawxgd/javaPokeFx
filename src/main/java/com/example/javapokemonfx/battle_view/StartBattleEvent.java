package com.example.javapokemonfx.battle_view;

import org.springframework.context.ApplicationEvent;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.util.List;

public class StartBattleEvent extends ApplicationEvent {
    private final List<Pokemon> selectedTeam;

    public StartBattleEvent(Object source, List<Pokemon> selectedTeam) {
        super(source);
        this.selectedTeam = selectedTeam;
    }

    public List<Pokemon> getSelectedTeam() {
        return selectedTeam;
    }
}
