package com.ssr_projects.trackpad.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ssr_projects.trackpad.Helpers.MyGestureDetector;
import com.ssr_projects.trackpad.R;
import com.ssr_projects.trackpad.Helpers.SocketClass;

public class TrackPadFragment extends Fragment {

    private GestureDetector mDetector;
    private String TAG = getClass().getName();
    private MyGestureDetector myGestureDetector;

    public TrackPadFragment() {
    }

    public static TrackPadFragment newInstance() {
        return new TrackPadFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_pad, container, false);
        myGestureDetector = new MyGestureDetector();

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
                SocketClass.getSocketInstance().emit("left_click_down");
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
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
                SocketClass.getSocketInstance().emit("right_click_down");
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                SocketClass.getSocketInstance().emit("right_click_up");
                break;
        }
        return true;
    };

}