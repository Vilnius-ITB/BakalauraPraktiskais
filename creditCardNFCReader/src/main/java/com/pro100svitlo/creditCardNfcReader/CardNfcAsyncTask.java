package com.pro100svitlo.creditCardNfcReader;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;

import com.pro100svitlo.creditCardNfcReader.enums.CardPaymentType;
import com.pro100svitlo.creditCardNfcReader.enums.EmvCardScheme;
import com.pro100svitlo.creditCardNfcReader.model.EmvCard;
import com.pro100svitlo.creditCardNfcReader.parser.EmvParser;
import com.pro100svitlo.creditCardNfcReader.utils.EncryptionUtils;
import com.pro100svitlo.creditCardNfcReader.utils.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import lombok.Getter;

/**
 * Created by pro100svitlo on 21.03.16.
 */
public class CardNfcAsyncTask extends AsyncTask<Void, Void, Object> {

    public static class Builder {
        private Tag mTag;
        private CardNfcInterface mInterface;
        private boolean mFromStart;

        private SecretKey encryptionKey;

        public Builder(CardNfcInterface i, Intent intent, SecretKey key, boolean fromCreate) {
            this.mInterface = i;
            this.mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            this.mFromStart = fromCreate;
            encryptionKey = key;
        }

        public CardNfcAsyncTask build() {
            return new CardNfcAsyncTask(this);
        }
    }

    public interface CardNfcInterface {
        void startNfcReadCard();

        void cardIsReadyToRead();

        void doNotMoveCardSoFast();

        void unknownEmvCard();

        void cardWithLockedNfc();

        void finishNfcReadCard();
    }

    public final static String CARD_UNKNOWN = EmvCardScheme.ANY_CARD.toString();
    public final static String CARD_VISA = EmvCardScheme.ANY_CARD.toString();

    private final static String NFC_A_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcA, android.nfc.tech.NfcA, android.nfc.tech.MifareClassic, android.nfc.tech.NdefFormatable]";
    private final static String NFC_G_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcA]";
    private final static String NFC_B_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcB]";
    private final static String NFC_F_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcF]";
    private final static String NFC_V_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcV]";
    private final String UNKNOWN_CARD_MESS =
            "===========================================================================\n\n" +
                    "Hi! This library is not familiar with your credit card. \n " +
                    "Please, write me an email with information of your bank: \n" +
                    "country, bank name, card type, etc) and i will try to do my best, \n" +
                    "to add your bank as a known one into this lib. \n" +
                    "Great thanks for using and reporting!!! \n" +
                    "Here is my email: pro100svitlo@gmail.com. \n\n" +
                    "===========================================================================";


    private static final Logger LOGGER = LoggerFactory.getLogger(CardNfcAsyncTask.class);

    private Provider mProvider = new Provider();
    private boolean mException;
    //private EmvCard mCard;
    private EmvParser mParser;
    private CardNfcInterface mInterface;
    private Tag mTag;

    private static SecretKey encryptionKey;

    private String encryptionCardData;

    public String getEncryptionCardData() {
        //Since this does create a new string so the object does not get modified
        return ""+this.encryptionCardData;
    }

    public EmvParser getEmvParser() {
        return this.mParser;
    }

    private CardNfcAsyncTask(Builder b) {
        mTag = b.mTag;
        encryptionKey = b.encryptionKey;
        if (mTag != null) {
            mInterface = b.mInterface;
            try {
                execute();
            } catch (NullPointerException e) {
                if (!b.mFromStart) {
                    mInterface.unknownEmvCard();
                }
                clearAll();
                e.printStackTrace();
            }
        }
    }

    public void setEncryptionKey(SecretKey key) {
        encryptionKey = key;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mInterface.startNfcReadCard();
    }

    @Override
    protected Object doInBackground(final Void... params) {
        Object result = null;
        try {
            doInBackground();
        } catch (Exception e) {
            result = e;
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(final Object result) {
        if (!mException) {
            EmvCard mCard = null;
            try {
                mCard = EncryptionUtils.decrypt(this.encryptionCardData,encryptionKey, EmvCard.class);
                if (mCard != null) {
                    if (StringUtils.isNotBlank(mCard.getCardNumber())) {
                        mInterface.cardIsReadyToRead();
                    } else if (mCard.isNfcLocked()) {
                        mInterface.cardWithLockedNfc();
                    }
                } else {
                    mInterface.unknownEmvCard();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            mInterface.doNotMoveCardSoFast();
        }
        mInterface.finishNfcReadCard();
        clearAll();
    }

    private void doInBackground() {
        IsoDep mIsoDep = IsoDep.get(mTag);
        if (mIsoDep == null) {
            mInterface.doNotMoveCardSoFast();
            return;
        }
        mException = false;
        try {
            // Open connection
            mIsoDep.connect();
            mProvider.setmTagCom(mIsoDep);
            this.mParser = new EmvParser(mProvider, true);
            this.encryptionCardData = this.mParser.readEmvCard(encryptionKey);
        } catch (Exception e) {
            mException = true;
        } finally {
            IOUtils.closeQuietly(mIsoDep);
        }
    }

    private void clearAll() {
        mInterface = null;
        mProvider = null;
        encryptionCardData = null;
        mTag = null;
    }
}
