package com.example.arduinobluetoothcontroller;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

/* copied and adapted from
 * https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 */
class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDevice> {

    BluetoothDeviceArrayAdapter(Context context, Set<BluetoothDevice> devices) {
        super(context, 0, new ArrayList<>(devices));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.bluetooth_device,
                    parent,
                    false
            );
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvMacAddress = convertView.findViewById(R.id.tvMacAddress);
        // Populate the data into the template view using the data object
        if (device != null) {
            tvName.setText(device.getName());
            tvMacAddress.setText(device.getAddress());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
