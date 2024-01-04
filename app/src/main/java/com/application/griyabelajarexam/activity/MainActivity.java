package com.application.griyabelajarexam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.application.griyabelajarexam.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends Base {
    private FancyButton actionOne, actionTwo;
    private TextInputEditText url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        init();
    }

    @Override
    protected void initView() {
        super.initView();

        actionOne = findViewById(R.id.action1);
        actionTwo = findViewById(R.id.action2);
        url = findViewById(R.id.url);

        back.setVisibility(View.GONE);
        title.setPadding(100, 0,0,0);
    }

    private void init() {
        actionOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmpUrl = url.getText().toString().isEmpty() ? "https://app.griyabelajar.com" : url.getText().toString();
                String baseUrl = "https://" + tmpUrl;
                startIntent(baseUrl);
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

        if (helper.getSession("url") != null) {
            startIntent(helper.getSession("url"));
        }
    }

    private void startIntent(String tmpUrl) {
        if (Patterns.WEB_URL.matcher(tmpUrl).matches()) {
            if (tmpUrl.contains("griyabelajar.com")) {
                helper.saveSession("url", tmpUrl);

                Intent intent = new Intent(MainActivity.this, FrameActivity.class);
                intent.putExtra("url", tmpUrl);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Link URL yang kamu masukan tidak kami izinkan!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Link URL yang kamu masukan tidak valid!", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(MainActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    startIntent(result.getContents());
                }
            });
}
