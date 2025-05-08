package com.example.runners.utils;

import com.example.runners.Race;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FeedbackGenerator {

    private static final Random random = new Random();

    public static String getWelcomeMessage(String level) {
        List<String> frases;

        if (level == null) {
            level = "amateur";
        }

        switch (level.toLowerCase()) {
            case "intermedio":
                frases = Arrays.asList(
                        "Tu esfuerzo empieza a tener forma… ¡y vaya forma!",
                        "Ya no eres quien empezó, ahora corres con cabeza, piernas y corazón.",
                        "Lo que antes parecía imposible, ahora es tu calentamiento.",
                        "Estás construyendo algo más que resistencia: estás construyendo constancia.",
                        "Cada entrenamiento suma. Cada día cuenta. Cada carrera te acerca."
                );
                break;
            case "avanzado":
                frases = Arrays.asList(
                        "No corres para mejorar... corres para dominar.",
                        "Tu marca personal no es el techo, es el suelo del próximo intento.",
                        "Donde otros se rinden, tú recién estás empezando.",
                        "Eres la referencia, el espejo y la meta.",
                        "No busques motivación. Sé tú la motivación."
                );
                break;
            default: // Amateur o null
                frases = Arrays.asList(
                        "Cada zancada que das es un “yo puedo” gritándole al mundo.",
                        "No importa si vas lento, lo importante es que no te detienes.",
                        "Hoy es el primer día de tu mejor versión.",
                        "Corre por ti, por lo que fuiste, y por lo que estás a punto de ser.",
                        "Estás empezando… y eso ya te hace valiente."
                );
                break;
        }

        return frases.get(random.nextInt(frases.size()));
    }
    // FeedbackGenerator.java
    public static String generateFeedback(Race race, String level) {
        double distance = race.getDistance(); // en km
        long durationMillis = race.getElapsedTime(); // en ms
        double pace = (durationMillis / 1000.0 / 60.0) / distance;

        String base;
        if (level == null) level = "amateur";

        switch (level.toLowerCase()) {
            case "intermedio":
                base = pace < 6 ? "¡Muy buen ritmo! Estás consolidando tu progreso." :
                        pace < 7 ? "Buen trabajo. Todavía hay margen para afinar ese ritmo." :
                                "Puedes mejorar con entrenamientos más constantes.";
                break;
            case "avanzado":
                base = pace < 4.5 ? "¡Ritmazo! Estás a nivel de élite." :
                        pace < 5.5 ? "Buen rendimiento. Sigue entrenando para bajar esos segundos." :
                                "Recuerda que hasta los mejores tienen días más lentos.";
                break;
            default: // amateur
                base = pace < 7 ? "¡Gran comienzo! Vas cogiendo forma rápido." :
                        pace < 8.5 ? "Lo importante es no detenerse. Sigue así." :
                                "Todos empezamos caminando. Hoy, tú ya estás corriendo.";
                break;
        }

        return base;
    }

}
