package com.example.arduinobluetoothcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class NewBTSettings extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_b_t_settings);

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

        // show chosen device
        showChosenDevice();

        // populate list of paired devices
        listPairedDevices();
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

    void showChosenDevice() {
        View chosenDeviceView = findViewById(R.id.chosenBluetoothDeviceView);
        BluetoothDevice chosenDevice = BluetoothUtilities.getSavedDevice(this);

        if (chosenDevice != null) {
            TextView tvName = chosenDeviceView.findViewById(R.id.tvName);
            TextView tvMacAddress = chosenDeviceView.findViewById(R.id.tvMacAddress);
            tvName.setText(chosenDevice.getName());
            tvMacAddress.setText(chosenDevice.getAddress());
        }
    }

    public void listPairedDevices() {
        if (BluetoothUtilities.prerequisitesMet(this)) {

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
        }
    }

    private class PairedDeviceOnClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
            Log.d("paired", "Device clicked: " + device.getName());

            // save chosen bluetooth device
            Log.d("paired", "saving chosen device to shared preferences");
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();

            editor.putString("MacAddress", device.getAddress());
            editor.apply();

            // update chosen device view
            showChosenDevice();
        }
    }

    public void openBluetoothSettings(View view) {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }
}
