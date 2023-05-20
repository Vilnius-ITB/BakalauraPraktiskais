package io.github.VilniusITB.BakalauraPraktiskais.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import io.github.VilniusITB.BakalauraPraktiskais.DebugLogger;
import io.github.VilniusITB.BakalauraPraktiskais.R;

public class EnableNFCDialog extends AbstractDialog {

    public EnableNFCDialog(Activity activity) {
        super(activity, R.layout.enable_nfc, false);
    }

    @Override
    public void showDialog() {
        DebugLogger.log("Snowing User NFC is disabled dialog");
        this.getAlertDialog().show();
        this.getView().findViewById(R.id.nfce_exitbtr).setOnClickListener(v->{
            this.getActivity().finish();
            System.exit(0);
        });
        this.getView().findViewById(R.id.nfce_stbtr).setOnClickListener(v->{
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            this.getActivity().startActivity(intent);
            Toast.makeText(this.getActivity(), "Please enable NFC in settings.", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void hideDialog() {
        this.getAlertDialog().dismiss();
    }
}
