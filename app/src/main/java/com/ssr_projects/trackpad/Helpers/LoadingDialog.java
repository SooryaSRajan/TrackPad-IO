package com.ssr_projects.trackpad.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;

import com.ssr_projects.trackpad.ConnectionActivity;
import com.ssr_projects.trackpad.R;

import static android.content.Context.MODE_PRIVATE;

public class LoadingDialog {

    private final AlertDialog.Builder builder;
    private final View dialogLayout;
    private AlertDialog dialog;
    private final Activity activity;

    public LoadingDialog(Activity activity, int resId) {
        this.activity = activity;
        builder = new AlertDialog.Builder(activity);
        dialogLayout = activity.getLayoutInflater().inflate(resId, null);
    }

    public void build() {
        if (dialogLayout != null) {
            dialogLayout.findViewById(R.id.config_connect)
                    .setOnClickListener(view -> {
                        SocketClass.getSocketInstance().disconnect();
                        SocketClass.destroySocketInstance();
                        activity.getSharedPreferences("IP_ADDRESS_PREF", MODE_PRIVATE).edit().putString("IP_ADDRESS", null).apply();
                        Intent intent = new Intent(activity, ConnectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();

                    });
            builder.setView(dialogLayout);
            dialog = builder.create();
        }
    }

    public void setCancelable(boolean isCancelable) {
        if (dialog != null) {
            dialog.setCancelable(isCancelable);
            dialog.setCanceledOnTouchOutside(isCancelable);
        }
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void destroy(){
        dialog = null;
    }


}
