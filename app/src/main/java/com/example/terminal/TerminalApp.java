package com.example.terminal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;

public class TerminalApp extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @NotNull protected static TerminalApp terminalApp;

    protected TextView amountDisplay;
    protected StringBuilder amountBuilder = new StringBuilder();

    protected Logger logger = Logger.getLogger("App Log");

    protected static TerminalInputState inputState = TerminalInputState.AMOUNT;

    @Override
    /**
     * The main app launcher
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (inputState==null) inputState = TerminalInputState.AMOUNT;
        terminalApp = this;
        setContentView(R.layout.activity_main);
        amountDisplay = findViewById(R.id.textView);

        this.findViewById(R.id.button1).setOnClickListener(createNumberClickListener("1"));
        this.findViewById(R.id.button2).setOnClickListener(createNumberClickListener("2"));
        this.findViewById(R.id.button3).setOnClickListener(createNumberClickListener("3"));
        this.findViewById(R.id.button4).setOnClickListener(createNumberClickListener("4"));
        this.findViewById(R.id.button5).setOnClickListener(createNumberClickListener("5"));
        this.findViewById(R.id.button6).setOnClickListener(createNumberClickListener("6"));
        this.findViewById(R.id.button7).setOnClickListener(createNumberClickListener("7"));
        this.findViewById(R.id.button8).setOnClickListener(createNumberClickListener("8"));
        this.findViewById(R.id.button9).setOnClickListener(createNumberClickListener("9"));
        this.findViewById(R.id.button0).setOnClickListener(createNumberClickListener("0"));

        Button buttonBackspace = findViewById(R.id.buttonBackspace);
        Button buttonDelete = findViewById(R.id.buttonDelete);
        Button buttonConfirm = findViewById(R.id.buttonConfirm);

        final int MAX_PIN_TRIES = 4;
        String pin = "1234";

        buttonConfirm.setOnClickListener(new AppClickListener(MAX_PIN_TRIES,pin));
        buttonDelete.setOnClickListener(v ->{
            if (amountBuilder.length() > 0) {
                amountBuilder.delete(0, amountBuilder.length());
                updateAmountDisplay();
            }
        });
        buttonBackspace.setOnClickListener(v ->{
            if (amountBuilder.length() > 0) {
                amountBuilder.deleteCharAt(amountBuilder.length() - 1);
                updateAmountDisplay();
            }
        });
    }

    /**
     * This create a listener for the number pat with in the app
     * @param number the string value of the number button that listener needs to be created on
     * @return click listener
     */
    private View.OnClickListener createNumberClickListener(final String number) {
        return v ->{
            if(inputState.isButtonDisabled()) return;
            if (amountBuilder.length() < 10) {
                amountBuilder.append(number);
                updateAmountDisplay();
            }
        };
    }

    /**
     * This method updates main display text message
     */
    protected void updateAmountDisplay() {
        logger.info(getDebugTerminalState());
        String amountString = amountBuilder.toString();
        if (inputState.equals(TerminalInputState.PIN)) {
            String maskedPIN = "";
            for (int i = 0; i < amountString.length(); i++) maskedPIN += "*";
            amountDisplay.setText(maskedPIN);
            return;
        }
        if (amountString.isEmpty()) {
            logger.info("amount Display was reporting to be empty resenting");
            amountDisplay.setText("$0.00");
        } else {
            double amount = Double.parseDouble(amountString) / 100.0;
            DecimalFormat decimalFormat = new DecimalFormat("$###0.00");
            this.logger.info(String.valueOf(amount));
            this.logger.info(decimalFormat.format(amount));
            amountDisplay.setText(decimalFormat.format(amount));
        }
    }

    /**
     * Resets everything back to default as like the app was restarted
     */
    protected void reset() {
        amountBuilder = new StringBuilder();
        inputState = TerminalInputState.AMOUNT;
        this.updateAmountDisplay();
    }

    /**
     * DEBUG: Just for debugging string text for getting current app state
     */
    @NotNull String getDebugTerminalState() {
        return "Debug State: "+inputState.name();
    }

}
