package com.example.runners;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.runners.databinding.ActivityRaceTrackingBinding;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class RaceTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityRaceTrackingBinding binding;
    private GoogleMap mMap;
    private boolean tracking = false;
    private boolean paused = false;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LatLng lastPoint = null;
    private List<LatLng> routePoints = new ArrayList<>();

    private long startTime = 0;
    private long elapsedPaused = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean fineGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                boolean coarseGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                if (fineGranted || coarseGranted) {
                    enableUserLocation();
                } else {
                    Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRaceTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapTracking);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding.btnStartRace.setOnClickListener(v -> {
            if (!tracking) {
                if (hasLocationPermissions()) {
                    startRace();
                } else {
                    requestLocationPermissions();
                }
            }
        });

        binding.btnPauseRace.setOnClickListener(v -> {
            if (tracking) pauseRace();
        });

        binding.btnStopRace.setOnClickListener(v -> {
            if (tracking) stopRace();
        });
    }

    private void startRace() {
        tracking = true;
        paused = false;
        startTime = System.currentTimeMillis();
        startTimer();

        Toast.makeText(this, "¡Carrera iniciada!", Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(this, RaceTrackingService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        startLocationUpdates();
    }

    private void pauseRace() {
        paused = !paused;
        if (paused) {
            stopTimer();
            Toast.makeText(this, "Carrera pausada", Toast.LENGTH_SHORT).show();
        } else {
            startTime = System.currentTimeMillis() - elapsedPaused;
            startTimer();
            Toast.makeText(this, "Carrera reanudada", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRace() {
        tracking = false;
        stopTimer();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        stopService(new Intent(this, RaceTrackingService.class));

        long elapsedTime = elapsedPaused;
        double totalDistance = 0.0;
        for (int i = 1; i < routePoints.size(); i++) {
            Location start = new Location("start");
            start.setLatitude(routePoints.get(i - 1).latitude);
            start.setLongitude(routePoints.get(i - 1).longitude);

            Location end = new Location("end");
            end.setLatitude(routePoints.get(i).latitude);
            end.setLongitude(routePoints.get(i).longitude);

            totalDistance += start.distanceTo(end);
        }
        totalDistance /= 1000.0;

        double avgSpeed = totalDistance / (elapsedTime / 3600000.0);
        String startTimeFormatted = android.text.format.DateFormat.format("HH:mm", startTime).toString();
        double calories = totalDistance * 70;
        double temp = 20 + Math.random() * 10;

        double finalTotalDistance = totalDistance;
        mMap.snapshot(bitmap -> {
            String encodedImage = encodeBitmapToBase64(bitmap);

            Race race = new Race(
                    android.text.format.DateFormat.format("dd-MM-yyyy", System.currentTimeMillis()).toString(),
                    finalTotalDistance,
                    elapsedTime,
                    avgSpeed,
                    encodedImage,
                    startTimeFormatted,
                    calories,
                    temp
            );

            RaceHolder.getInstance().setLastRace(race);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                Map<String, Object> raceMap = new HashMap<>();
                raceMap.put("date", race.getDate());
                raceMap.put("distance", race.getDistance());
                raceMap.put("elapsedTime", race.getElapsedTime());
                raceMap.put("averageSpeed", race.getAverageSpeed());
                raceMap.put("mapSnapshotBase64", race.getMapSnapshotBase64());
                raceMap.put("startTimeFormatted", race.getStartTimeFormatted());
                raceMap.put("caloriesBurned", race.getCaloriesBurned());
                raceMap.put("temperature", race.getTemperature());
                raceMap.put("userId", uid);

                FirebaseFirestore.getInstance().collection("races")
                        .add(raceMap)
                        .addOnSuccessListener(documentReference -> {
                            startActivity(new Intent(this, RaceSummaryActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al guardar carrera en Firebase", Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String encodeBitmapToBase64(android.graphics.Bitmap bitmap) {
        java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedPaused = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedPaused / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                binding.tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void startLocationUpdates() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(3000);
        request.setFastestInterval(1500);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null || paused) return;
                for (Location location : locationResult.getLocations()) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    routePoints.add(point);
                    if (lastPoint != null) {
                        mMap.addPolyline(new PolylineOptions().add(lastPoint, point).width(5));
                    }
                    lastPoint = point;
                }
            }
        };

        if (hasLocationPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
        }
    }

    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION))) {

            new AlertDialog.Builder(this)
                    .setTitle("Permisos necesarios")
                    .setMessage("RunnerS necesita permisos de ubicación para registrar tu carrera. Ve a ajustes y actívalos.")
                    .setPositiveButton("Abrir ajustes", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

        } else {
            List<String> permissions = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            } else {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            locationPermissionLauncher.launch(permissions.toArray(new String[0]));
        }
    }

    private void enableUserLocation() {
        if (hasLocationPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (hasLocationPermissions()) {
            enableUserLocation();
        } else {
            requestLocationPermissions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
