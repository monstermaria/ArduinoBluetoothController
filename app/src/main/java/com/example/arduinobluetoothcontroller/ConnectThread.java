package com.example.arduinobluetoothcontroller;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    BluetoothCommunicationThread communicationThread;

    ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//        UUID uuid = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb");

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            Log.d("connect", "trying to create socket");
            tmp = device.createRfcommSocketToServiceRecord(uuid);
            Log.d("connect", "socket created");
        } catch (IOException e) {
            Log.e("connect", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    @Override
    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            Log.d("connect", "trying to connect");
            mmSocket.connect();
            Log.d("connect", "connected");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("connect", "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        communicationThread = new BluetoothCommunicationThread(mmSocket);
        communicationThread.start();
        Log.d("connect", "started communication thread");

    }

    // Closes the client socket and causes the thread to finish.
    void cancel() {
        // stop the thread responsible for reading
        if (communicationThread != null) {
            communicationThread.interrupt();
            communicationThread.cancel();
        }

        // close the socket
        try {
            Log.d("connect", "trying to close socket");
            mmSocket.close();
            Log.d("connect", "socket closed");
        } catch (IOException e) {
            Log.e("connect", "Could not close the client socket", e);
        }
    }
}