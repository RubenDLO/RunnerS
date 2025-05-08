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
        if (level == null) level = "amateur";
        String displayedLevel = capitalize(level);

        tvTrainingTitle.setText("Tu plan para nivel: " + displayedLevel);
        tvTrainingPlan.setText(getTrainingPlanForLevel(level));
        tvReminders.setText(getFriendlyReminders());
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private String getTrainingPlanForLevel(String level) {
        switch (level.toLowerCase()) {
            case "amateur":
                return "🏃‍♂️ Nivel Amateur\n" +
                        "- Lunes: 5 km ritmo cómodo + estiramientos\n" +
                        "- Miércoles: 30 min caminata rápida o bici suave\n" +
                        "- Viernes: 5 km + 4 progresivos\n" +
                        "- Domingo: 6 km tranquilo\n\n" +
                        "🎯 Objetivo: generar hábito y mejorar fondo base.";

            case "intermedio":
                return "🏃‍♂️ Nivel Intermedio\n" +
                        "- Lunes: 6-8 km a ritmo medio\n" +
                        "- Miércoles: 4x1000m a ritmo intenso (descanso 2min)\n" +
                        "- Viernes: 7 km + 6 progresivos\n" +
                        "- Domingo: 10-12 km rodaje largo\n\n" +
                        "🎯 Objetivo: aumentar resistencia y velocidad controlada.";

            case "avanzado":
                return "🏃‍♂️ Nivel Avanzado\n" +
                        "- Lunes: 10 km suaves\n" +
                        "- Martes: 5x1200m + técnica de carrera\n" +
                        "- Jueves: Fartlek 45 min (ej. 4x3min rápido + 2min suave)\n" +
                        "- Sábado: 16-18 km fondo largo\n\n" +
                        "🎯 Objetivo: consolidar rendimiento y preparar competiciones.";

            default:
                return "Nivel no reconocido. Volvamos a empezar desde la base. 🏃‍♂️";
        }
    }

    private String getFriendlyReminders() {
        return "💧 Hidratación\n" +
                "- No esperes a tener sed. Bebe agua antes, durante y después.\n\n" +
                "🍽️ Alimentación\n" +
                "- Come variado. Prioriza alimentos naturales. Post-carrera: proteínas + hidratos.\n\n" +
                "🛌 Descanso\n" +
                "- Dormir entre 7-9h mejora la recuperación. Un día de descanso activo a la semana.\n\n" +
                "🔥 Mentalidad\n" +
                "- Correr es constancia. Celebra pequeños logros. El cuerpo escucha a la mente.\n\n" +
                "⚠️ Extra\n" +
                "- No te compares, progresa a tu ritmo. Y si duele, descansa. Tu cuerpo es sabio.";
    }
}
