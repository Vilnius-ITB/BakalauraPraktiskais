package io.github.VilniusITB.BakalauraPraktiskais.dialogs;

import android.app.Activity;

import io.github.VilniusITB.BakalauraPraktiskais.R;

public class UnsupportedDeviceDialog extends AbstractDialog {

    /**
     Constructs a new instance of the UnsupportedDeviceDialog class with the given activity.
     This dialog is used to inform the user that their device is not supported for NFC payments.
     @param activity the activity in which the dialog is shown.
     */
    public UnsupportedDeviceDialog(Activity activity) {
        super(activity, R.layout.notnfc_device,false);
    }

    @Override
    /**
     Displays the dialog box associated with this UnsupportedDeviceDialog object.
     The dialog box is displayed on top of the activity associated with this object.
     The dialog box includes a button that closes the activity when clicked.
     @throws IllegalStateException if the activity associated with this object has been destroyed or is not currently visible
     */
    public void showDialog() {
        this.getAlertDialog().show();
        this.getView().findViewById(R.id.uexit).setOnClickListener(v -> {
            this.getActivity().finish();
            System.exit(0);
        });
    }

    @Override
    public void hideDialog() {
        this.getAlertDialog().dismiss();
    }
}
