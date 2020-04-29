package com.example.arduinobluetoothcontroller;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

class BluetoothCommunicationThread extends Thread {

    private BluetoothSocket socket;
    private InputStream input;
    private OutputStream output;

    private byte[] inputBytes, outputBytes;

    BluetoothCommunicationThread(BluetoothSocket socket) {

        this.socket = socket;
//        Log.d("communication", "connection established");
    }

    @Override
    public void run() {
        Log.d("communication", "run");

        if (socket.isConnected()) {
            Log.d("communication", "socket connected");
        } else {
            Log.d("communication", "socket not connected");
            try {
                Log.d("communication", "trying to establish connection");
                socket.connect();
                Log.d("communication", "connection established");
            } catch (IOException e) {
                Log.d("communication", "connection failed");
                e.printStackTrace();
                return;
            }
        }

        try {
            input = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        while (socket.isConnected()) {
//            try {
//                if (input.available() > 0) {
//                    int data = input.read();
//                    Log.d("communication", "input read: " + data);
//                }
//            } catch (IOException e) {
//                Log.d("communication", "read failed");
//                e.printStackTrace();
//            }
//        }
    }

    void writeToOutputStream(CharSequence data) {
        Log.d("communication", "write");
        byte[] bytes = data.toString().getBytes(Charset.defaultCharset());
        try {
            Log.d("communication", "trying to write data");
            output.write(bytes);
            Log.d("communication", "data written");
        } catch (IOException e) {
            Log.d("communication", "write failed");
            e.printStackTrace();
        }
    }

    void writeByteToOutputStream(byte myByte) {
        Log.d("communication", "write byte");
        byte[] bytes = {myByte};
        try {
            Log.d("communication", "trying to write byte");
            output.write(bytes);
            Log.d("communication", "byte written");
        } catch (IOException e) {
            Log.d("communication", "write byte failed");
            e.printStackTrace();
        }

    }

    void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
