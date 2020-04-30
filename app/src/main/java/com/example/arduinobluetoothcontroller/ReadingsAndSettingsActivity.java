package com.example.arduinobluetoothcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ReadingsAndSettingsActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readings_and_settings);

        Log.d("lifecycle", "onCreate " + this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("lifecycle", "onStart " + this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("lifecycle", "onRestart " + this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("lifecycle", "onResume " + this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("lifecycle", "onPause " + this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("lifecycle", "onStop " + this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("lifecycle", "onDestroy " + this);
    }

    public void getReadings(View view) {
        Log.d("getReadings", "start reading");

        // get bluetooth socket
        ConnectThread connectionThread = getCommunicationChannel();
        BluetoothCommunicationThread communicationThread;

        if (connectionThread != null) {
            communicationThread = connectionThread.communicationThread;
            if (communicationThread != null) {
                // send read command
                communicationThread.writeToOutputStream("1");

                // receive answer
                try {
                    communicationThread.join(5000);
                    ArrayList<Integer> data = communicationThread.getData();
                    Log.d("getReadings", data.toString());

                    // populate views
                    showReadings(data);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                // release resources
                connectionThread.cancel();

            } else {
                Log.e("connection", "no communication channel opened (communication thread)");
            }
        } else {
            Log.e("connection", "no communication channel opened (connection thread)");
        }

        Log.d("getReadings", "end reading");
    }

    public void writeSettings(View view) {
        Log.d("writeSettings", "start writing");

        // gather data to send
        EditText setpointEditText = findViewById(R.id.setpointEditText);
        RadioGroup start = findViewById(R.id.start);

        String setpoint = setpointEditText.getText().toString();
        Log.d("writeSettings", setpoint);

        // send data

        Log.d("writeSettings", "end writing");
    }

    void showReadings(ArrayList<Integer> data) {
        // get handles for the views
        TextView pressureView = findViewById(R.id.pressureView);
        TextView currentView = findViewById(R.id.currentView);
        TextView runningView = findViewById(R.id.runningView);
        TextView alarmView = findViewById(R.id.alarmView);
        TextView setpointView = findViewById(R.id.setpointView);
        TextView startView = findViewById(R.id.startView);

        // set data in views
        pressureView.setText(getString(R.string.pressure) + ": " + data.get(5));
        currentView.setText(getString(R.string.current) + ": " + data.get(6));
        runningView.setText(getString(R.string.running_signal) + ": " + data.get(1));
        alarmView.setText(getString(R.string.sum_alarm) + ": " + data.get(2));
        setpointView.setText(getString(R.string.setpoint) + ": " + data.get(4));
        startView.setText(getString(R.string.start_signal) + ": " + data.get(3));
    }

    ConnectThread getCommunicationChannel() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device;
        ConnectThread connectionThread;

        // check if bluetooth is enabled
        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // get stored bluetooth device
        SharedPreferences prefs = getSharedPreferences(
                "ArduinoBluetoothController",
                MODE_PRIVATE
        );

        String macAddress = prefs.getString("MacAddress", null);

        if (macAddress != null) {
            device = adapter.getRemoteDevice(macAddress);
            if (device != null) {
                connectionThread = new ConnectThread(device);
                Log.d("getReadings", "connection thread state: " + connectionThread.getState());
                connectionThread.start();
                Log.d("getReadings", "connection thread state: " + connectionThread.getState());
                try {
                    connectionThread.join(5000);
                    Log.d("getReadings", "connection thread state: " + connectionThread.getState());
                    return connectionThread;
                } catch (InterruptedException e) {
                    Log.e("getReadings", "connection thread state: " + connectionThread.getState());
                    e.printStackTrace();
                }
            } else {
                Log.e("connection", "bluetooth device not found");
            }
        } else {
            Log.e("connection", "MAC address is null ");
        }

        return null;
    }
}
