package io.github.VilniusITB.BakalauraPraktiskais;

import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.enums.CardPaymentType;

public class mPOSNFCHandler implements CardNfcAsyncTask.CardNfcInterface {

    static boolean cardFinishedReading = false;
    static boolean cardTooFast = false;

    /**

     Resets the values of the cardTooFast and cardFinishedReading boolean flags to false.
     */

    protected void reset() {
        cardTooFast = false;
        cardFinishedReading = false;
    }

    /**

    Returns a boolean value indicating whether the payment card reading process has finished or not.
    */

    protected boolean isCardFinishedReading() {return cardFinishedReading;}

    /**

     Starts the NFC read card process if the card has not finished reading.
     Checks if the payment request dialog is currently showing.
     Resets the cardFinishedReading and cardTooFast flags.
     Sets the status message in the payment request dialog to "Scanning...".
     Resets the image box in the payment request dialog.
     Plays a beep sound in the payment request dialog.
     Hides the cancel button in the payment request dialog.
     */

    @Override
    public void startNfcReadCard() {
        if (cardFinishedReading) return;
        TerminalApp app = TerminalApp.terminalApp;
        if (!app.getPaymentRequestDialog().isShowing()) return;
        cardFinishedReading = false;
        cardTooFast = false;
        app.getPaymentRequestDialog().setStatusMessage("Scanning...");
        app.getPaymentRequestDialog().resetImageBox();
        app.getPaymentRequestDialog().playBeep();
        app.getPaymentRequestDialog().hideCancelButton();
    }

    /**

     Method to handle a situation where a card is ready to be read by the NFC reader.
     Sets the value of the boolean variable cardFinishedReading to true.
     Then, retrieves the instance of TerminalApp and checks if its PaymentRequestDialog is showing.
     If not, the method returns.
     Otherwise, retrieves the type, expiry date, and card number of the card payment from the CardNFCLibTask instance in the TerminalApp.
     Logs information about the card type using the logger.
     Sets the payment processor brand on the PaymentRequestDialog instance to the type of card payment.
     Displays the card information on the PaymentRequestDialog instance.
     Sets the status message on the PaymentRequestDialog instance to the name of the card payment type.
     Disables the NFC dispatcher in the TerminalApp instance and calls the progressPayment method on the TerminalApp instance with the card payment type and expiry date as parameters.
     */

    @Override
    public void cardIsReadyToRead() {
        cardFinishedReading = true;
        TerminalApp app = TerminalApp.terminalApp;
        if (!app.getPaymentRequestDialog().isShowing()) return;
        CardPaymentType type = app.getCardNFCLibTask().getCardPaymentType();
        String exp = app.getCardNFCLibTask().getCardExpireDate();
        String card = app.cardNfcAsyncTask.getCardNumber();
        app.logger.info("End of the NFC request! Card type: "+type.name());
        app.getPaymentRequestDialog().setPaymentProcessorBrand(type);
        app.getPaymentRequestDialog().displayCardInfo(card);
        app.getPaymentRequestDialog().setStatusMessage(type.getName());
        app.disableNFCDispatcher();
        app.progressPayment(type,exp);

    }

    /**

     Notifies the user that the card was removed too fast from the device, and sets the
     {@code cardTooFast} flag to true.
     If {@code cardFinishedReading} is already true, the method returns without taking any action.
     If the payment request dialog is not showing, the method also returns without taking any action.
     */

    @Override
    public void doNotMoveCardSoFast() {
        if (cardFinishedReading) return;
        TerminalApp app = TerminalApp.terminalApp;
        if (!app.getPaymentRequestDialog().isShowing()) return;
        app.logger.warning("Could not obtain data due to card was removed to fast from the device!");
        cardFinishedReading = false;
        cardTooFast = true;
    }

    /**

     Signals that an unknown EMV card was placed on the terminal.
     This method does nothing if the card has already been read or if the payment request dialog is not showing.
     If the previous call to doNotMoveCardSoFast() was made, the flag cardTooFast will be reset.
     */

    @Override
    public void unknownEmvCard() {
        if (cardFinishedReading) return;
        TerminalApp app = TerminalApp.terminalApp;
        if (!app.getPaymentRequestDialog().isShowing()) return;
        if (cardTooFast) cardTooFast = false;
        app.logger.severe("Unknown card was placed!");
    }

    /**

     This method is called when an NFC card with locked NFC is detected.
     If the payment request dialog is not currently showing, the method returns early.
     If the card has already finished reading, the method returns early.
     Otherwise, it sets the cardFinishedReading flag to false and performs necessary actions in the payment request dialog.
     @see TerminalApp
     */

    @Override
    public void cardWithLockedNfc() {
        if (cardFinishedReading) return;
        TerminalApp app = TerminalApp.terminalApp;
        if (!app.getPaymentRequestDialog().isShowing()) return;
        cardFinishedReading = false;
    }

    /**

     Finishes the NFC card reading process and displays the appropriate message depending on whether the
     reading was successful or not.
     */

    @Override
    public void finishNfcReadCard() {
        if (cardFinishedReading) return;
        TerminalApp app = TerminalApp.terminalApp;
        if (!app.getPaymentRequestDialog().isShowing()) return;
        if (!cardFinishedReading) {
            if (cardTooFast) {
                app.getPaymentRequestDialog().displayTooFastError();
                return;
            }
            app.getPaymentRequestDialog().displayUnknownCard();
        }
    }

}
