package com.ssr_projects.trackpad.Helpers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class Accelerometer implements SensorEventListener {

    int t = 0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if((int)x != 0){
            t++;
        }
        else{
            t = 0;
        }
        Log.d(getClass().getName(), "onSensorChanged: " + ((int) x * t * t) + " " + t);
        if(x != 0)
        SocketClass.getSocketInstance().emit("mouse_data", ((int) x * t * t), 0);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
