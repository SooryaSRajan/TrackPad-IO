package com.ssr_projects.trackpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private GestureDetector mDetector;
    private static String TAG = MainActivity.class.getName();
    Socket mSocket = SocketClass.getSocketInstance();
    private boolean twoFingersDownFlag = false;
    LinearLayout mediaControlLayout;
    private boolean isVisible = false;
    SharedPreferences sharedPreferences;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaControlLayout = findViewById(R.id.media_control_layout);
        sharedPreferences = getSharedPreferences("IP_ADDRESS_PREF", MODE_PRIVATE);
        mediaControlLayout.setTranslationY(mediaControlLayout.getHeight());

        if (savedInstanceState != null) {
            isVisible = savedInstanceState.getBoolean("MEDIA_TREY");
            if (!isVisible) {
                mediaControlLayout.setVisibility(View.GONE);
                mediaControlLayout.setTranslationY(mediaControlLayout.getHeight());
            } else {
                mediaControlLayout.setVisibility(View.VISIBLE);
                mediaControlLayout.setTranslationY(0);
            }
        }

        loadingDialog = new LoadingDialog(this, R.layout.loading_dialog);
        loadingDialog.build();
        loadingDialog.setCancelable(false);

        Log.d(getClass().getName(), "onCreate: " + mSocket.connected());

        Emitter.Listener onConnect = args -> {
            System.out.println("Connected to server");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("IP_ADDRESS", SocketClass.getIpAddress());
            editor.apply();
            String ipAddressPref = sharedPreferences.getString("IP_ADDRESS", null);
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
            mSocket.emit("activity", getClass().getName());
        }


        mDetector = new GestureDetector(this, new MyGestureListener());
        View touchPadView = findViewById(R.id.root);
        View leftButton = findViewById(R.id.left_button);
        View rightButton = findViewById(R.id.right_button);

        touchPadView.setOnTouchListener(touchListener);
        leftButton.setOnTouchListener(leftListener);
        rightButton.setOnTouchListener(rightListener);

    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            Log.e(TAG, "onDown: " + event.getPointerCount());
            twoFingersDownFlag = event.getPointerCount() == 2;
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
        sharedPreferences.edit().putString("IP_ADDRESS", null).apply();
        Intent intent = new Intent(this, ConnectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            SocketClass.getSocketInstance().emit("single_tap");
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            SocketClass.getSocketInstance().emit("double_tap");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent event) {
            Log.e(TAG, "Not two: ");

            return true;
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (twoFingersDownFlag) {
                if (distanceY < 0) distanceY += -50;
                else distanceY += 50;
                SocketClass.getSocketInstance().emit("scroll", distanceY * -1);
            } else {
                SocketClass.getSocketInstance().emit("mouse_data", distanceX * -1, distanceY * -1);
            }
            Log.e(getClass().getName(), "onScroll: " + distanceX + " " + distanceY + " ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            return true;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("MEDIA_TREY", isVisible);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
        loadingDialog.destroy();
    }
}
