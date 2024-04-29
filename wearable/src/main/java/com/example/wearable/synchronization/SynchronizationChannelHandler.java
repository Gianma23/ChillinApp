package com.example.wearable.synchronization;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.ChannelClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SynchronizationChannelHandler extends Service {
    // private ChannelClient.Channel channel;
    // private OutputStream outputStream;
    // private String smartphoneID;
    // private final String communicationPath = "chillinapp";
    private final String TAG = "SynchronizationChannelHandler";
    private final String CHANNEL_MSG = "chillinapp";

    public SynchronizationChannelHandler() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
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

        if (intent.getAction() != null && intent.getAction().equals("SEND")) {
            Log.d(TAG, "Sending data");
            sendData();
        } else if (intent.getAction() != null && intent.getAction().equals("STOP_SERVICE")) {
            Log.d(TAG, "Stopping service");
            stopSelf();
        } else if (intent.getAction() != null && intent.getAction().equals("START_SERVICE")) {
            Log.d(TAG, "Starting service");
        } else
            Log.w(TAG, "No action found");

        return START_STICKY;
    }

    private void sendData() {
        Runnable toRun = () -> {
            String nodeId = getNode();
            Log.d(TAG, "Node: " + nodeId);

            Task<ChannelClient.Channel> channelTask = Wearable.getChannelClient(getApplicationContext()).openChannel(nodeId, CHANNEL_MSG);
            channelTask.addOnSuccessListener(channel -> {
                Log.d(TAG, "onSuccess " + channel.getNodeId());
                Task<OutputStream> outputStreamTask = Wearable.getChannelClient(getApplicationContext()).getOutputStream(channel);
                outputStreamTask.addOnSuccessListener(outputStream -> {
                    Log.d(TAG, "output stream onSuccess");
                    try {
                        outputStream.write("Hello".getBytes());
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error in sending data: " + e);
                    }
                });
            });
        };
        Thread run = new Thread(toRun);
        run.start();
    }

    private String getNode() {
        String nodeId = null;

        Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();

        try {
            List<Node> nodes = Tasks.await(nodeListTask);

            if (nodes.size() != 1) {
                throw new IllegalStateException("Unexpected number of nodes found: " + nodes.size());
            }
            nodeId = nodes.get(0).getId();

        } catch (ExecutionException exception) {
            Log.e(TAG, "Task failed: " + exception);

        } catch (InterruptedException exception) {
            Log.e(TAG, "Interrupt occurred: " + exception);
        }

        return nodeId;
    }
}

