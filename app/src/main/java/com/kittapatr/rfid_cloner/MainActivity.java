package com.kittapatr.rfid_cloner;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.kittapatr.rfid_cloner.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // NFC adapter and intention wait for NFC
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;

    // Application and console controller
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    final static String TAG = "NFC detected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Application initialization
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Set a NFC adapter as a current NFC device
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Restriction allowance for device that has no NFC adapter
        if (nfcAdapter == null) {
            Toast.makeText(this, "Your device has no NFC adapter.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        // Create a PendingIntent object for populating
        // it with the details when the tag is scanned.
        pendingIntent = PendingIntent.
                getActivity(this, 0, new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        assert nfcAdapter != null;
        // Focus on pendingIntent when a user is using application
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    protected void onPause() {
        super.onPause();

        // Stop listening when quit the application
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // No options in action bar
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Navigate between activity_main.xml to content_main.xml
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // Add an information into a console
        TextView textView = this.findViewById(R.id.textView2);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // Current date and time
        textView.append("Time: " + formatter.format(new Date(System.currentTimeMillis())) + "\n" + resolveIntent(intent) + "\n\n"); // RFID data
    }

    private String resolveIntent(Intent intent) {
        String action = intent.getAction();
        // Detect an intent that is as a list provided below
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            // Get tag data
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.v(TAG, action);
            assert tag != null;
            return detectTagData(tag);
        }
        // For the other intents, return an empty string
        return "";
    }

    //For information gathering in RFID card
    private String detectTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();

        // Extract byte UID in RFID card
        byte[] id = tag.getId();

        // Hexadecimal id
        String hexID = toHex(id);
        sb.append("ID (hex): ").append(hexID).append('\n');

        // Reversed hexadecimal id
        String reversedHexID = toReversedHex(id);
        sb.append("ID (reversed hex): ").append(reversedHexID).append('\n');

        // Decimal id
        long decID = toDec(id);
        sb.append("ID (dec): ").append(decID).append('\n');

        // Reversed Decimal id
        long reversedDecID = toReversedDec(id);
        sb.append("ID (reversed dec): ").append(reversedDecID).append('\n');

        // Technology detection, in this application, detects only Mifare technology.
        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");

        String[] techList = tag.getTechList();
        for (String tech : techList) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        boolean classic = false;
        String type = "Unknown";
        int size = 0, sector = 0, block = 0;
        for (String tech : techList) {

            // Mifare classic data can extract overall of the card.
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');

                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);
                    classic = true;
                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    size = mifareTag.getSize();
                    sb.append(size + " bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sector = mifareTag.getSectorCount();
                    sb.append(sector);
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    block = mifareTag.getBlockCount();
                    sb.append(block);
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }

            // Mifare Ultralight can only extract in card type.
            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }
        // deprecated function
//        String json;
//        if (classic) {
//            json = "{\"ID (hex)\": \"" + hexID + "\","
//                    + "\"ID (reversed hex)\": \"" + reversedHexID + "\","
//                    + "\"ID (dec)\": " + decID + ","
//                    + "\"ID (reversed dec)\": " + reversedDecID + ","
//                    + "\"Technologies\": " + joinArray(techList, prefix.length()) + ","
//                    + "\"Mifare Classic type\": \"" + type + "\","
//                    + "\"Mifare size\": " + size + ","
//                    + "\"Mifare sectors\": " + sector + ","
//                    + "\"Mifare blocks\": " + block + "}";
//        } else {
//            json = "{ \"ID (hex)\": \"" + hexID + "\","
//                    + "\"ID (reversed hex)\": \"" + reversedHexID + "\","
//                    + "\"ID (dec)\": " + decID + ","
//                    + "\"ID (reversed dec)\": " + reversedDecID + ","
//                    + "\"Technologies\": " + joinArray(techList, prefix.length()) + ","
//                    + "\"Mifare Ultralight type\": \"" + type + "\"}";
//        }
//        Log.v(TAG, json);
        Log.v(TAG,sb.toString());
        return sb.toString();
    }

//    private String joinArray(String[] strings, int trim) {
//        String result = "[";
//        for (String string : strings) {
//            result += "\"" + string.substring(trim) + "\", ";
//        }
//        return result.substring(0, result.length() - 2) + "]";
//    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (byte aByte : bytes) {
            long value = aByte & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }
}