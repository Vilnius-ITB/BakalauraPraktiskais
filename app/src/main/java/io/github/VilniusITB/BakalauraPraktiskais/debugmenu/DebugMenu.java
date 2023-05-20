package io.github.VilniusITB.BakalauraPraktiskais.debugmenu;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.github.VilniusITB.BakalauraPraktiskais.DebugLogger;
import io.github.VilniusITB.BakalauraPraktiskais.R;
import io.github.VilniusITB.BakalauraPraktiskais.TransactionsDatastore;

public class DebugMenu extends AppCompatActivity {

    private static Timer loopedTimer = new Timer();
    private TextView debugLog;
    private RecyclerView debugRW;

    private DebugConfirmWipeTransactionsDialog confirmWipeTransactionsDialog;
    private TransactionsDatastore datastore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.debug_menu);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        this.confirmWipeTransactionsDialog = new DebugConfirmWipeTransactionsDialog(this);
        this.debugLog = this.findViewById(R.id.debug_log);
        this.debugRW = this.findViewById(R.id.debug_rw);
        this.debugLog.setText(DebugLogger.getLogsAsString());
        if (loopedTimer==null) {
            loopedTimer = new Timer();
            loopedTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (debugLog!=null) debugLog.setText(DebugLogger.getLogsAsString());
                }
            }, 0, 500);
        }
        this.findViewById(R.id.dtg_clearlog).setOnClickListener(t->{
            DebugLogger.clearLogs();
            debugLog.setText(DebugLogger.getLogsAsString());
        });
        this.datastore = new TransactionsDatastore(this);
        this.findViewById(R.id.dtg_clearalltransaction).setOnClickListener(t->{
            if (this.datastore.getTransactions().size()==0) {
                Toast.makeText(this, "Transactions logs are empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            this.confirmWipeTransactionsDialog.showDialog();
        });
        this.applyRWList();
    }

    private void applyRWList() {
        TextView t = this.findViewById(R.id.dgb_tmain4);
        t.setText("Items: "+this.datastore.getTransactions().size());
        this.debugRW.setLayoutManager(new LinearLayoutManager(this));
        this.debugRW.setAdapter(new DebugTransactionViewAdapter(this, this.datastore.getTransactions()));
    }

    void wipeTransactions() {
        if (this.confirmWipeTransactionsDialog.getAlertDialog().isShowing()) this.confirmWipeTransactionsDialog.hideDialog();
        this.datastore.wipeTransactions();
        this.applyRWList();
    }

    void deleteTransaction(UUID id) {
        this.datastore.removeTransactionFromDB(id);
        this.applyRWList();
    }
}
