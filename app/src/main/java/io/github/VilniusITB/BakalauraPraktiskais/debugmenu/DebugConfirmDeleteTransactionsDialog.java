package io.github.VilniusITB.BakalauraPraktiskais.debugmenu;

import android.view.WindowManager;
import android.widget.TextView;

import io.github.VilniusITB.BakalauraPraktiskais.R;
import io.github.VilniusITB.BakalauraPraktiskais.data.ReadOnlyTransaction;
import io.github.VilniusITB.BakalauraPraktiskais.dialogs.AbstractDialog;

public class DebugConfirmDeleteTransactionsDialog extends AbstractDialog {

    private final DebugMenu menu;
    private final ReadOnlyTransaction transaction;

    public DebugConfirmDeleteTransactionsDialog(DebugMenu debugMenu, ReadOnlyTransaction transaction) {
        super(debugMenu, R.layout.debug_transations_delete_dialog, true);
        this.menu = debugMenu;
        this.transaction = transaction;
    }

    @Override
    public void showDialog() {
        this.getAlertDialog().show();
        this.getAlertDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        ((TextView)this.getView().findViewById(R.id.dtran_last_warn_text)).setText(this.transaction.getTransactionsID().toString());
        this.getView().findViewById(R.id.dtran_last_warn_purge).setOnClickListener(v->{
            this.hideDialog();
            this.menu.deleteTransaction(this.transaction.getTransactionsID());
        });
        this.getView().findViewById(R.id.dtran_last_warn_safe).setOnClickListener(v-> this.hideDialog());
    }

    @Override
    public void hideDialog() {
        this.getAlertDialog().hide();
    }
}
