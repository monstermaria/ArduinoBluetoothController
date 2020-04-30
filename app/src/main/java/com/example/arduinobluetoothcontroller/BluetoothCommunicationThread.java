package com.example.arduinobluetoothcontroller;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

class BluetoothCommunicationThread extends Thread {

    private BluetoothSocket socket;
    private InputStream input;
    private OutputStream output;
    private ArrayList<Integer> data = new ArrayList<>();

    BluetoothCommunicationThread(BluetoothSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        Log.d("communication", "run reader");

        // get input stream
        try {
            input = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // listen for incoming data as long as the socket is connected
        while (socket.isConnected() && !dataReady()) {
            try {
                if (input.available() > 0) {
                    // handle data
                    int d = input.read();
                    data.add(d);
                    Log.d("communication", "input read: " + d);
                }
            } catch (IOException e) {
                Log.d("communication", "read failed");
                e.printStackTrace();
            }
        }
    }

    boolean dataReady() {
        return data.size() >= 7;
    }

    ArrayList<Integer> getData() {
        ArrayList<Integer> dataToDeliver = new ArrayList<>(data);
        data = new ArrayList<>();
        return dataToDeliver;
    }

    void writeToOutputStream(CharSequence data) {

        Log.d("communication", "write");

        // get output stream
        try {
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // convert from char sequence to bytes
        byte[] bytes = data.toString().getBytes(Charset.defaultCharset());

        // write bytes
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

        // get output stream
        try {
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
