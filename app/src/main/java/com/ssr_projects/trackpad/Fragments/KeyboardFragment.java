package com.ssr_projects.trackpad.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssr_projects.trackpad.R;

public class KeyboardFragment extends Fragment {

    public KeyboardFragment() {
    }

    public static KeyboardFragment newInstance(String param1, String param2) {
        return new KeyboardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        return view;
    }
}