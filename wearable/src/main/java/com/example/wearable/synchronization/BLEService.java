package com.example.wearable.synchronization;

import android.Manifest;
import android.app.Service;
import android.bluetooth.*;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import android.os.Binder;

import java.util.HashSet;
import java.util.Set;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BLEService extends Service {

    public static final String TAG = "BLEService";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    /* Collection of notification subscribers */
    private Set<BluetoothDevice> mRegisteredDevices = new HashSet<>();

    /**
     * Callback for GATT Server
     */
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            // Code executed when advertising starts successfully
            Log.i(TAG, "LE Advertising started successfully");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            // Code executed when advertising fails to start
            Log.e(TAG, "LE Advertising failed to start, error code: " + errorCode);
        }
    };

    @Nullable
    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Class used for the client Binder. The client binder allows the client to interact with the service.
     */
    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE})
    public void onCreate() {
        super.onCreate();

        // Initialize Bluetooth adapter.
        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (!checkBluetoothSupport()) {
            return;
        }

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, filter);

        // Check if Bluetooth is enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is currently disabled...enabling");
            mBluetoothAdapter.enable();
        } else {
            Log.d(TAG, "Bluetooth enabled...starting services");
            startServer();
            startAdvertising();
        }
    }

    /**
     * Start the GATT server
     */
    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    private void startServer() {
        // Initialize the GATT server
        mBluetoothGattServer = mBluetoothManager.openGattServer(this, new GattServerCallback(mBluetoothGattServer, mRegisteredDevices));
        if (mBluetoothGattServer == null) {
            Log.w(TAG, "Unable to create GATT server");
            return;
        }

        // Add a service for the GATT server
        mBluetoothGattServer.addService(TelemetryProfile.createTelemetryService());
    }

    /**
     * Stop the GATT server
     */
    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    private void stopServer() {
        if (mBluetoothGattServer == null)
            return;

        mBluetoothGattServer.close();
    }


    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE})
    public void onDestroy() {
        super.onDestroy();

        if (mBluetoothAdapter.isEnabled()) {
            stopServer();
            stopAdvertising();
        }

        unregisterReceiver(mBluetoothReceiver);
    }

    /**
     * Broadcast receiver to listen for Bluetooth adapter state changes.
     */

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE})
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    startAdvertising();
                    startServer();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopServer();
                    stopAdvertising();
                    break;
                default:
                    // Do nothing
            }

        }
    };

    /**
     * Start BLE Advertising
     */
    @RequiresPermission(value = "android.permission.BLUETOOTH_ADVERTISE")
    public void startAdvertising() {
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser != null) {
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                    .setConnectable(true)
                    .build();

            AdvertiseData data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(true)
                    .addServiceUuid(new ParcelUuid(TelemetryProfile.TELEMETRY_SERVICE))
                    .build();

            mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
        } else {
            Log.w(TAG, "Failed to create advertiser");
        }
    }

    /**
     * Stop BLE Advertising
     */
    @RequiresPermission(value = "android.permission.BLUETOOTH_ADVERTISE")
    public void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        }
    }

    /**
     * Send notification to subscribers
     */
    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    public void sendNotification() {
        if (mRegisteredDevices.isEmpty()) {
            Log.i(TAG, "No subscribers registered");
            return;
        }

        byte[] sensorData = TelemetryProfile.getSensorData();
        Log.i(TAG, "Sending notification to " + mRegisteredDevices.size() + " subscribers");

        if (mBluetoothGattServer != null) {
            for (BluetoothDevice device : mRegisteredDevices) {
                BluetoothGattCharacteristic sensorDataCharacteristic = mBluetoothGattServer
                        .getService(TelemetryProfile.TELEMETRY_SERVICE)
                        .getCharacteristic(TelemetryProfile.SENSOR_DATA);
                sensorDataCharacteristic.setValue(sensorData);
                mBluetoothGattServer.notifyCharacteristicChanged(device, sensorDataCharacteristic, false);
            }
        }
    }


    private boolean checkBluetoothSupport() {
        if (mBluetoothAdapter == null){
            Log.w(TAG, "Bluetooth is not supported");
            return false;
        }

        // Check if Bluetooth LE is supported on the device.
        if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            // Device does not support Bluetooth LE Advertising
            Log.w(TAG, "Device does not support Bluetooth LE Advertising");
            return false;
        }

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Log.w(TAG, "Bluetooth LE is not supported");
            return false;
        }

        return true;
    }
}
