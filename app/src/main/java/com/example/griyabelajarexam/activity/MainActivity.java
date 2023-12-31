package com.example.griyabelajarexam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.griyabelajarexam.R;
import com.google.android.material.textfield.TextInputEditText;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
    private FancyButton actionOne, actionTwo;
    private TextInputEditText url;
    private final String SESSION = "GRIYA_SESSION";

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
                Intent intent = new Intent(MainActivity.this, FrameActivity.class);
                intent.putExtra("url", tmpUrl);
                startActivity(intent);
            }
        });
    }
}
