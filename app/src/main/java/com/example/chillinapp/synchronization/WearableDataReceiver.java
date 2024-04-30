package com.example.chillinapp.synchronization;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.ChannelClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WearableDataReceiver extends Service {
    private static final String TAG = "WearableDataReceiver";

    public WearableDataReceiver() {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Check the action received
        if (intent.getAction() != null && intent.getAction().equals("RECEIVE")) {
            Log.d(TAG, "Receiving data");
            receiveData();
        } else if (intent.getAction() != null && intent.getAction().equals("STOP_SERVICE")) {
            Log.d(TAG, "Stopping service");
            stopSelf();
        } else if (intent.getAction() != null && intent.getAction().equals("START_SERVICE")) {
            Log.d(TAG, "Starting service");
        } else
            Log.w(TAG, "No action found");

        // Return START_STICKY to restart the service if it gets killed
        return START_STICKY;
    }

    private void receiveData() {
        // Register a channel callback to receive data from the wearable device
        Wearable.getChannelClient(getApplicationContext()).registerChannelCallback(new ChannelClient.ChannelCallback() {
            @Override
            public void onChannelOpened(@NonNull ChannelClient.Channel channel) {
                super.onChannelOpened(channel);
                Log.d(TAG, "onChannelOpened");
                Task<InputStream> inputStreamTask = Wearable.getChannelClient(getApplicationContext()).getInputStream(channel);
                inputStreamTask.addOnSuccessListener(inputStream -> {
                        // Read the data received from the wearable device
                        Runnable toRun = () -> {
                            try {
                                StringBuilder text = new StringBuilder();
                                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                                int read;
                                byte[] data = new byte[1024];
                                while ((read = inputStream.read(data, 0, data.length)) != -1) {
                                    Log.d(TAG, "Data length " + read);
                                    buffer.write(data, 0, read);

                                    buffer.flush();
                                    byte[] byteArray = buffer.toByteArray();

                                    text.append(new String(byteArray, StandardCharsets.UTF_8));
                                }
                                Log.d(TAG, "Reading: " + text);

                                /* Parse the data received from the wearable device */
                                // List<SensorData> bulkData = parseBulkData(buffer.toByteArray());
                                // Log.d(TAG, "Received data: " + sensorData.toString());
                                /* ------------------------------- */
                                inputStream.close();
                            } catch (IOException e) {
                               Log.e(TAG, "Error in receiving data: " + e);
                            } finally {
                                Wearable.getChannelClient(getApplicationContext()).close(channel);
                            }
                        };
                        Thread run = new Thread(toRun);
                        run.start();
                });
            }
        });
    }

    /**
     * Parse the data received from the wearable device. The data is composed by a list of 30 SensorData objects.
     * @param data The data received from the wearable device.
     * @return A list of SensorData objects.
     */
    private List<SensorData> parseBulkData(byte[] data){
        List<SensorData> sensorDataList = new ArrayList<>();
        int i = 0;
        while (i < data.length) {
            byte[] singleData = new byte[24];
            System.arraycopy(data, i, singleData, 0, 24);
            SensorData sensorData = parseSingleData(singleData);
            sensorDataList.add(sensorData);
            i += 24;
        }
        return sensorDataList;
    }

    private SensorData parseSingleData(byte[] data) {
        // Array of bytes composed by:
        // 8 bytes for timestamp
        // 8 bytes for EDA
        // 8 bytes for skin temperature

        // Timestamp
        byte[] timestampBytes = new byte[8];
        System.arraycopy(data, 0, timestampBytes, 0, 8);
        long timestamp = bytesToLong(timestampBytes);

        // EDA
        byte[] edaBytes = new byte[8];
        System.arraycopy(data, 8, edaBytes, 0, 8);
        double eda = bytesToDouble(edaBytes);

        // Skin temperature
        byte[] skinTemperatureBytes = new byte[8];
        System.arraycopy(data, 16, skinTemperatureBytes, 0, 8);
        double skinTemperature = bytesToDouble(skinTemperatureBytes);

        return new SensorData(timestamp, eda, skinTemperature);
    }

    /**
     * Converts a byte array to a long
     * @param timestampBytes
     * @return
     */
    private long bytesToLong(byte[] timestampBytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(timestampBytes);
        buffer.flip();
        return buffer.getLong();
    }

    /**
     * Converts a byte array to a double
     * @param sensorBytes
     * @return
     */
    private double bytesToDouble(byte[] sensorBytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.put(sensorBytes);
        buffer.flip();
        return buffer.getDouble();
    }
}
