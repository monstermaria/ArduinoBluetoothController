package com.example.arduinobluetoothcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice chosenBluetoothDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(
                    this,
                    R.string.no_bluetooth_device,
                    Toast.LENGTH_SHORT
            ).show();
            finish();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
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
        }
    }

    private class PairedDeviceOnClick implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
            Log.d("paired", "Device clicked: " + device.getName());
            chosenBluetoothDevice = device;
        }
    }

}
