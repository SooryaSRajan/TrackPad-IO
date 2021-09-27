package com.ssr_projects.trackpad.Fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ssr_projects.trackpad.Helpers.Accelerometer;
import com.ssr_projects.trackpad.Helpers.MyGestureDetector;
import com.ssr_projects.trackpad.R;
import com.ssr_projects.trackpad.Helpers.SocketClass;

public class TrackPadFragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor sensor;

    private GestureDetector mDetector;
    private String TAG = getClass().getName();
    private MyGestureDetector myGestureDetector;
    private static TrackPadFragment trackPadFragment;

    public TrackPadFragment() {

    }

    public static TrackPadFragment newInstance() {
        if(trackPadFragment == null)
            trackPadFragment = new TrackPadFragment();
        return trackPadFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_pad, container, false);
        myGestureDetector = new MyGestureDetector();

        //sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        //sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //sensorManager.registerListener(new Accelerometer(), sensor, SensorManager.SENSOR_DELAY_GAME);

        mDetector = new GestureDetector(getActivity(), myGestureDetector);
        View touchPadView = view.findViewById(R.id.root);
        View leftButton = view.findViewById(R.id.left_button);
        View rightButton = view.findViewById(R.id.right_button);

        touchPadView.setOnTouchListener(touchListener);
        leftButton.setOnTouchListener(leftListener);
        rightButton.setOnTouchListener(rightListener);


        return view;
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            Log.e(TAG, "onDown: " + event.getPointerCount());
            myGestureDetector.setTwoFingersDownFlag(event.getPointerCount() == 2);
            return mDetector.onTouchEvent(event);
        }
    };

    View.OnTouchListener leftListener = (v, event) -> {
        v.performClick();
        Log.e(TAG, "onDown: " + event.getPointerCount());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setPressed(true);
                SocketClass.getSocketInstance().emit("left_click_down");
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                v.setPressed(false);
                SocketClass.getSocketInstance().emit("left_click_up");
                break;
        }
        return true;
    };

    View.OnTouchListener rightListener = (v, event) -> {
        v.performClick();
        Log.e(TAG, "onDown: " + event.getPointerCount());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setPressed(true);
                SocketClass.getSocketInstance().emit("right_click_down");
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                v.setPressed(false);
                SocketClass.getSocketInstance().emit("right_click_up");
                break;
        }
        return true;
    };

}