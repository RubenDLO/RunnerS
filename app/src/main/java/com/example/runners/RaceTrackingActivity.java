package com.example.runners;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.runners.databinding.ActivityRaceTrackingBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityRaceTrackingBinding binding;
    private GoogleMap mMap;
    private boolean tracking = false;
    private boolean isPaused = false;
    private boolean hasCenteredMap = false;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LatLng lastPoint = null;
    private final List<LatLng> routePoints = new ArrayList<>();

    private long elapsedPaused = 0;
    private long startTime = 0;
    private final Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean fineGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
                boolean coarseGranted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                boolean fgServiceGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                        || Boolean.TRUE.equals(result.get(Manifest.permission.FOREGROUND_SERVICE_LOCATION));

                if ((fineGranted || coarseGranted) && fgServiceGranted) {
                    enableUserLocation();
                } else {
                    Toast.makeText(this, "Permisos de ubicación y servicio no concedidos", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRaceTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTracking);
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
            if (tracking) {
                isPaused = !isPaused;
                if (isPaused) {
                    pauseRace();
                    binding.btnPauseRace.setImageResource(R.drawable.ic_play);
                } else {
                    resumeRace();
                    binding.btnPauseRace.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        binding.btnStopRace.setOnClickListener(v -> {
            if (tracking) stopRace();
        });
    }

    private void startRace() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No tienes permiso para iniciar el servicio de carrera", Toast.LENGTH_SHORT).show();
            return;
        }

        tracking = true;
        isPaused = false;
        startTime = System.currentTimeMillis();
        startTimer();

        binding.btnStartRace.setVisibility(View.GONE);
        binding.btnPauseRace.setVisibility(View.VISIBLE);
        binding.btnStopRace.setVisibility(View.VISIBLE);
        binding.btnPauseRace.setImageResource(R.drawable.ic_pause);

        Intent serviceIntent = new Intent(this, RaceTrackingService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        startLocationUpdates();
    }

    private void pauseRace() {
        stopTimer();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void resumeRace() {
        startTime = System.currentTimeMillis() - elapsedPaused;
        startTimer();
        startLocationUpdates();
    }

    private void stopRace() {
        tracking = false;
        stopTimer();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        stopService(new Intent(this, RaceTrackingService.class));

        binding.btnStartRace.setVisibility(View.VISIBLE);
        binding.btnPauseRace.setVisibility(View.GONE);
        binding.btnStopRace.setVisibility(View.GONE);

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

        double avgSpeed = (elapsedTime > 0) ? totalDistance / (elapsedTime / 3600000.0) : 0;
        if (Double.isNaN(avgSpeed) || Double.isInfinite(avgSpeed)) {
            Toast.makeText(this, "Error: velocidad media inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        String startTimeFormatted = android.text.format.DateFormat.format("HH:mm", startTime).toString();
        double calories = totalDistance * 70;
        double temp = 20 + Math.random() * 10;

        if (mMap == null) {
            Toast.makeText(this, "Mapa no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!routePoints.isEmpty()) {
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng point : routePoints) {
                boundsBuilder.include(point);
            }
            LatLngBounds bounds = boundsBuilder.build();

            mMap.setOnMapLoadedCallback(() -> {
                int padding = 180;
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            });
        }

        double finalTotalDistance = totalDistance;
        mMap.setOnMapLoadedCallback(() -> mMap.snapshot(bitmap -> {
            if (bitmap == null) {
                Toast.makeText(this, "Error al capturar el mapa", Toast.LENGTH_SHORT).show();
                goToSummary();
                return;
            }

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

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && isInternetAvailable()) {
                try {
                    String uid = currentUser.getUid();

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
                            .addOnSuccessListener(documentReference -> goToSummary())
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar carrera", Toast.LENGTH_SHORT).show();
                                goToSummary();
                            });
                } catch (Exception e) {
                    Toast.makeText(this, "Error inesperado", Toast.LENGTH_SHORT).show();
                    goToSummary();
                }
            } else {
                Toast.makeText(this, "No hay conexión o usuario activo", Toast.LENGTH_SHORT).show();
                goToSummary();
            }
        }));
    }

    private void goToSummary() {
        Intent resumenIntent = new Intent(this, RaceSummaryActivity.class);
        startActivity(resumenIntent);
        finish();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private String encodeBitmapToBase64(android.graphics.Bitmap bitmap) {
        java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
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
                if (locationResult == null || isPaused) return;
                for (Location location : locationResult.getLocations()) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    routePoints.add(point);

                    if (!hasCenteredMap) {
                        hasCenteredMap = true;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 17));
                    }

                    if (lastPoint != null) {
                        mMap.addPolyline(new PolylineOptions()
                                .add(lastPoint, point)
                                .width(12f)
                                .color(ContextCompat.getColor(RaceTrackingActivity.this, R.color.accent_orange))
                                .geodesic(true));
                    }

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                    lastPoint = point;
                }
            }
        };

        if (hasLocationPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
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
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng startLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 17f));
                    hasCenteredMap = true;
                }
            });
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
