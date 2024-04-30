package com.example.chillinapp.synchronization;

import org.jetbrains.annotations.NotNull;

/**
 * Class that represents the data that is sent from the wearable to the mobile device.
 */
public class SensorData {

    private Long timestamp;
    private Double eda;
    private Double skinTemperature;
    private Double heartRate;


    /**
     * Constructor for the SensorData class.
     * @param eda The Electrodermal Activity value.
     * @param skinTemperature The skin temperature value.
     * @param heartRate The heart rate value.
     */
    public SensorData(Double eda, Double skinTemperature, Double heartRate) {
        this.eda = eda;
        this.skinTemperature = skinTemperature;
        this.heartRate = heartRate;
        this.timestamp = setCurrentTimestamp();
    }

    /**
     * Constructor for the SensorData class.
     * @param timestamp The timestamp of the data.
     * @param eda The Electrodermal Activity value.
     * @param skinTemperature The skin temperature value.
     * @param heartRate The heart rate value.
     */
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

    /**
     * Method that sets the timestamp of the data to the current time.
     * @return The current timestamp.
     */
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
