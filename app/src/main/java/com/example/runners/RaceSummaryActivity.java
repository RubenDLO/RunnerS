package com.example.runners;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.runners.utils.FeedbackGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RaceSummaryActivity extends AppCompatActivity {

    private TextView tvDate, tvDistance, tvDuration, tvSpeed, tvCalories, tvTemp, tvFeedback;
    private ImageView ivMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_summary);

        tvDate = findViewById(R.id.tvDate);
        tvDistance = findViewById(R.id.tvDistance);
        tvDuration = findViewById(R.id.tvDuration);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvCalories = findViewById(R.id.tvCalories);
        tvTemp = findViewById(R.id.tvTemp);
        tvFeedback = findViewById(R.id.tvFeedback);
        ivMap = findViewById(R.id.ivMap);

        Race race = RaceHolder.getInstance().getLastRace();
        if (race == null) {
            tvFeedback.setText("No hay datos de la carrera.");
            return;
        }

        tvDate.setText("Fecha: " + race.getDate());
        tvDistance.setText(String.format("Distancia: %.2f km", race.getDistance()));
        tvDuration.setText("Duración: " + race.getFormattedTime());
        tvSpeed.setText(String.format("Velocidad media: %.2f km/h", race.getAverageSpeed()));
        tvCalories.setText(String.format("Calorías: %.0f kcal", race.getCaloriesBurned()));
        tvTemp.setText(String.format("Temperatura: %.1f ºC", race.getTemperature()));

        byte[] decodedBytes = Base64.decode(race.getEncodedMap(), Base64.DEFAULT);
        Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        ivMap.setImageBitmap(bitmap);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String level = doc.getString("level");
                        String feedback = FeedbackGenerator.generateFeedback(race, level);
                        String quote = "\n\nComo diría Kipchoge: 'Sin disciplina, no hay éxito'. Sigue adelante.";
                        tvFeedback.setText(feedback + quote);
                    } else {
                        tvFeedback.setText("No se encontró el nivel del usuario.");
                    }
                })
                .addOnFailureListener(e -> {
                    tvFeedback.setText("Error al cargar tu nivel: " + e.getMessage());
                });
    }
}
