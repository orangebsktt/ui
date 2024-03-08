package com.hackathon.fastshop.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hackathon.fastshop.dto.IntentDto;
import com.hackathon.fastshop.dto.ProductDto;
import com.hackathon.fastshop.R;

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
    Dictionary<String, IntentDto> intents = new Hashtable<>();
    Dictionary<String, ProductDto> products = new Hashtable<>();

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
        nfc_qr_contents = findViewById(R.id.nfc_qr_contents);

    }

}