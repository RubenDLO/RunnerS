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

    public Race() {}

    public Race(String date, double distance, long elapsedTime, double averageSpeed,
                String mapSnapshotBase64, String startTimeFormatted,
                double caloriesBurned, double temperature) {
        this.date = date;
        this.distance = distance;
        this.elapsedTime = elapsedTime;
        this.averageSpeed = averageSpeed;
        this.mapSnapshotBase64 = mapSnapshotBase64;
        this.startTimeFormatted = startTimeFormatted;
        this.caloriesBurned = caloriesBurned;
        this.temperature = temperature;
    }

    public String getDate() { return date; }
    public double getDistance() { return distance; }
    public long getElapsedTime() { return elapsedTime; }
    public double getAverageSpeed() { return averageSpeed; }
    public String getMapSnapshotBase64() { return mapSnapshotBase64; }
    public String getStartTimeFormatted() { return startTimeFormatted; }
    public double getCaloriesBurned() { return caloriesBurned; }
    public double getTemperature() { return temperature; }

    public String getFormattedTime() {
        int totalSeconds = (int) (elapsedTime / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getEncodedMap() {
        return mapSnapshotBase64;
    }
}
