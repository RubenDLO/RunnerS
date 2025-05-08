package com.example.runners.utils;

public class WeatherAdvisor {

    public static double simularTemperatura() {
        // Temperatura aleatoria entre 12 y 28 grados
        return 12 + Math.random() * 16;
    }

    public static String obtenerHoraIdeal(double temperatura) {
        if (temperatura < 15) {
            return "13:00 (cuando sube un poco)";
        } else if (temperatura < 22) {
            return "9:00 o 19:00 (perfecto)";
        } else {
            return "21:00 (evita el calor)";
        }
    }
}
