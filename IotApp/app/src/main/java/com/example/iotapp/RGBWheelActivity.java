package com.example.iotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.flask.colorpicker.ColorCircle;
import com.flask.colorpicker.ColorCircleDrawable;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.IOException;

import static com.example.iotapp.MainActivity.btSocket;

public class RGBWheelActivity extends AppCompatActivity {

    int r,g,b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb_wheel);

        Button confirmColor = (Button) findViewById(R.id.confirm_btn);
        ColorPickerView picker = (ColorPickerView) findViewById(R.id.color_picker_view);

        picker.addOnColorSelectedListener(selectedColor -> {
            r = Color.red(selectedColor);
            g = Color.green(selectedColor);
            b = Color.blue(selectedColor);
//                Toast.makeText(getApplicationContext(), String.format("%d %d %d", r,g,b), Toast.LENGTH_SHORT).show();

        });

        confirmColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    btSocket.getOutputStream().write(String.format("%d %d %d", r,g,b).getBytes());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });


    }


}
