package io.github.VilniusITB.BakalauraPraktiskais.debugmenu;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.github.VilniusITB.BakalauraPraktiskais.R;

public class DebugTransactionViewHolder extends RecyclerView.ViewHolder {

    TextView transactionID,transitionStatus,transitionAmount;

    public DebugTransactionViewHolder(@NonNull View itemView) {
        super(itemView);
        this.transactionID = itemView.findViewById(R.id.transtactionid_txt);
        this.transitionStatus = itemView.findViewById(R.id.transition_status);
        this.transitionAmount = itemView.findViewById(R.id.transtactionid_amount);
    }
}
