package com.example.wearable.synchronization;

import android.bluetooth.*;
import android.util.Log;
import androidx.annotation.RequiresPermission;

import java.util.Arrays;
import java.util.Set;

import static com.example.wearable.synchronization.BLEService.TAG;

/**
 * This is a callback class that will handle requests from the client device. You’ll need to override methods like onCharacteristicReadRequest(), onCharacteristicWriteRequest(), etc.
 */
public class GattServerCallback extends BluetoothGattServerCallback {
    private BluetoothGattServer mGattServer;
    private Set<BluetoothDevice> mRegisteredDevices;


    /**
     * Constructor for GattServerCallback
     * @param gattServer
     * @param registeredDevices
     */
    public GattServerCallback(BluetoothGattServer gattServer, Set<BluetoothDevice> registeredDevices) {
        mGattServer = gattServer;
        mRegisteredDevices = registeredDevices;
    }

    /**
     * This method is called when a remote device connects or disconnects from the GATT server.
     * @param device Remote device that has been connected or disconnected.
     * @param status Status of the connect or disconnect operation.
     * @param newState Returns the new connection state. Can be one of {@link
     * BluetoothProfile#STATE_DISCONNECTED} or {@link BluetoothProfile#STATE_CONNECTED}
     */
    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        super.onConnectionStateChange(device, status, newState);

        if (newState == BluetoothProfile.STATE_CONNECTED){
            Log.i(TAG, "Bliuetooth Device CONNETED: " + device);
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED){
            Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device);
            mRegisteredDevices.remove(device);
        }

    }

    /**
     * This method is called when a service has been added to the GATT server.
     * @param status Returns {@link BluetoothGatt#GATT_SUCCESS} if the service was added
     * successfully.
     * @param service The service that has been added
     */
    @Override
    public void onServiceAdded(int status, BluetoothGattService service) {
        super.onServiceAdded(status, service);
        // Handle service added status

    }

    /**
     * This method is called when a remote device requests to read a given characteristic.
     * @param device The remote device that is requesting the read operation
     * @param requestId The ID of the request
     * @param offset Offset into the value of the characteristic
     * @param characteristic The characteristic to be read
     */
    @Override
    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        // Handle characteristic read requests
        if (TelemetryProfile.SENSOR_DATA.equals(characteristic.getUuid())){
            Log.i(TAG, "Read Sensor Data");
            mGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    0,
                    TelemetryProfile.getSensorData());
        } else {
            Log.w(TAG, "Invalid Characteristic Read: " + characteristic.getUuid());
            mGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0,
                    null);
        }
    }


   /* @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        // Handle characteristic write requests
        if (responseNeeded) {
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }
    }
*/

    /**
     * This method is called when a remote device requests to read a descriptor.
     * @param device The remote device that has requested the read operation
     * @param requestId The Id of the request
     * @param offset Offset into the value of the characteristic
     * @param descriptor Descriptor to be read
     */
    @Override
    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        super.onDescriptorReadRequest(device, requestId, offset, descriptor);

        if (TelemetryProfile.CLIENT_CONFIG.equals(descriptor.getUuid())){
            Log.d(TAG, "Config descriptor read");
            byte[] returnValue;
            if (mRegisteredDevices.contains(device)) {
                returnValue = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
            } else {
                returnValue = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            }
            mGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0,
                    returnValue);
        } else {
            Log.w(TAG, "Unknown descriptor read request");
            mGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0,
                    null);
        }
    }

    /**
     * This method is called when a remote device requests to write a descriptor.
     * @param device The remote device that has requested the write operation
     * @param requestId The Id of the request
     * @param descriptor Descriptor to be written
     * @param preparedWrite true, if this write operation should be queued for later execution
     * @param responseNeeded true, if the remote device requires a response
     * @param offset Offset into the value of the characteristic
     * @param value The value the client wants to assign to the descriptor
     */
    @Override
    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        // Handle descriptor write requests

        if (TelemetryProfile.CLIENT_CONFIG.equals(descriptor.getUuid())) {
            if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                Log.d(TAG, "Subscribe device to notifications: " + device);
                mRegisteredDevices.add(device);
            } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                Log.d(TAG, "Unsubscribe device from notifications: " + device);
                mRegisteredDevices.remove(device);
            }

            if (responseNeeded) {
                mGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        null);
            }
        } else {
            Log.w(TAG, "Unknown descriptor write request");
            if (responseNeeded) {
                mGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null);
            }
        }
    }
}
