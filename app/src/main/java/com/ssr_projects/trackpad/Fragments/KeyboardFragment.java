package com.ssr_projects.trackpad.Fragments;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ssr_projects.trackpad.Helpers.SocketClass;
import com.ssr_projects.trackpad.R;

import java.io.IOException;
import java.util.Objects;

public class KeyboardFragment extends Fragment {

    private static KeyboardFragment keyboardFragment;
    private final String TAG = getClass().getName();


    public KeyboardFragment() {
    }

    public static KeyboardFragment newInstance() {
        if (keyboardFragment == null)
            keyboardFragment = new KeyboardFragment();
        return keyboardFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);

        for (int i = 0; i < 71; i++) {
            String buttonID = "_" + (i + 1);
            Log.d(TAG, "onCreateView: " + buttonID + " " + Objects.requireNonNull(getActivity()).getPackageName());
            int resID = getResources().getIdentifier(buttonID, "id", Objects.requireNonNull(getActivity()).getPackageName());
            Log.d(TAG, "onCreateView: " + resID);
            view.findViewById(resID).setOnTouchListener((View view1, MotionEvent motionEvent) -> {
                view1.performClick();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        MediaPlayer.create(getActivity(), R.raw.key_click).start();
                        view1.setPressed(true);
                        Log.d(TAG, "onTouch: " + view1.getTag());
                        SocketClass.getSocketInstance().emit("key_down", view1.getTag());
                        break;

                    case MotionEvent.ACTION_UP:
                        view1.setPressed(false);
                        SocketClass.getSocketInstance().emit("key_up", view1.getTag());
                        break;
                }
                return true;
            });

        }

        return view;
    }
}