package com.example.arduinobluetoothcontroller;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

class BluetoothUtilities {
    private static final int REQUEST_ENABLE_BT = 1;
    private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    static boolean prerequisitesMet(Activity activity) {
        // exit activity if there is no bluetooth adapter
        if (bluetoothAdapter == null) {
            Log.d("BT", "no adapter");
            Toast.makeText(
                    activity,
                    R.string.no_bluetooth_device,
                    Toast.LENGTH_SHORT
            ).show();
            activity.finish();
            return false;
        }

        // check if bluetooth is enabled, and request to enable it if not
        if (!bluetoothAdapter.isEnabled()) {
            Log.d("BT", "not enabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        return bluetoothAdapter.isEnabled();
    }

    static BluetoothDevice getSavedDevice(Activity activity) {

        BluetoothDevice device;

        // check if there is a saved device
        SharedPreferences prefs = activity.getSharedPreferences(
                "SavedDevice",
                MODE_PRIVATE
        );

        if (prefs == null) {
            Log.d("BT", "no preferences");
        } else {
            Log.d("BT", "preferences found");

            String macAddress = prefs.getString("MacAddress", null);

            if (macAddress != null) {
                Log.d("BT", "MAC address: " + macAddress);
                device = bluetoothAdapter.getRemoteDevice(macAddress);
                if (device != null) {
                    Log.d("BT", "saved device found: " + device.toString());
                    return device;
                } else {
                    Log.d("BT", "saved device not found");
                }
            } else {
                Log.d("BT", "MAC address is null ");
            }
        }
        return null;
    }
}
