package com.example.runners;

/**
 * RaceHolder es un singleton que nos permite guardar temporalmente
 * la última carrera registrada, para acceder a ella desde otra actividad.
 */
public class RaceHolder {

    private static final RaceHolder instance = new RaceHolder();

    private Race lastRace;

    private RaceHolder() {
        // Constructor privado para patrón Singleton
    }

    public static RaceHolder getInstance() {
        return instance;
    }

    public void setLastRace(Race race) {
        this.lastRace = race;
    }

    public Race getLastRace() {
        return lastRace;
    }

    public void clear() {
        lastRace = null;
    }
}
