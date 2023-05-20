package io.github.VilniusITB.BakalauraPraktiskais.debugmenu;

import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.VilniusITB.BakalauraPraktiskais.R;
import io.github.VilniusITB.BakalauraPraktiskais.data.ReadOnlyTransaction;
import io.github.VilniusITB.BakalauraPraktiskais.dialogs.AbstractDialog;

public class DebugTransactionLogDialog extends AbstractDialog {

    private final ReadOnlyTransaction transaction;
    private DebugMenu menu;

    public DebugTransactionLogDialog(DebugMenu menu, ReadOnlyTransaction transaction) {
        super(menu, R.layout.transaction_log, true);
        this.transaction = transaction;
        this.menu = menu;
    }

    @Override
    public void showDialog() {
        this.getAlertDialog().show();
        this.getAlertDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        ((TextView)this.getView().findViewById(R.id.transation_a_id)).setText("ID: "+this.transaction.getTransactionsID().toString());
        ((TextView)this.getView().findViewById(R.id.transation_a_amount)).setText("Amount: "+this.transaction.getAmount()+"$");
        ((TextView)this.getView().findViewById(R.id.transation_a_result)).setText("Result: "+this.transaction.getStatus().name());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        ((TextView)this.getView().findViewById(R.id.transation_a_sd)).setText("Start: "+dateFormat.format(new Date(this.transaction.getStartEpochTime())));
        ((TextView)this.getView().findViewById(R.id.transation_a_ed)).setText("End: "+dateFormat.format(new Date(this.transaction.getEndEpochTime())));

        this.getView().findViewById(R.id.transation_a_closebtr).setOnClickListener(v->this.hideDialog());
        this.getView().findViewById(R.id.transation_a_delbtr).setOnClickListener(v->{
            this.hideDialog();
            new DebugConfirmDeleteTransactionsDialog(this.menu,this.transaction).showDialog();
        });
    }

    @Override
    public void hideDialog() {
        this.getAlertDialog().hide();
    }
}
