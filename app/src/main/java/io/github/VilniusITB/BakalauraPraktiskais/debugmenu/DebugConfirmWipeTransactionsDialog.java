package io.github.VilniusITB.BakalauraPraktiskais.debugmenu;

import android.view.WindowManager;

import io.github.VilniusITB.BakalauraPraktiskais.R;
import io.github.VilniusITB.BakalauraPraktiskais.dialogs.AbstractDialog;

public class DebugConfirmWipeTransactionsDialog extends AbstractDialog {

    private final DebugMenu menu;

    public DebugConfirmWipeTransactionsDialog(DebugMenu debugMenu) {
        super(debugMenu, R.layout.debug_transations_wipe_dialog, true);
        this.menu = debugMenu;
    }

    @Override
    public void showDialog() {
        this.getAlertDialog().show();
        this.getAlertDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        this.getView().findViewById(R.id.dtran_last_warn_purge).setOnClickListener(v-> this.menu.wipeTransactions());
        this.getView().findViewById(R.id.dtran_last_warn_safe).setOnClickListener(v-> this.hideDialog());
    }

    @Override
    public void hideDialog() {
        this.getAlertDialog().hide();
    }
}
