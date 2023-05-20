package io.github.VilniusITB.BakalauraPraktiskais.enums;

public enum TerminalInputState {
    AMOUNT(false),
    PIN(false),
    NFC(true),
    NFC_DEVICE_ERROR(true),
    PAYMENT_SUCCESSFUL(true),
    PAYMENT_FAILURE(true),
    THANK_YOU(true),
    DEBUG_MENU(true);
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
