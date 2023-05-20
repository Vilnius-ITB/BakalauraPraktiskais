package io.github.VilniusITB.BakalauraPraktiskais.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pro100svitlo.creditCardNfcReader.enums.CardPaymentType;


import io.github.VilniusITB.BakalauraPraktiskais.R;
import io.github.VilniusITB.BakalauraPraktiskais.TerminalApp;

public class PaymentRequestDialog extends AbstractDialog {
    private AudioManager audioManager;
    private int originalVolume;
    private TerminalApp app;

    /**
     * Constructs a new PaymentRequestDialog with the given TerminalApp instance.
     *
     * @param app the TerminalApp instance used to create the dialog
     */

    public PaymentRequestDialog(TerminalApp app) {
        super(app,R.layout.payment_request,false);
        this.app = app;
    }

    /**
     Checks if the payment request dialog is currently being displayed on the screen.
     @return true if the payment request dialog is showing, false otherwise.
     */
    public boolean isShowing() {
        return this.getAlertDialog().isShowing();
    }

    /**

     Shows the payment request dialog and sets up the necessary UI components.
     Enables the NFC dispatcher and sets the payment image and status text.
     Displays the last 4 digits of the card and sets the visibility of the card info to GONE.
     Sets the cancel button to visible.
     */

    public void showDialog() {
        this.getAlertDialog().show();
        this.getAlertDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE); //Disables screenshot and disabled or make the screen-share blank when the app is running!
        this.app.enableNFCDispatcher();
        this.getView().findViewById(R.id.cancel_action_button).setOnClickListener(v -> this.app.cancelPreCardScan());
        ImageView i = this.getView().findViewById(R.id.paymentImgBox);
        TextView t = this.getView().findViewById(R.id.paymentStatusText);
        TextView p = this.getView().findViewById(R.id.cardInfo);
        i.setImageResource(R.drawable.nfc);
        t.setText("Please scan your card!");
        p.setText("XXXX-XXXX-XXXX-XXXX");
        p.setVisibility(View.GONE);
        this.setPreCancelButton(true);
    }

    /**

     Hides the cancel button in the payment request dialog, updates the card information to "XXXX-XXXX-XXXX-XXXX" and sets the pre-cancel button state to false.
     Does nothing if the dialog is not showing.
     */

    public void hideCancelButton() {
        if (!this.getAlertDialog().isShowing()) return;
        TextView p = this.getView().findViewById(R.id.cardInfo);
        p.setText("XXXX-XXXX-XXXX-XXXX");
        p.setVisibility(View.GONE);
        this.setPreCancelButton(false);
    }

    /**

     Hides the payment request dialog by dismissing the underlying AlertDialog.
     */

    public void hideDialog() {
        this.getAlertDialog().dismiss();
    }

    /**

     Sets the status message displayed on the PaymentRequestDialog.
     @param message The message to be displayed.
     */

    public void setStatusMessage(String message) {
        if (!this.getAlertDialog().isShowing()) return;
        TextView t = this.getView().findViewById(R.id.paymentStatusText);
        t.setText(message);
    }

    /**
     * Resets the image in the payment image box to the default NFC image.
     * If the payment request dialog is not currently showing, the method returns without doing anything.
     */

    public void resetImageBox() {
        if (!this.getAlertDialog().isShowing()) return;
        ImageView i = this.getView().findViewById(R.id.paymentImgBox);
        i.setImageResource(R.drawable.nfc);
    }

    /**

     Displays the card information in the payment request dialog.
     @param cardinfo the card information to be displayed
     */

    public void displayCardInfo(String cardinfo) {
        if (!this.getAlertDialog().isShowing()) return;
        TextView t = this.getView().findViewById(R.id.cardInfo);
        t.setVisibility(View.VISIBLE);
        String a = cardinfo.substring(cardinfo.length() - 4);
        t.setText("XXXX-XXXX-XXXX-" + a);
    }

    /**

     Sets the payment processor brand image based on the provided CardPaymentType.
     @param type the CardPaymentType representing the brand of the payment processor
     */

    public void setPaymentProcessorBrand(CardPaymentType type) {
        if (!this.getAlertDialog().isShowing()) return;
        ImageView i = this.getView().findViewById(R.id.paymentImgBox);
        ImageView b = this.getActivity().findViewById(R.id.bannerImage);
        if (type.equals(CardPaymentType.VISA)) {
            i.setImageResource(R.drawable.visa2);
            b.setImageResource(R.drawable.visa2);
        }
        else if (type.equals(CardPaymentType.MASTER_CARD)) {
            i.setImageResource(R.drawable.mastercard2);
            b.setImageResource(R.drawable.mastercard2);
        }
        else {
            i.setImageResource(R.drawable.ucard);
            b.setImageResource(R.drawable.ucard);
        }
    }

    /**

     Displays an error message on the payment request dialog if the user scans their card too fast.
     Shows an error image and updates the payment status text accordingly. Also sets the visibility of the cancel button to true.
     Does nothing if the dialog is not currently showing.
     */

    public void displayTooFastError() {
        if (!this.getAlertDialog().isShowing()) return;
        Button b = this.getView().findViewById(R.id.cancel_action_button);
        ImageView i = this.getView().findViewById(R.id.paymentImgBox);
        TextView t = this.getView().findViewById(R.id.paymentStatusText);
        i.setImageResource(R.drawable.error);
        t.setText("Too Fast! Try Again!");
        this.setPreCancelButton(true);
    }

    /**

     Displays an error message indicating that an unknown card was detected.
     If the dialog is not currently showing, this method does nothing.
     Sets the image in the payment image box to an "invalid card" image, sets the payment status text to "Invalid Card",
     and sets the pre-cancel button to enabled.
     */

    public void displayUnknownCard() {
        if (!this.getAlertDialog().isShowing()) return;
        ImageView i = this.getView().findViewById(R.id.paymentImgBox);
        TextView t = this.getView().findViewById(R.id.paymentStatusText);
        i.setImageResource(R.drawable.invcard);
        t.setText("Invalid Card");
        this.setPreCancelButton(true);
    }

    /**

     Displays an error message when a payment card is expired.
     If the dialog is not showing, the method returns without doing anything.
     The payment image box and the banner image are both set to the invalid card image,
     and the payment status text is set to "Expired Payment Card".
     The pre-cancel button is set to false.
     */

    public void displayCardExp() {
        if (!this.getAlertDialog().isShowing()) return;
        ImageView i = this.getView().findViewById(R.id.paymentImgBox);
        ImageView b = this.getActivity().findViewById(R.id.bannerImage);
        TextView t = this.getView().findViewById(R.id.paymentStatusText);
        i.setImageResource(R.drawable.invcard);
        b.setImageResource(R.drawable.invcard);
        t.setText("Expired Payment Card");
        this.setPreCancelButton(false);
    }

    /**

     Plays a beep sound if the dialog is currently showing.
     The sound is played through the device's AudioManager using a MediaPlayer instance
     with a preset beep sound file.
     The AudioManager volume is adjusted to 80% of the maximum volume before playing the sound,
     and restored to the original volume after the sound is completed.
     */

    public void playBeep() {
        if (!this.getAlertDialog().isShowing()) return;
        MediaPlayer m = MediaPlayer.create(this.getActivity(),R.raw.beep);
        m.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build());
        m.setOnPreparedListener(mp -> {
            audioManager = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int targetVolume = (int) (maxVolume * 0.8);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0);
            m.start();
        });
        m.setOnCompletionListener(mp -> {
            if (audioManager != null) audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
        });
        float vol = 0.3F;
        m.setVolume(vol,vol);
        m.start();
    }

    /**

     Sets the state of the "pre-cancel" button in the payment dialog.
     If the dialog is not showing, this method does nothing.
     @param state a boolean value indicating whether the button should be enabled and visible (true) or disabled and invisible (false)
     */

    private void setPreCancelButton(boolean state) {
        if (!this.getAlertDialog().isShowing()) return;
        Button b = this.getView().findViewById(R.id.cancel_action_button);
        if (state&&!b.isEnabled()) {
            b.setEnabled(true);
            b.setVisibility(View.VISIBLE);
        }
        else if (!state&&b.isEnabled()) {
            b.setEnabled(false);
            b.setVisibility(View.GONE);
        }
    }

}
