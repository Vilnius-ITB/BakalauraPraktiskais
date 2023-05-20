package io.github.VilniusITB.BakalauraPraktiskais;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.VilniusITB.BakalauraPraktiskais.data.ReadOnlyTransaction;
import io.github.VilniusITB.BakalauraPraktiskais.data.TransactionsData;
import io.github.VilniusITB.BakalauraPraktiskais.enums.TerminalTransactionStatus;

public class TransactionsDatastore extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "transactions";
    private static final int DATABASE_VERSION = 1;


    public TransactionsDatastore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        @Language("SQL") String table = "CREATE TABLE transactions_history(" +
                "transactions_id TEXT PRIMARY KEY NOT NULL," +
                "transactions_start LONG NOT NULL," +
                "transactions_end LONG NOT NULL," +
                "transactions_result TEXT NOT NULL," +
                "transactions_amount DOUBLE NOT NULL" +
                ")";
        db.execSQL(table);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


    public boolean doesIDExist(UUID id) {
        @Language("SQL") String sqlStatement = "SELECT transactions_id FROM transactions_history WHERE transactions_id = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {id.toString()};
        Cursor c = db.rawQuery(sqlStatement,args);
        boolean exists = (c.getCount()>0);
        c.close();
        db.close();
        return exists;
    }

    public void addTransactionToDB(TransactionsData data) {
        data.updateEndEpochTime();
        if (!doesIDExist(data.getTransactionsID())) {
            if (data.getStatus().equals(TerminalTransactionStatus.PENDING)) {
                DebugLogger.log("Cannot create transaction entry to db ("+data.getTransactionsID()+")! Because the status is pending!");
                return;
            }
            DebugLogger.log("Creating new transaction entry to db ("+data.getTransactionsID()+")-"+data.getStatus().name());
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put("transactions_id",data.getTransactionsID().toString());
            v.put("transactions_start",data.getStartEpochTime());
            v.put("transactions_end",data.getEndEpochTime());
            v.put("transactions_result",data.getStatus().name());
            v.put("transactions_amount",data.getAmount());
            db.insert("transactions_history",null, v);
            db.close();
        }
    }

    public void removeTransactionFromDB(UUID id) {
        if (!this.doesIDExist(id)) return;
        DebugLogger.log("Removing transaction entry from db ("+id+")");
        SQLiteDatabase db = this.getWritableDatabase();
        String whereCause = "transactions_id=?";
        String[] args = {id.toString()};
        db.delete("transactions_history",whereCause,args);
        db.close();
    }

    public void wipeTransactions() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("transactions_history", null, null);
        db.close();
    }

    @SuppressLint("Range") public ReadOnlyTransaction getTransaction(UUID id) {
        SQLiteDatabase db = this.getReadableDatabase();
        @Language("SQL") String query = "SELECT * FROM transactions_history WHERE transactions_id = ?";
        String[] selectionArgs = {id.toString()};
        Cursor cursor = db.rawQuery(query,selectionArgs);
        ReadOnlyTransaction transaction = null;
        if (cursor.moveToFirst()) {
            UUID uid = UUID.fromString(cursor.getString(cursor.getColumnIndex("transactions_id")));
            long startTime = cursor.getLong(cursor.getColumnIndex("transactions_start"));
            long endTime = cursor.getLong(cursor.getColumnIndex("transactions_end"));
            TerminalTransactionStatus status = TerminalTransactionStatus.parse(cursor.getString(cursor.getColumnIndex("transactions_result")));
            double amount = cursor.getDouble(cursor.getColumnIndex("transactions_amount"));
            transaction = new ReadOnlyTransaction(uid,startTime,endTime,status,amount);
        }
        cursor.close();
        db.close();
        return transaction;
    }

    @SuppressLint("Range") public List<ReadOnlyTransaction> getTransactions() {
        List<ReadOnlyTransaction> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        @Language("SQL") String query = "SELECT * FROM transactions_history";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndex("transactions_id")));
                long startTime = cursor.getLong(cursor.getColumnIndex("transactions_start"));
                long endTime = cursor.getLong(cursor.getColumnIndex("transactions_end"));
                TerminalTransactionStatus status = TerminalTransactionStatus.parse(cursor.getString(cursor.getColumnIndex("transactions_result")));
                double amount = cursor.getDouble(cursor.getColumnIndex("transactions_amount"));
                data.add(new ReadOnlyTransaction(id,startTime,endTime,status,amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }
}
