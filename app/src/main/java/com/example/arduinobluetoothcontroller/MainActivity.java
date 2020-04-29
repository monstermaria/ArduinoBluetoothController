package com.example.arduinobluetoothcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice chosenBluetoothDevice;
    ConnectThread connectionThread;
    View chosenDeviceView;
    byte testByte = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("lifecycle", "onCreate " + this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(
                    this,
                    R.string.no_bluetooth_device,
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        }

        // set callback for when data has been entered
        EditText dataInputView = findViewById(R.id.sendText);

        dataInputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                Log.d("lifecycle", "enterData.onEditorAction: " + v.getText() );
                Log.d("lifecycle", "enterData.onEditorAction: " + actionId );
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = true;
                }
                connectionThread.communicationThread.writeToOutputStream(v.getText());

                return handled;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("lifecycle", "onStart " + this);
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        SharedPreferences prefs = getSharedPreferences(
                "ArduinoBluetoothController",
                MODE_PRIVATE
        );

        String macAddress = prefs.getString("MacAddress", null);

        if (macAddress != null) {
            Log.d("onStart", "MAC address: " + macAddress);
            chosenBluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
            connectToDevice(chosenBluetoothDevice);
        } else {
            Log.d("onStart", "MAC address is null ");
        }
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

        if (connectionThread != null) {
            connectionThread.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("lifecycle", "onDestroy " + this);
    }

    public void listPairedDevices(View view) {
        if (bluetoothAdapter.isEnabled()) {

            for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
                Log.d("paired", device.getName());
            }

            BluetoothDeviceArrayAdapter adapter = new BluetoothDeviceArrayAdapter(
                    this,
                    bluetoothAdapter.getBondedDevices()
            );

            ListView pairedDevicesView = findViewById(R.id.pairedListView);
            pairedDevicesView.setAdapter(adapter);
            pairedDevicesView.setOnItemClickListener(new PairedDeviceOnClick());
        } else {
            // request to enable bluetooth
            Log.d("paired", "bluetooth not enabled");
        }
    }

    private class PairedDeviceOnClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
            Log.d("paired", "Device clicked: " + device.getName());

            Log.d("paired", "services on this device:");
            ParcelUuid[] services = device.getUuids();
            for (ParcelUuid s : services) {
                Log.d("paired", s.toString());
            }

            Log.d("paired", "saving chosen device to shared preferences");
            // save chosen bluetooth device
            SharedPreferences.Editor editor = getSharedPreferences(
                    "ArduinoBluetoothController",
                    MODE_PRIVATE
            ).edit();

            editor.putString("MacAddress", device.getAddress());
            editor.apply();
            editor.commit();

            chosenBluetoothDevice = device;
            connectToDevice(device);
        }
    }

    void connectToDevice(BluetoothDevice device) {
        // get view elements to show info about the chosen/connected device
        chosenDeviceView = findViewById(R.id.chosenBluetoothDeviceView);
        TextView tvName = chosenDeviceView.findViewById(R.id.tvName);
        TextView tvMacAddress = chosenDeviceView.findViewById(R.id.tvMacAddress);

        if (device != null) {
            Log.d("connectToDevice", "setting chosen device view and connecting to device");
            tvName.setText(device.getName());
            tvMacAddress.setText(device.getAddress());
            connectionThread = new ConnectThread(device);
            connectionThread.start();
        } else {
            Log.d("connectToDevice", "no device given");
        }
    }

    public void writeByte(View view) {
        Log.d("writeData main", "writeData called, byte = " + testByte);
        connectionThread.communicationThread.writeByteToOutputStream(testByte);
        testByte++;
    }
}
