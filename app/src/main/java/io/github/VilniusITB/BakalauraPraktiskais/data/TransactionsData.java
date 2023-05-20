package io.github.VilniusITB.BakalauraPraktiskais.data;

import androidx.annotation.NonNull;

import java.util.UUID;

import io.github.VilniusITB.BakalauraPraktiskais.TerminalApp;
import io.github.VilniusITB.BakalauraPraktiskais.enums.TerminalTransactionStatus;
public class TransactionsData {
    private final TerminalApp app;
    private final UUID transactionsID;
    private final long startEpochTime;
    private TerminalTransactionStatus status;
    private long endEpochTime;

    private double amount;

    public TransactionsData(@NonNull TerminalApp app) {
        this.app = app;
        UUID id = UUID.randomUUID();
        while (app.getTransactionsDatastore().doesIDExist(id)) id = UUID.randomUUID();
        this.transactionsID = id;
        this.startEpochTime = System.currentTimeMillis();
        this.endEpochTime = System.currentTimeMillis();
        this.status = TerminalTransactionStatus.PENDING;
        this.amount = 0.0;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public UUID getTransactionsID() {
        return this.transactionsID;
    }
    public long getStartEpochTime() {
        return this.startEpochTime;
    }

    public long getEndEpochTime() {
        return this.endEpochTime;
    }

    public TerminalTransactionStatus getStatus() {
        return this.status;
    }

    public void setStatus(TerminalTransactionStatus status) {
        this.status = status;
    }

    public void updateEndEpochTime() {
        this.endEpochTime = System.currentTimeMillis();
    }

}
