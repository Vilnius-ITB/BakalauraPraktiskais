package io.github.VilniusITB.BakalauraPraktiskais.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import io.github.VilniusITB.BakalauraPraktiskais.R;

public class UnsupportedDeviceDialog {
    private Activity activity;
    private AlertDialog alertDialog;
    private View iv;

    /**

     Constructs a new instance of the UnsupportedDeviceDialog class with the given activity.
     This dialog is used to inform the user that their device is not supported for NFC payments.
     @param activity the activity in which the dialog is shown.
     */

    public UnsupportedDeviceDialog(Activity activity) {
        this.activity = activity;
        AlertDialog.Builder b = new AlertDialog.Builder(this.activity);
        LayoutInflater inflater = this.activity.getLayoutInflater();
        this.iv = inflater.inflate(R.layout.notnfc_device,null);
        b.setView(this.iv);
        b.setCancelable(false);
        this.alertDialog = b.create();
    }

    /**

     Displays the dialog box associated with this UnsupportedDeviceDialog object.
     The dialog box is displayed on top of the activity associated with this object.
     The dialog box includes a button that closes the activity when clicked.
     @throws IllegalStateException if the activity associated with this object has been destroyed or is not currently visible
     */

    public void displayDialog() {
        this.alertDialog.show();
        this.iv.findViewById(R.id.uexit).setOnClickListener(v -> {
            this.activity.finish();
            System.exit(0);
        });
    }

}
