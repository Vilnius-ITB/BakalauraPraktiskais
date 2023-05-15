package io.github.VilniusITB.BakalauraPraktiskais;

import android.os.Handler;
import android.view.View;

import com.pro100svitlo.creditCardNfcReader.enums.CardPaymentType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppClickListener implements View.OnClickListener {

    int maxPinTries;
    final String pin;
    int pinTries;

    /**
     * Constructs a new AppClickListener with the specified maximum number of PIN tries
     * and PIN value.
     *
     * @param maxPinTries the maximum number of PIN tries allowed before locking out the user
     * @param pin the PIN value for the user
     */

    protected AppClickListener(int maxPinTries, String pin) {
        this.maxPinTries = maxPinTries;
        this.pin = pin;
        this.pinTries = 0;
    }

    /**
     * Handles the click event for this AppClickListener by calling the execute method
     * with null parameters.
     *
     * @param v the view that was clicked (ignored in this implementation)
     */
    @Override
    public void onClick(View v) {
        this.execute(null,null);
    }

    /**
     * Executes the current app state that can be entering amount, entering a pin and even handling the NFC data income it has a ability to check if the card is
     * expired or not if it expired then prevent anything else happening and reset the app from the start.
     *
     * @param ctype the type of payment card used (Only being used when TerminalApp input state is TerminalInputState is NFC)
     * @param expDate the expiration date of the payment card (Only being used when TerminalApp input state is TerminalInputState is NFC)
     */

    public void execute(CardPaymentType ctype, String expDate) {
        TerminalApp app = TerminalApp.terminalApp;
        app.logger.info(app.amountBuilder.toString());
        app.logger.info(app.getDebugTerminalState());
        if (TerminalApp.inputState.equals(TerminalInputState.THANK_YOU)) return;
        switch (TerminalApp.inputState) {
            case NFC:
                if (isCardValid(expDate)) {
                    double amount = Double.parseDouble(app.amountBuilder.toString())  / 100.0;
                    if (!this.prePinCheck(ctype,amount)) {
                        app.inputState = TerminalInputState.PAYMENT_SUCCESSFUL;
                        app.amountDisplay.setText("Payment successful");
                        new Handler().postDelayed(() -> {
                            app.amountDisplay.setText("Thank you");
                            app.inputState = TerminalInputState.THANK_YOU;
                            new Handler().postDelayed(app::reset,3000);
                        }, 2000); // Delay 2 seconds before showing "Thank you"
                    } else {
                        if (this.pinTries!=0) this.pinTries = 0;
                        app.inputState = TerminalInputState.PIN;
                        app.amountBuilder.setLength(0); // Clear the amount builder
                        app.updateAmountDisplay(); // Update TextView to show empty string
                        app.amountDisplay.setText("Please enter your PIN");
                    }
                } else {
                    app.amountDisplay.setText("Payment card expired!");
                    app.inputState = TerminalInputState.PAYMENT_FAILURE;
                    new Handler().postDelayed(app::reset,3000);
                }

                break;
            case AMOUNT:
                if (this.pinTries!=0) this.pinTries = 0;
                TerminalApp.inputState = TerminalInputState.NFC;
                app.logger.info("Display payment dialog box to client!");
                TerminalApp.terminalApp.paymentRequestDialog.showDialog();
                break;
            case PIN:
                if (maxPinTries-pinTries > 0) {
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
                        if (maxPinTries-pinTries==0) {
                            this.sendPinFailure();
                        } else app.amountDisplay.setText("Please enter your PIN again (" + (maxPinTries-pinTries) + " tries left)");
                    }
                } else this.sendPinFailure();

                break;
            default:
                break;
        }
    }

    private void sendPinFailure() {
        TerminalApp app = TerminalApp.terminalApp;
        this.pinTries = 0;
        app.amountDisplay.setText("Payment cancelled");
        app.inputState = TerminalInputState.PAYMENT_FAILURE;
        new Handler().postDelayed(app::reset,2000);
    }

    /**

     This method checks if the given card payment type and amount meet the required pre-PIN check criteria.
     @param type The type of card payment being made.
     @param amount The amount of the payment being made.
     @return true if the payment type and amount meet the pre-PIN check criteria, false otherwise.
     */

    private boolean prePinCheck(CardPaymentType type, double amount) {
        if (type.equals(CardPaymentType.MASTER_CARD)&&amount < 50.00) return false;
        if (type.equals(CardPaymentType.VISA)&&amount < 60.00) return false;
        if (type.equals(CardPaymentType.AMERICAN_EXPRESS)&&amount < 30.00) return false;
        if (type.equals(CardPaymentType.DISCOVER)&&amount < 30.00) return false;
        if (type.equals(CardPaymentType.UNKNOWN)&&amount < 1.00) return false;
        return true;
    }

    /**
     * Determines if a card is still valid based on its expiry date.
     *
     * @param exp a String representing the card's expiry date in "MM/yy" format
     * @return a boolean value indicating whether the card is still valid or not
     * @throws RuntimeException if the expiry date is not in the correct format
     */

    public static boolean isCardValid(String exp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
        simpleDateFormat.setLenient(false);
        Date expiry = null;
        try {
            expiry = simpleDateFormat.parse(exp);
            boolean expired = expiry.before(new Date());
            return !expired;
        } catch (ParseException e) {throw new RuntimeException(e);}
    }
}
