package com.application.griyabelajarexam.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
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
    private TextView version;

    private boolean isScreenLockedMode = false;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        init();

        handler = new Handler(Looper.getMainLooper());

        runnable = new Runnable() {
            @Override
            public void run() {
                if (isInLockTaskMode()) {
                    isScreenLockedMode = true;
                } else {
                    isScreenLockedMode = false;
                }

                handler.postDelayed(this, 1000);
            }
        };

        // Menjalankan Runnable pertama kali
        handler.post(runnable);
    }

    @Override
    protected void initView() {
        super.initView();

        version = findViewById(R.id.version);
        actionOne = findViewById(R.id.action1);
        actionTwo = findViewById(R.id.action2);
        url = findViewById(R.id.url);
    }

    private void init() {
        back.setVisibility(View.GONE);
        title.setPadding(100, 0, 0, 0);

        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;

            version.setText("Versi " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        actionOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmpUrl = url.getText().toString().isEmpty() ? "https://app.griyabelajar.com" : url.getText().toString();
                String baseUrl = "https://" + tmpUrl;
                if (!checkViolation()) {
                    if (!isScreenLockedMode) {
                        startKioskMode();
                    } else {
                        startIntent(baseUrl);
                    }
                }
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
            if (!checkViolation()) {
                if (!isScreenLockedMode) {
                    startKioskMode();
                } else {
                    startIntent(helper.getSession("url"));
                }
            }
        }
    }

    private void startIntent(String tmpUrl) {
        if (Patterns.WEB_URL.matcher(tmpUrl).matches()) {
            if (tmpUrl.toLowerCase().contains("griyabelajar")) {
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
                    if (!checkViolation())
                        startIntent(result.getContents());
                }
            });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Jangan lupa untuk menghentikan handler saat activity dihancurkan
        handler.removeCallbacks(runnable);
    }
}