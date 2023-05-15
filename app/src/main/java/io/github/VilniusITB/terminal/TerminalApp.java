package io.github.VilniusITB.terminal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.enums.CardPaymentType;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.logging.Logger;

import io.github.VilniusITB.terminal.dialogs.PaymentRequestDialog;
import io.github.VilniusITB.terminal.dialogs.UnsupportedDeviceDialog;

public class TerminalApp extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @NotNull protected static TerminalApp terminalApp;

    protected TextView amountDisplay;
    protected ImageView bannerImage;
    protected StringBuilder amountBuilder = new StringBuilder();

    protected Logger logger = Logger.getLogger("App Log");

    protected static TerminalInputState inputState = TerminalInputState.AMOUNT;

    protected NfcAdapter nfcAdapter;
    protected CardNfcUtils cardNfcUtils;
    protected CardNfcAsyncTask cardNfcAsyncTask;
    private mPOSNFCHandler mPOSNFCHandler;

    private boolean intentFromCreate = false;

    //Dialogs
    protected PaymentRequestDialog paymentRequestDialog;
    private AppClickListener appClickListener;

    public CardNfcAsyncTask getCardNFCLibTask() {
        return this.cardNfcAsyncTask;
    }


    @Override
    /**

     Initializes the terminal application and sets up the UI components.
     Checks if NFC is supported and enabled, and displays an error message if it is not.
     Initializes the payment request dialog and the app click listener.
     Sets up the number buttons and the backspace and delete buttons to handle user input for the payment amount.
     @param savedInstanceState The saved instance state of the activity.
     @throws UnsupportedDeviceException If the device does not support NFC.
     @throws NFCNotEnabledException If NFC is not enabled on the device.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (inputState==null) inputState = TerminalInputState.AMOUNT;
        terminalApp = this;
        this.mPOSNFCHandler = new mPOSNFCHandler();
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE); //Disables screenshot and disabled or make the screen-share blank when the app is running!
        this.amountDisplay = this.findViewById(R.id.textView);
        this.bannerImage = this.findViewById(R.id.bannerImage);
        if (this.nfcAdapter==null) {
            inputState = TerminalInputState.NFC_DEVICE_ERROR;
            this.amountDisplay.setText("Invalid Device! (No NFC Found)");
            new UnsupportedDeviceDialog(this).displayDialog();
            return;
        }
        if (!this.nfcAdapter.isEnabled()) {
            inputState = TerminalInputState.NFC_DEVICE_ERROR;
            this.amountDisplay.setText("NFC is not enabled! Please turn it on!");
            return;
        }
        this.paymentRequestDialog = new PaymentRequestDialog(this);
        this.cardNfcUtils = new CardNfcUtils(this);
        this.intentFromCreate = true;
        onNewIntent(getIntent());


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

        final int MAX_PIN_TRIES = 3;
        String pin = "1234";
        this.appClickListener = new AppClickListener(MAX_PIN_TRIES,pin);
        buttonConfirm.setOnClickListener(this.appClickListener);
        buttonDelete.setOnClickListener(v ->{
            if(inputState.isButtonDisabled()) return;
            if (amountBuilder.length() > 0) {
                amountBuilder.delete(0, amountBuilder.length());
                updateAmountDisplay();
            }
        });
        buttonBackspace.setOnClickListener(v ->{
            if(inputState.isButtonDisabled()) return;
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
            amountDisplay.setText("€0.00");
        } else {
            double amount = Double.parseDouble(amountString) / 100.0;
            DecimalFormat decimalFormat = new DecimalFormat("€###0.00");
            this.logger.info(String.valueOf(amount));
            this.logger.info(decimalFormat.format(amount));
            amountDisplay.setText(decimalFormat.format(amount));
        }
    }

    /**
     * Resets everything back to default as like the app was restarted
     */
    protected void reset() {
        this.logger.info("Calling Reset...");
        if (this.nfcAdapter==null) {
            inputState = TerminalInputState.NFC_DEVICE_ERROR;
            amountDisplay.setText("Invalid Device! (No NFC Found)");
            return;
        }
        if (!this.nfcAdapter.isEnabled()) {
            inputState = TerminalInputState.NFC_DEVICE_ERROR;
            amountDisplay.setText("NFC is not enabled! Please turn it on!");
            return;
        }
        this.mPOSNFCHandler.reset();
        if (this.paymentRequestDialog.isShowing()) this.paymentRequestDialog.hideDialog();
        this.bannerImage.setImageResource(R.drawable.pay);
        this.amountBuilder = new StringBuilder();
        inputState = TerminalInputState.AMOUNT;
        this.updateAmountDisplay();
    }

    /**

     Enables the NFC dispatcher and logs the action to the logger.
     If the cardNfcUtils instance is not null, it calls its enableDispatch() method to enable NFC dispatching.
     */

    public void enableNFCDispatcher(){
        this.logger.info("[NFC] Enabling NFC Dispatcher");
        if (this.cardNfcUtils!=null) this.cardNfcUtils.enableDispatch();
    }

    /**

     Disables the NFC dispatcher to stop handling NFC intents.
     Also logs a message indicating that the NFC dispatcher is being disabled.
     If the cardNfcUtils object is not null, it calls the disableDispatch method on it.
     */

    public void disableNFCDispatcher(){
        this.logger.info("[NFC] Disabling NFC Dispatcher");
        if (this.cardNfcUtils!=null) this.cardNfcUtils.disableDispatch();
    }

    /**

     Called when the activity is resumed after being paused.
     It sets the intentFromCreate flag to false and enables NFC dispatcher only if paymentRequestDialog is showing
     and mPOSNFCHandler has not finished reading the card.
     */

    @Override
    protected void onResume() {
        super.onResume();
        this.intentFromCreate = false;
        if (this.paymentRequestDialog!=null) {
            if (this.paymentRequestDialog.isShowing()) {
                if (!this.mPOSNFCHandler.isCardFinishedReading())
                    this.enableNFCDispatcher();
            }
        }

    }

    /**

     Called when the activity is going into the background, typically because another activity is being resumed
     and this one is no longer visible.
     This method overrides the parent class method to handle the onPause lifecycle event of the activity.
     If the payment request dialog is showing and the NFC reading process is not finished, the NFC dispatcher
     is enabled to continue reading the card on resume.
     */

    @Override
    protected void onPause() {
        super.onPause();
        if (this.paymentRequestDialog!=null) {
            if (this.paymentRequestDialog.isShowing()) {
                if (!this.mPOSNFCHandler.isCardFinishedReading())
                    this.enableNFCDispatcher();
            }
        }
    }

    /**

     Returns the instance of PaymentRequestDialog for this TerminalApp.
     @return PaymentRequestDialog instance of this TerminalApp
     */

    public PaymentRequestDialog getPaymentRequestDialog() {
        return this.paymentRequestDialog;
    }

    /**

     Cancels the pre-card scan process by hiding the payment request dialog, setting the input state to payment failure,
     displaying a payment cancelled message on the amount display, and resetting the payment terminal after 2 seconds.
     This method is only applicable if the input state is NFC and the payment request dialog is showing.
     */

    public void cancelPreCardScan() {
        if (!inputState.equals(TerminalInputState.NFC)) return;
        if (!this.paymentRequestDialog.isShowing()) return;
        this.paymentRequestDialog.hideDialog();
        inputState = TerminalInputState.PAYMENT_FAILURE;
        this.amountDisplay.setText("Payment cancelled");
        new Handler().postDelayed(this::reset,2000);
    }

    /**

     This method is used to progress with the card payment when the input state is NFC and the payment request dialog is showing.
     If the card expiration is invalid, the payment request dialog displays an error message.
     After a delay of 2 seconds, the payment request dialog is hidden and the AppClickListener's execute method is called with the card payment type and expiration as arguments.
     @param type The type of card payment (debit or credit).
     @param exp The card expiration date as a string in MM/yy format.
     */

    void progressPayment(CardPaymentType type, String exp) {
        if (!inputState.equals(TerminalInputState.NFC)) return;
        if (!this.paymentRequestDialog.isShowing()) return;
        if (!AppClickListener.isCardValid(exp)) this.paymentRequestDialog.displayCardExp();

        new Handler().postDelayed(()->{
            if (this.paymentRequestDialog.isShowing()) this.paymentRequestDialog.hideDialog();
            this.appClickListener.execute(type,exp);
        },2000);
    }

    /**
     * DEBUG: Just for debugging string text for getting current app state
     */
    @NotNull String getDebugTerminalState() {
        return "Debug State: "+inputState.name();
    }

    /**

     This method is called when a new NFC intent is received by the activity.
     It creates a new instance of the CardNfcAsyncTask if the NFC adapter is enabled,
     and passes the instance of the mPOSNFCHandler, the intent received and the intentFromCreate variable to the constructor.
     @param intent The new NFC intent received by the activity.
     */

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (this.nfcAdapter!=null&&nfcAdapter.isEnabled()) {
            this.cardNfcAsyncTask = new CardNfcAsyncTask.Builder(this.mPOSNFCHandler,intent,this.intentFromCreate).build();
        }
    }

}
