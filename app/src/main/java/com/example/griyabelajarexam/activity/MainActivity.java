package com.example.griyabelajarexam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.griyabelajarexam.R;
import com.example.griyabelajarexam.helper.General;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
    private FancyButton actionOne, actionTwo;
    private TextInputEditText url;
    private General helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        init();
    }

    private void initView() {
        actionOne = findViewById(R.id.action1);
        actionTwo = findViewById(R.id.action2);
        url = findViewById(R.id.url);
    }

    private void init() {
        actionOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmpUrl = url.getText().toString().isEmpty() ? "https://app.griyabelajar.com" : url.getText().toString();
                startIntent(tmpUrl);
            }
        });
        actionTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanOptions options = new ScanOptions();
                options.setPrompt("Place a qrcode inside the rectangle to scan it.");
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setBarcodeImageEnabled(true);
                options.setOrientationLocked(false);
                barcodeLauncher.launch(options);
            }
        });

        this.helper = new General(this);

        if(helper.getSession("url") != null) {
            startIntent(helper.getSession("url"));
        }
    }

    private void startIntent(String tmpUrl) {
        helper.saveSession("url", tmpUrl);

        Intent intent = new Intent(MainActivity.this, FrameActivity.class);
        intent.putExtra("url", tmpUrl);
        startActivity(intent);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(MainActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    startIntent(result.getContents());
                }
            });

}
