package io.github.VilniusITB.BakalauraPraktiskais.debugmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.VilniusITB.BakalauraPraktiskais.R;
import io.github.VilniusITB.BakalauraPraktiskais.data.ReadOnlyTransaction;

public class DebugTransactionViewAdapter extends RecyclerView.Adapter<DebugTransactionViewHolder> {

    private Context context;
    private List<ReadOnlyTransaction> data;

    private DebugMenu menu;

    public DebugTransactionViewAdapter(DebugMenu menu, List<ReadOnlyTransaction> data) {
        this.context = menu.getApplicationContext();
        this.menu = menu;
        this.data = new ArrayList<>(data);
        Collections.reverse(this.data);
    }


    @NonNull
    @Override
    public DebugTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DebugTransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.transaction_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DebugTransactionViewHolder holder, int position) {
        ReadOnlyTransaction transaction = data.get(position);
        holder.transactionID.setText(transaction.getTransactionsID().toString());
        holder.transitionStatus.setText(transaction.getStatus().name());
        holder.transitionAmount.setText("AMOUNT: "+transaction.getAmount()+"$");
        holder.itemView.setOnClickListener(v-> new DebugTransactionLogDialog(this.menu,transaction).showDialog());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
