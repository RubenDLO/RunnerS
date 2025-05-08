package com.example.runners;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RaceTrackingService extends Service {

    public static final String CHANNEL_ID = "RaceTrackingChannel";
    public static List<LatLng> trackedRoutePoints = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Handler notificationHandler;
    private Runnable notificationRunnable;
    private long startTime;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                checkSelfPermission(android.Manifest.permission.FOREGROUND_SERVICE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Log.e("RaceTrackingService", "Permiso FOREGROUND_SERVICE_LOCATION no concedido. Deteniendo servicio.");
            stopSelf();
            return;
        }

        createNotificationChannel();
        startTime = System.currentTimeMillis();

        Notification notification = buildNotification("00:00", 0.0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        } else {
            startForeground(1, notification);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();
        startNotificationUpdates();
    }


    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    trackedRoutePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
                    Log.d("RaceTrackingService", "Punto GPS añadido: " + location.getLatitude() + ", " + location.getLongitude());
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("RaceTrackingService", "Permisos de localización no concedidos. Deteniendo servicio.");
            stopSelf();
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void startNotificationUpdates() {
        notificationHandler = new Handler();
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                int minutes = (int) (elapsed / 60000);
                int seconds = (int) ((elapsed % 60000) / 1000);
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

                double distanceKm = 0.0;
                for (int i = 1; i < trackedRoutePoints.size(); i++) {
                    Location start = new Location("start");
                    start.setLatitude(trackedRoutePoints.get(i - 1).latitude);
                    start.setLongitude(trackedRoutePoints.get(i - 1).longitude);

                    Location end = new Location("end");
                    end.setLatitude(trackedRoutePoints.get(i).latitude);
                    end.setLongitude(trackedRoutePoints.get(i).longitude);

                    distanceKm += start.distanceTo(end);
                }
                distanceKm = distanceKm / 1000.0;

                Notification updatedNotification = buildNotification(formattedTime, distanceKm);
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.notify(1, updatedNotification);
                }

                notificationHandler.postDelayed(this, 1000);
            }
        };
        notificationHandler.post(notificationRunnable);
    }

    private Notification buildNotification(String time, double distanceKm) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("RunnerS")
                .setContentText("Duración: " + time + "  Distancia: " + String.format(Locale.getDefault(), "%.2f km", distanceKm))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (notificationHandler != null && notificationRunnable != null) {
            notificationHandler.removeCallbacks(notificationRunnable);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Race Tracking Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
