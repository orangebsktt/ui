package com.hackathon.fastshop.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hackathon.fastshop.ApiCaller;
import com.hackathon.fastshop.IntentData;
import com.hackathon.fastshop.ProductData;
import com.hackathon.fastshop.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.Hashtable;

public class PaymentActivity extends AppCompatActivity {

    public static final String ERROR_DETECTED = "No NFC Tag Detected";
    public static final String WRITE_SUCCESS = "Text Written Successfully!";
    public static final String WRITE_ERROR = "Error during Writing, Try Again!";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] writingTagFilters;
    boolean writeMode;
    Tag myTag;
    Context context;
    TextView nfc_qr_contents;
    Dictionary<String, IntentData> intents = new Hashtable<>();
    Dictionary<String, ProductData> products = new Hashtable<>();

    Button qrButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        generateProducts();
        nfc_qr_contents = findViewById(R.id.nfc_qr_contents);
        //activate_button = findViewById(R.id.activate_button);

        context = this;
        ApiCaller a = new ApiCaller();
        a.getData(context, 3);
        /*activate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myTag ==null) {
                        Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show();
                    } else {
                        write(edit_message.getText().toString(), myTag);
                        Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show();
                    }

                } catch (IOException e) {
                    Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show(); e.printStackTrace();
                } catch (FormatException e) {
                    Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show(); e.printStackTrace();
                }
            }
        });*/
        readData();

        qrButton = findViewById(R.id.qrButton);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(PaymentActivity.this);
                integrator.setOrientationLocked(true);
                integrator.setPrompt("Scan a QR Code");
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.initiateScan();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result == null || result.getContents() == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        //nfc_contents.setText(result.getContents());
        Toast.makeText(context, "Qr Content :" + result.getContents(), Toast.LENGTH_LONG).show();//display the response on screen
    }

    public String readData() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device does not support NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilters = new IntentFilter[] {tagDetected};
        return "";
    }

    private String readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            return buildTagViews (msgs);
        }

        return "";
    }

    private String buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return "";

        String text = "";
        //String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; //Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; //Get the Language Code, e.g. "en"
        //String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            //Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        nfc_qr_contents.setText("NFC Content: " + text);
        return text;
    }

    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord (text) };

        // get techlist for debug error Ndef...
        String[] techList = tag.getTechList();


        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        NdefFormatable ndefFormatable = NdefFormatable.get(tag);
        // Enable I/O ndef.connect();

        if(ndefFormatable != null) {
            ndefFormatable.connect();

            // Write the message
            ndefFormatable.formatReadOnly(message); //writeNdefMessage(message);
            // Close the connection

            //old
            //Ndef ndef = Ndef.get(tag);
            //ndef.writeNdefMessage(message);

            ndefFormatable.close();
        } else {
            Ndef ndef = Ndef.get(tag);

            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
                readData();
            } else {
                Toast.makeText(context, "NdefFormatable and Ndef is null...", Toast.LENGTH_LONG).show();
            }

        }

    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);
        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String data = readFromIntent(intent);
        intents.put(bytesToHexString(myTag.getId()), new IntentData(bytesToHexString(myTag.getId()), data));
        Toast.makeText(context, "RFID Content : " + data, Toast.LENGTH_LONG).show();//display the response on screen
    }

    @Override
    public void onPause() {
        super.onPause();
        WriteMode0ff();
    }

    @Override
    public void onResume() {
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        if (nfcAdapter != null) {
            writeMode = true;
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, writingTagFilters, null);
        }
    }

    private void WriteMode0ff(){
        if (nfcAdapter != null) {
            writeMode = false;
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }

    private void generateProducts(){
        products.put("0x39f52c7d", new ProductData("0x39f52c7d", "Bere", BigDecimal.valueOf(75)));
        products.put("0x39e8337d", new ProductData("0x39e8337d", "Canta", BigDecimal.valueOf(125)));
        products.put("0x49ccd87d", new ProductData("0x49ccd87d", "Bot", BigDecimal.valueOf(2775)));
        products.put("0xa7b43e65", new ProductData("0xa7b43e65", "Atki", BigDecimal.valueOf(125)));
        products.put("0xc712b865", new ProductData("0xc712b865", "Tshirt", BigDecimal.valueOf(250)));
    }
}