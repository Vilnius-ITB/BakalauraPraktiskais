package com.example.terminal;

import android.os.Handler;
import android.view.View;
public class AppClickListener implements View.OnClickListener {

    int maxPinTries;
    final String pin;
    int pinTries;

    protected AppClickListener(int maxPinTries, String pin) {
        this.maxPinTries = maxPinTries;
        this.pin = pin;
        this.pinTries = 0;
    }

    /**
     * This method aka onClick event method handler.
     */
    @Override
    public void onClick(View v) {
        TerminalApp app = TerminalApp.terminalApp;
        app.logger.info(app.amountBuilder.toString());
        app.logger.info(app.getDebugTerminalState());
        if (TerminalApp.inputState.equals(TerminalInputState.THANK_YOU)) return;
        switch (TerminalApp.inputState) {
            case AMOUNT:
                TerminalApp.inputState = TerminalInputState.NFC;
                // Simulate NFC card read
                boolean isCardRead = true; // Card is always read
                if (!isCardRead) {
                    app.amountDisplay.setText("Something went wrong");
                    new Handler().postDelayed(app::reset,2000);
                    return;
                }
                double amount = Double.parseDouble(app.amountBuilder.toString())  / 100.0;
                if (amount < 50.00) {
                    app.inputState = TerminalInputState.PAYMENT_SUCCESSFUL;
                    app.amountDisplay.setText("Payment successful");
                    new Handler().postDelayed(() -> {
                        app.amountDisplay.setText("Thank you");
                        app.inputState = TerminalInputState.THANK_YOU;
                        new Handler().postDelayed(app::reset,3000);
                    }, 2000); // Delay 2 seconds before showing "Thank you"
                } else {
                    app.inputState = TerminalInputState.PIN;
                    app.amountBuilder.setLength(0); // Clear the amount builder
                    app.updateAmountDisplay(); // Update TextView to show empty string
                    app.amountDisplay.setText("Please enter your PIN");
                }
                break;
            case PIN:
                if (maxPinTries-pinTries > 1) {
                    if (app.amountBuilder.toString().equals(pin)) {
                        app.inputState = TerminalInputState.PAYMENT_SUCCESSFUL;
                        app.amountDisplay.setText("Payment successful");
                        new Handler().postDelayed(() -> {
                            app.amountDisplay.setText("Thank you");
                            app.inputState = TerminalInputState.THANK_YOU;
                            new Handler().postDelayed(app::reset,1000);

                        }, 2000); // Delay 2 seconds before showing "Thank you"
                    } else {
                        pinTries++;
                        app.amountBuilder.setLength(0); // Clear the amount builder
                        app.amountDisplay.setText("Please enter your PIN again (" + (maxPinTries-pinTries) + " tries left)");
                    }
                } else {
                    this.pinTries = 0;
                    app.amountDisplay.setText("Payment cancelled");
                    app.inputState = TerminalInputState.PAYMENT_FAILURE;
                    new Handler().postDelayed(app::reset,2000);
                }
                break;
            default:
                break;
        }
    }
}
