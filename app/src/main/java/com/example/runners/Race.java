package com.example.runners;

public class Race {
    private String date;
    private double distance;
    private long elapsedTime;
    private double averageSpeed;
    private String mapSnapshotBase64;
    private String startTimeFormatted;
    private double caloriesBurned;
    private double temperature;

    // 🔧 Constructor vacío obligatorio para Firebase
    public Race() {
    }

    // 🔧 Constructor completo
    public Race(String date, double distance, long elapsedTime, double averageSpeed, String mapSnapshotBase64,
                String startTimeFormatted, double caloriesBurned, double temperature) {
        this.date = date;
        this.distance = distance;
        this.elapsedTime = elapsedTime;
        this.averageSpeed = averageSpeed;
        this.mapSnapshotBase64 = mapSnapshotBase64;
        this.startTimeFormatted = startTimeFormatted;
        this.caloriesBurned = caloriesBurned;
        this.temperature = temperature;
    }

    // 🔧 Getters y Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public long getElapsedTime() { return elapsedTime; }
    public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }

    public double getAverageSpeed() { return averageSpeed; }
    public void setAverageSpeed(double averageSpeed) { this.averageSpeed = averageSpeed; }

    public String getMapSnapshotBase64() { return mapSnapshotBase64; }
    public void setMapSnapshotBase64(String mapSnapshotBase64) { this.mapSnapshotBase64 = mapSnapshotBase64; }

    public String getStartTimeFormatted() { return startTimeFormatted; }
    public void setStartTimeFormatted(String startTimeFormatted) { this.startTimeFormatted = startTimeFormatted; }

    public double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(double caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    // 🔧 Métodos adicionales útiles
    public String getEncodedMap() {
        return mapSnapshotBase64;
    }

    public String getFormattedTime() {
        int totalSeconds = (int) (elapsedTime / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
