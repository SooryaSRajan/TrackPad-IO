package com.ssr_projects.trackpad.Helpers;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.ssr_projects.trackpad.Helpers.SocketClass;

public class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private boolean twoFingersDownFlag = false;

    public void setTwoFingersDownFlag(boolean twoFingersDownFlag) {
        this.twoFingersDownFlag = twoFingersDownFlag;
    }

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
