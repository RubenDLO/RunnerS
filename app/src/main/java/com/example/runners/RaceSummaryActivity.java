package com.example.runners;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.runners.utils.FeedbackGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RaceSummaryActivity extends AppCompatActivity {

    private TextView tvDate, tvDistance, tvDuration, tvSpeed, tvCalories, tvTemp, tvFeedback;
    private ImageView ivMap;
    private Button btnGoToHistory;

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
        btnGoToHistory = findViewById(R.id.btnGoToHistory);

        Race race = RaceHolder.getInstance().getLastRace();
        if (race == null) {
            tvFeedback.setText("No hay datos de la carrera.");
            return;
        }

        tvDate.setText("Fecha: " + race.getDate());
        tvDistance.setText(String.format("Distancia: %.2f km", race.getDistance()));
        tvDuration.setText("Duración: " + race.getFormattedTime());

        if (race.getDistance() == 0) {
            tvFeedback.setText("La carrera no registró distancia. Intenta de nuevo.");
            return;
        }

        double pace = race.getElapsedTime() / 1000.0 / 60.0 / race.getDistance();
        int minutes = (int) pace;
        int seconds = (int) ((pace - minutes) * 60);
        tvSpeed.setText(String.format("Ritmo medio: %d:%02d min/km", minutes, seconds));

        tvCalories.setText(String.format("Calorías: %.0f kcal", race.getCaloriesBurned()));
        tvTemp.setText(String.format("Temperatura: %.1f ºC", race.getTemperature()));

        String encodedMap = race.getMapSnapshotBase64();
        if (encodedMap != null && !encodedMap.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(encodedMap, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                ivMap.setImageBitmap(bitmap);
            } catch (Exception e) {
                tvFeedback.setText("Error al cargar el mapa de la ruta.");
            }
        } else {
            tvFeedback.setText("No se generó imagen del mapa.");
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            tvFeedback.append("\nUsuario no identificado.");
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(userId)
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
                    if (!isInternetAvailable()) {
                        tvFeedback.setText("No tienes conexión a Internet.\n\nCorre con corazón.");
                    } else {
                        tvFeedback.setText("Error al cargar tu nivel.");
                    }
                });

        btnGoToHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainNavigationActivity.class);
            intent.putExtra("goToHistory", true);
            startActivity(intent);
            finish();
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
