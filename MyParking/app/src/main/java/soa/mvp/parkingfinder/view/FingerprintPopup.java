package soa.mvp.parkingfinder.view;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Created by raulvillca on 16/5/17.
 */

public class FingerprintPopup {
    private AppCompatActivity activity;
    private int layout;
    private AlertDialog.Builder alert;
    private AlertDialog dialog;

    public FingerprintPopup(AppCompatActivity activity, int layout) {
        this.activity = activity;
        this.layout = layout;
    }

    public void initPopup(String tag, boolean cancelable) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(layout, null);

        alert = new AlertDialog.Builder(activity);

        alert.setTitle(tag);

        alert.setView(alertLayout);

        alert.setCancelable(cancelable);

    }

    public void start() {
        dialog = alert.create();
        dialog.show();
    }

    public void closePopup() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }
}
