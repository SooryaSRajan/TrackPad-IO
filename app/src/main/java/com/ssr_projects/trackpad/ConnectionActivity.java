package com.ssr_projects.trackpad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ssr_projects.trackpad.Helpers.SocketClass;

import static com.ssr_projects.trackpad.Constants.Constants.CONFIG_PREFERENCE_KEY;
import static com.ssr_projects.trackpad.Constants.Constants.IP_ADDRESS;
import static com.ssr_projects.trackpad.Constants.Constants.PORT_NUMBER;
import static com.ssr_projects.trackpad.Constants.Constants.SERVER_REQUEST_ADDRESS;

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
        sharedPreferences = getSharedPreferences(CONFIG_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String ipAddressPref = sharedPreferences.getString(SERVER_REQUEST_ADDRESS, null);
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

        ipAddressBox.setText(sharedPreferences.getString(IP_ADDRESS, ""));
        portNoBox.setText(sharedPreferences.getString(PORT_NUMBER, ""));


        connectToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portNo = portNoBox.getText().toString();
                ipAddress = ipAddressBox.getText().toString();
                if (portNo.length() == 0 || ipAddress.length() == 0) {
                    Toast.makeText(ConnectionActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putString(IP_ADDRESS, ipAddress);
                    editor.putString(PORT_NUMBER, portNo);
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