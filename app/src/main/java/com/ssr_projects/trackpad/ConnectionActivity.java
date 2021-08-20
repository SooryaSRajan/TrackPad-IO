package com.ssr_projects.trackpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectionActivity extends AppCompatActivity {


    EditText ipAddressBox, portNoBox;
    Button connectToServer;
    String portNo, ipAddress;
    private final String TAG = getClass().getName();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        sharedPreferences = getSharedPreferences("IP_ADDRESS_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String ipAddressPref = sharedPreferences.getString("IP_ADDRESS", null);
        Log.d(TAG, "onCreate: " + ipAddressPref);

        if (ipAddressPref != null) {
                SocketClass.setIpAddress(ipAddressPref);
                Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
        }

        ipAddressBox = findViewById(R.id.ip_address);
        portNoBox = findViewById(R.id.port_no);
        connectToServer = findViewById(R.id.connect);

        ipAddressBox.setText(sharedPreferences.getString("IP", ""));
        portNoBox.setText(sharedPreferences.getString("PORT", ""));


        connectToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portNo = portNoBox.getText().toString();
                ipAddress = ipAddressBox.getText().toString();
                if (portNo.length() == 0 || ipAddress.length() == 0) {
                    Toast.makeText(ConnectionActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString("IP", ipAddress);
                    editor.putString("PORT", portNo);
                    editor.apply();
                    SocketClass.setIpAddress("http://" + ipAddress + ":" + portNo);
                    Log.e(TAG, "onClick: IP Address: " + "http://" + ipAddress + ":" + portNo);
                    Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }


}