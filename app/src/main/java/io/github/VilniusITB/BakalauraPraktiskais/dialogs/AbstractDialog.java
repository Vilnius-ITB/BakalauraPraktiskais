package io.github.VilniusITB.BakalauraPraktiskais.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public abstract class AbstractDialog {

    private Activity activity;
    private AlertDialog alertDialog;
    private View view;

    public abstract void showDialog();
    public abstract void hideDialog();

    public AbstractDialog(Activity activity, int resource, boolean cancelable) {
        this.activity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        LayoutInflater inflater = this.activity.getLayoutInflater();
        this.view = inflater.inflate(resource,null);
        builder.setView(this.view);
        builder.setCancelable(cancelable);
        this.alertDialog = builder.create();
    }

    public AlertDialog getAlertDialog() {
        return this.alertDialog;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public View getView() {
        return this.view;
    }

}
