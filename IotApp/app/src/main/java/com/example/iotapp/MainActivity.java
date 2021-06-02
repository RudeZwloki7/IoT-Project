package com.example.iotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //widgets
//    Button btnPaired;
//    ListView deviceList;
    //Bluetooth
    public BluetoothAdapter myBluetooth = null;


    public static BluetoothSocket btSocket = null;
    public boolean isBtConnected = false;
    public Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnWheel = (ImageButton) findViewById(R.id.led_control);
        btnWheel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToRgbWheelActivity();
            }
        });



        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a message that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

    }

    private void goToRgbWheelActivity() {
        Intent switchActivityIntent = new Intent(this, RGBWheelActivity.class);
        startActivity(switchActivityIntent);
    }



@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.toolbar, menu);
    return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.search_bt_devices) {

            PairedDeviceList dialogList = new PairedDeviceList();
            dialogList.show(getSupportFragmentManager(), "custom");
        }

        return super.onOptionsItemSelected(item);
    }
}