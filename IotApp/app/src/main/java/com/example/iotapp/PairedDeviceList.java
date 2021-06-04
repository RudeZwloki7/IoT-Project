package com.example.iotapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.iotapp.MainActivity.btSocket;
import static com.example.iotapp.MainActivity.myUUID;


public class PairedDeviceList extends DialogFragment {

    public MainActivity mainActivity;
    public ListView deviceList;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_paired_device_list, container, true);

        deviceList = (ListView) view.findViewById(R.id.device_list);

        pairedDevicesList();
        return view;
    }


    private void pairedDevicesList() {

        mainActivity.pairedDevices = mainActivity.myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        if (mainActivity.pairedDevices.size() > 0) {
            for (BluetoothDevice bt : mainActivity.pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            Toast.makeText(mainActivity.getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity.getApplicationContext(), android.R.layout.simple_list_item_1, list);

        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked


    }

    private final AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            MainActivity.EXTRA_ADDRESS = address;


            try {
                if (btSocket != null || mainActivity.isBtConnected)
                    btSocket.close();
                    mainActivity.isBtConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (btSocket == null || !mainActivity.isBtConnected) {
                    mainActivity.myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice remoteDevice = mainActivity.myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection

                    mainActivity.isBtConnected = true;
                    Toast.makeText(mainActivity.getApplicationContext(), "Device connected", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                mainActivity.isBtConnected = false;//if the try failed, you can check the exception here
            }

            try {
//                btSocket.getOutputStream().write(String.format("Automatic: %b", is_automatic).getBytes());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
                boolean is_automatic = prefs.getBoolean("automatic", false);
                btSocket.getOutputStream().write(String.format("Automatic: %d", is_automatic ? 1 : 0).getBytes());
//                System.out.println("Automatic is "+ is_automatic);
                Toast.makeText(mainActivity.getApplicationContext(), String.valueOf(is_automatic), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


}