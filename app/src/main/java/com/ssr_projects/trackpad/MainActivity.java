package com.ssr_projects.trackpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ssr_projects.trackpad.Fragments.KeyboardFragment;
import com.ssr_projects.trackpad.Fragments.TrackPadFragment;
import com.ssr_projects.trackpad.Helpers.LoadingDialog;
import com.ssr_projects.trackpad.Helpers.SocketClass;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.ssr_projects.trackpad.Constants.Constants.CONFIG_PREFERENCE_KEY;
import static com.ssr_projects.trackpad.Constants.Constants.MEDIA_TREY;
import static com.ssr_projects.trackpad.Constants.Constants.MOUSE_KEYBOARD_RES;
import static com.ssr_projects.trackpad.Constants.Constants.SERVER_REQUEST_ADDRESS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private final Socket mSocket = SocketClass.getSocketInstance();
    private LinearLayout mediaControlLayout, holderLayout;
    private boolean isVisible = false;
    private SharedPreferences sharedPreferences;
    private LoadingDialog loadingDialog;
    private int iconId = R.drawable.ic_baseline_keyboard_24;
    private Fragment trackPadFragment, keyboardFragment;
    FloatingActionButton keyboardTrackPadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaControlLayout = findViewById(R.id.media_control_layout);
        keyboardTrackPadButton = findViewById(R.id.keyboard_mouse_fab);
        holderLayout = findViewById(R.id.holder_linear_layout);


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holderLayout.getLayoutParams();

        sharedPreferences = getSharedPreferences(CONFIG_PREFERENCE_KEY, MODE_PRIVATE);

        loadingDialog = new LoadingDialog(this, R.layout.loading_dialog);
        loadingDialog.build();
        loadingDialog.setCancelable(false);

        trackPadFragment = TrackPadFragment.newInstance();
        keyboardFragment = KeyboardFragment.newInstance();

        if (savedInstanceState != null) {
            isVisible = savedInstanceState.getBoolean(MEDIA_TREY);
            iconId = savedInstanceState.getInt(MOUSE_KEYBOARD_RES);
            keyboardTrackPadButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, iconId));

            if (!isVisible) {
                mediaControlLayout.setVisibility(View.GONE);
                mediaControlLayout.setTranslationY(mediaControlLayout.getHeight());
            } else {
                mediaControlLayout.setVisibility(View.VISIBLE);
                mediaControlLayout.setTranslationY(0);
            }
        } else {
            mediaControlLayout.setTranslationY(mediaControlLayout.getHeight());
        }

        if (iconId == R.drawable.ic_baseline_keyboard_24) {
            setTrackPadLayout();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, R.id.media_control);
        } else {
            setKeyboardLayout();
            params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        }

        keyboardTrackPadButton.setOnClickListener(view -> {
            if (iconId == R.drawable.ic_baseline_keyboard_24) {
                keyboardTrackPadButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_baseline_mouse_24));
                iconId = R.drawable.ic_baseline_mouse_24;
                setKeyboardLayout();
                params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else {
                keyboardTrackPadButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_baseline_keyboard_24));
                iconId = R.drawable.ic_baseline_keyboard_24;
                setTrackPadLayout();
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, R.id.media_control);
            }
        });

        configureServerConnection();
    }

    private void setKeyboardLayout() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, keyboardFragment).commit();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    private void setTrackPadLayout() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, trackPadFragment).commit();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void configureServerConnection() {
        Log.d(getClass().getName(), "onCreate: " + mSocket.connected());
        Emitter.Listener onConnect = args -> {
            System.out.println("Connected to server");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SERVER_REQUEST_ADDRESS, SocketClass.getIpAddress());
            editor.apply();
            String ipAddressPref = sharedPreferences.getString(SERVER_REQUEST_ADDRESS, null);
            Log.d(TAG, "onCreate: " + ipAddressPref);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> loadingDialog.dismiss());
        };

        Emitter.Listener onDisconnect = args -> {
            System.out.println("Disconnected from server " + args);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> loadingDialog.show());

        };

        if (!mSocket.connected()) {
            loadingDialog.show();
            mSocket.connect();
            mSocket.on("connect", onConnect);
            mSocket.on("disconnect", onDisconnect);
        }
    }


    public void play(View view) {
        SocketClass.getSocketInstance().emit("play");
    }

    public void increaseVolume(View view) {
        SocketClass.getSocketInstance().emit("increase_volume");
    }

    public void decreaseVolume(View view) {
        SocketClass.getSocketInstance().emit("decrease_volume");
    }

    public void muteVolume(View view) {
        SocketClass.getSocketInstance().emit("mute_volume");
    }

    public void copyContent(View view) {
        SocketClass.getSocketInstance().emit("copy");
    }

    public void pasteContent(View view) {
        SocketClass.getSocketInstance().emit("paste");
    }

    public void undo(View view) {
        SocketClass.getSocketInstance().emit("undo");
    }

    public void redo(View view) {
        SocketClass.getSocketInstance().emit("redo");
    }

    public void cut(View view) {
        SocketClass.getSocketInstance().emit("cut");
    }

    public void reveal_hide_media(View view) {
        isVisible = !isVisible;
        if (!isVisible) {
            mediaControlLayout.animate().translationY(mediaControlLayout.getHeight()).setDuration(getResources().getInteger(
                    android.R.integer.config_shortAnimTime))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mediaControlLayout.setVisibility(View.GONE);
                        }
                    });
        } else {
            mediaControlLayout.animate().translationY(0).setDuration(getResources().getInteger(
                    android.R.integer.config_shortAnimTime))
                    .setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            mediaControlLayout.setVisibility(View.VISIBLE);
                            super.onAnimationStart(animation);
                        }
                    });
        }
    }

    public void go_back(View view) {
        mSocket.disconnect();
        SocketClass.destroySocketInstance();
        sharedPreferences.edit().putString(SERVER_REQUEST_ADDRESS, null).apply();
        Intent intent = new Intent(this, ConnectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MEDIA_TREY, isVisible);
        outState.putInt(MOUSE_KEYBOARD_RES, iconId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
        loadingDialog.destroy();
    }
}
