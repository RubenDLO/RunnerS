package com.example.runners.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.runners.R;
import com.example.runners.RaceTrackingActivity;
import com.example.runners.utils.FeedbackGenerator;
import com.example.runners.utils.WeatherAdvisor;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private TextView tvMotivation, tvTemperature, tvRecommendation;
    private LinearLayout bubbleMotivation, bubbleWeather;
    private String currentLevel = null;

    private ImageView bgHome;
    private final Handler imageHandler = new Handler();
    private final int[] backgroundImages = {
            R.drawable.pic_login8, R.drawable.pic_login9, R.drawable.pic_login10,
            R.drawable.pic_login11, R.drawable.pic_login12, R.drawable.pic_login13, R.drawable.pic_login14
    };
    private int currentImageIndex = 0;
    private Runnable imageSliderRunnable;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvMotivation = view.findViewById(R.id.tvMotivation);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        tvRecommendation = view.findViewById(R.id.tvRecommendation);
        bubbleMotivation = view.findViewById(R.id.bubbleMotivation);
        bubbleWeather = view.findViewById(R.id.bubbleWeather);
        bgHome = view.findViewById(R.id.bgHome);
        bgHome.setImageResource(backgroundImages[0]);

        MaterialButton btnStartRace = view.findViewById(R.id.btnStartRace);
        FloatingActionButton btnInfoMotivation = view.findViewById(R.id.btnInfoMotivation);
        FloatingActionButton btnInfoWeather = view.findViewById(R.id.btnInfoWeather);

        btnStartRace.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RaceTrackingActivity.class);
            startActivity(intent);
        });

        double temperatura = WeatherAdvisor.simularTemperatura();
        tvTemperature.setText(String.format("Temperatura actual: %.1f ºC", temperatura));
        String hora = WeatherAdvisor.obtenerHoraIdeal(temperatura);
        tvRecommendation.setText("Hora recomendada: " + hora);

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        currentLevel = doc.getString("level");
                        String mensaje = FeedbackGenerator.getWelcomeMessage(currentLevel);
                        tvMotivation.setText(mensaje);
                    })
                    .addOnFailureListener(e -> {
                        tvMotivation.setText("\u00a1Hoy es un buen d\u00eda para correr!");
                    });
        } else {
            tvMotivation.setText("\u00a1Hoy es un buen d\u00eda para correr!");
        }

        btnInfoMotivation.setOnClickListener(v -> {
            bubbleMotivation.setVisibility(
                    bubbleMotivation.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
        });

        btnInfoWeather.setOnClickListener(v -> {
            bubbleWeather.setVisibility(
                    bubbleWeather.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 🟠 Imagen dinámica de fondo (no se modifica)
        imageSliderRunnable = new Runnable() {
            @Override
            public void run() {
                currentImageIndex = (currentImageIndex + 1) % backgroundImages.length;

                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(500);
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(500);

                bgHome.startAnimation(fadeOut);
                bgHome.setImageResource(backgroundImages[currentImageIndex]);
                bgHome.startAnimation(fadeIn);

                imageHandler.postDelayed(this, 10000);
            }
        };
        imageHandler.postDelayed(imageSliderRunnable, 10000);

        // 🟠 Actualizar mensaje motivacional según nivel actualizado
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        currentLevel = doc.getString("level");
                        String mensaje = FeedbackGenerator.getWelcomeMessage(currentLevel);
                        tvMotivation.setText(mensaje);
                    })
                    .addOnFailureListener(e -> {
                        tvMotivation.setText("¡Hoy es un buen día para correr!");
                    });
        } else {
            tvMotivation.setText("¡Hoy es un buen día para correr!");
        }
    }
}
