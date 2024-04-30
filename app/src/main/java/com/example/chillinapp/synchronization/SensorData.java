package com.example.chillinapp.synchronization;

import org.jetbrains.annotations.NotNull;

public class SensorData {

    private Long timestamp;
    private Double eda;
    private Double skinTemperature;
    private Double heartRate;


    public SensorData(Double eda, Double skinTemperature, Double heartRate) {
        this.eda = eda;
        this.skinTemperature = skinTemperature;
        this.heartRate = heartRate;
        this.timestamp = setCurrentTimestamp();
    }

    public SensorData(Long timestamp, Double eda, Double skinTemperature, Double heartRate) {
        this.eda = eda;
        this.skinTemperature = skinTemperature;
        this.heartRate = heartRate;
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Double getEda() {
        return eda;
    }

    public Double getSkinTemperature() {
        return skinTemperature;
    }

    public Double getHeartRate() {
        return heartRate;
    }

    public void setEda(Double eda) {
        this.eda = eda;
    }

    public void setSkinTemperature(Double skinTemperature) {
        this.skinTemperature = skinTemperature;
    }

    public void setHeartRate(Double heartRate) {
        this.heartRate = heartRate;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    private long setCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public @NotNull String toString() {
        return "SensorData{" +
                "timestamp=" + timestamp +
                ", eda=" + eda +
                ", skinTemperature=" + skinTemperature +
                ", heartRate=" + heartRate +
                '}';
    }
}
