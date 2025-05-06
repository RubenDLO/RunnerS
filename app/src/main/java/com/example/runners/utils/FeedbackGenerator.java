package com.example.runners.utils;

import com.example.runners.Race;

public class FeedbackGenerator {

    public static String generateFeedback(Race race, String level) {
        double avgSpeedKmH = race.getAverageSpeed(); // en km/h
        double pace = (avgSpeedKmH > 0) ? (60 / avgSpeedKmH) : 0; // min/km

        switch (level) {
            case "Amateur":
                return feedbackForAmateur(pace);
            case "Intermedio":
                return feedbackForIntermediate(pace);
            case "Avanzado":
                return feedbackForAdvanced(pace);
            default:
                return "Nivel no reconocido. No se puede generar feedback.";
        }
    }

    private static String feedbackForAmateur(double pace) {
        if (pace > 8) {
            return "Estás comenzando con buen paso. Sigue alternando caminatas y carreras cortas. ¡Lo estás haciendo bien!";
        } else if (pace > 6) {
            return "¡Gran progreso! Ya puedes mantener ritmos constantes. Intenta aumentar ligeramente la duración.";
        } else {
            return "¡Impresionante! Vas camino de dejar de ser amateur. Puedes probar entrenamientos más avanzados.";
        }
    }

    private static String feedbackForIntermediate(double pace) {
        if (pace > 6) {
            return "Buen trabajo, pero intenta añadir más variedad: fartlek, series cortas o tiradas largas.";
        } else if (pace > 5) {
            return "¡Estás en buena forma! Puedes empezar a controlar ritmos de carrera y umbrales.";
        } else {
            return "Excelente rendimiento. Estás listo para retos de 10K o media maratón.";
        }
    }

    private static String feedbackForAdvanced(double pace) {
        if (pace > 5) {
            return "Nivel alto, pero puedes afinar más la técnica y la nutrición pre/post entreno.";
        } else if (pace > 4) {
            return "Muy buen ritmo. Controla tu carga de entrenamiento y busca rendimiento estable.";
        } else {
            return "Elite runner detected. ¡Sigue con esa disciplina! Considera entrenamientos polarizados.";
        }
    }
}
