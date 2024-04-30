package com.example.chillinapp.synchronization;

import org.jetbrains.annotations.NotNull;

/**
 * Class that represents the data that is sent from the wearable to the mobile device.
 */
public class SensorData {

    private Long timestamp;
    private Double eda;
    private Double skinTemperature;


    /**
     * Constructor for the SensorData class.
     * @param eda The Electrodermal Activity value.
     * @param skinTemperature The skin temperature value.
     */
    public SensorData(Double eda, Double skinTemperature) {
        this.eda = eda;
        this.skinTemperature = skinTemperature;
        this.timestamp = setCurrentTimestamp();
    }

    /**
     * Constructor for the SensorData class.
     * @param timestamp The timestamp of the data.
     * @param eda The Electrodermal Activity value.
     * @param skinTemperature The skin temperature value.
     */
    public SensorData(Long timestamp, Double eda, Double skinTemperature) {
        this.eda = eda;
        this.skinTemperature = skinTemperature;
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

    public void setEda(Double eda) {
        this.eda = eda;
    }

    public void setSkinTemperature(Double skinTemperature) {
        this.skinTemperature = skinTemperature;
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
                '}';
    }
}
