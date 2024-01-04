package com.application.griyabelajarexam.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.griyabelajarexam.R;
import com.application.griyabelajarexam.helper.General;

import eo.view.batterymeter.BatteryMeter;

public class Base extends AppCompatActivity {
    private BatteryMeter batteryMeter;
    protected ImageButton back;
    protected General helper;
    protected TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.helper = new General(this);
    }

    protected void initView() {
        batteryMeter = findViewById(R.id.battery);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
    }


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            int batteryPct = (int) (level * 100 / (float) scale);
            batteryMeter.setChargeLevel(batteryPct);

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            if (isCharging) {
                batteryMeter.setCharging(true);
            } else {
                batteryMeter.setCharging(false);
            }
        }
    };

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
