package com.example.runners;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TrainingPlanActivity extends AppCompatActivity {

    private TextView tvTrainingTitle, tvTrainingPlan, tvReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_plan);

        tvTrainingTitle = findViewById(R.id.tvTrainingTitle);
        tvTrainingPlan = findViewById(R.id.tvTrainingPlan);
        tvReminders = findViewById(R.id.tvReminders);

        String level = getIntent().getStringExtra("runnerLevel");
        if (level == null) level = "Amateur";

        tvTrainingTitle.setText("Tu plan para nivel: " + level);
        tvTrainingPlan.setText(getTrainingPlanForLevel(level));
        tvReminders.setText(getFriendlyReminders());
    }

    private String getTrainingPlanForLevel(String level) {
        switch (level.toLowerCase()) {
            case "AmateuR":
                return "- 3 días suaves: 5 km a ritmo cómodo\n" +
                        "- 1 día de series cortas: 5x400m\n" +
                        "- 1 día de caminata activa o descanso\n" +
                        "- Consejo: ¡Disfruta cada paso!";

            case "IntermediO":
                return "- 2 días de rodaje largo: 8-12 km\n" +
                        "- 1 día de series medias: 6x800m\n" +
                        "- 1 día de fartlek (cambios de ritmo)\n" +
                        "- 1 día de recuperación activa";

            case "AvanzadO":
                return "- 1 tirada larga (15 km o más)\n" +
                        "- 2 días de calidad (intervalos + técnica)\n" +
                        "- 1 día de rodaje medio\n" +
                        "- 1 día de descanso total o muy suave";

            default:
                return "¡Nivel no reconocido! Volvamos a correr desde cero 🏃‍♂️";
        }
    }

    private String getFriendlyReminders() {
        return "💧 Hidratación: No esperes a tener sed. Agua antes, durante y después.\n\n" +
                "🍽️ Alimentación: Varía tu dieta. No vivas solo de pasta y plátanos 🍌.\n\n" +
                "🛌 Descanso: El descanso también entrena. 7-9h de sueño te harán volar 💤.\n\n" +
                "🔥 Recuerda: ¡No hay éxito sin constancia! Cada zancada cuenta.";
    }
}
