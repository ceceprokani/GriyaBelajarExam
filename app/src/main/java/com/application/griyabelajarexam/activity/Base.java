package com.application.griyabelajarexam.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.application.griyabelajarexam.R;
import com.application.griyabelajarexam.helper.BatteryDrawable;
import com.application.griyabelajarexam.helper.General;

import java.util.Arrays;
import java.util.List;

public class Base extends AppCompatActivity {
    private ImageView battery;
    protected ImageButton back;
    protected General helper;
    protected TextView title;

    private BatteryDrawable batteryDrawable;
    protected String packageList[] = {
            "comspli.exaspli.splitscspli",
            "com.split.screen.shortcut.overview.accessibility.notification",
            "any.splitscreen",
            "com.split.screen",
            "com.fb.splitscreenlauncher",
            "maxcom.toolbox.screensplitter",
            "com.dvg.multivideoplayer",
            "com.mercandalli.android.apps.bubble",
            "com.split.screen.shortcut",
            "com.artds.split.dual.screen.nb",
            "com.dualbrowser.splitscreen.multi",
            "splitscreen.dualscreen.favoriteappindia",
            "com.view.multiscreenviewbrowser",
            "com.aicity.aiscreen",
            "com.applay.overlay",
            "com.lwi.android.flapps"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        batteryDrawable = new BatteryDrawable();
        this.helper = new General(this);
    }

    protected void initView() {
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        battery = findViewById(R.id.battery);

        battery.setImageDrawable(batteryDrawable);
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int batteryPct = (int) (level * 100 / (float) scale);
            batteryDrawable.setBatteryLevel(batteryPct);

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
        }
    };

    protected boolean checkViolation() {
        boolean isInstalled = false;
        try {
            List<ApplicationInfo> listPackageInstalled = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            String applicationNameDetected = "";
            for (ApplicationInfo data: listPackageInstalled) {
                if (Arrays.asList(packageList).contains(data.packageName)) {
                    isInstalled = true;

                    final PackageManager pm = getApplicationContext().getPackageManager();
                    ApplicationInfo ai;
                    try {
                        ai = pm.getApplicationInfo(data.packageName, 0);
                    } catch (final PackageManager.NameNotFoundException e) {
                        ai = null;
                    }

                    applicationNameDetected = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                    break;
                }
            }

//            // if app is not installed
            if (isInstalled) {
                Toast.makeText(getApplicationContext(), "Kamu terdekteksi melakukan kecurangan dengan menginstall aplikasi " + applicationNameDetected, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isInstalled;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(this.mBatInfoReceiver);
    }
}
