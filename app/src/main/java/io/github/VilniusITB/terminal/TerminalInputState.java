package io.github.VilniusITB.terminal;
public enum TerminalInputState {
    AMOUNT(false),
    PIN(false),
    NFC(true),
    NFC_DEVICE_ERROR(true),
    PAYMENT_SUCCESSFUL(true),
    PAYMENT_FAILURE(true),
    CONFIRM_AMOUNT(false),
    THANK_YOU(true);
    private final boolean disableButtons;

    /**
     * This is used for when the buttons needs to be disabled from any user/human interaction
     * @return boolean value if the button is disabled or not
     */
    public boolean isButtonDisabled() {
        return disableButtons;
    }

    TerminalInputState(boolean disableButtons) {
        this.disableButtons = disableButtons;
    }
}
