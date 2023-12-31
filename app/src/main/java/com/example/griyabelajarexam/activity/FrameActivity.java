package com.example.griyabelajarexam.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.griyabelajarexam.R;

public class FrameActivity extends AppCompatActivity {
    private WebView frame;
    private String url;
    private final String SESSION = "GRIYA_SESSION";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_content);

        initView();
        init();
        startLockTask();

        onBackPress();
    }

    private void initView() {
        swipeRefreshLayout = findViewById(R.id.swipe);
        frame = findViewById(R.id.frame);
    }

    private void init() {
        this.loadWeb();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                frame.reload();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadWeb() {
        try {
            this.url = getIntent().getStringExtra("url");

            frame.clearCache(true);
            frame.setVerticalScrollBarEnabled(true);
            frame.setHorizontalScrollBarEnabled(true);
            frame.getSettings().setBuiltInZoomControls(true);
            frame.getSettings().setDisplayZoomControls(false);
            frame.getSettings().setSupportZoom(true);
            frame.setLongClickable(false);
            frame.getSettings().setDomStorageEnabled(true);
            frame.setHapticFeedbackEnabled(false);
            frame.getSettings().setJavaScriptEnabled(true);
            frame.getSettings().setLoadWithOverviewMode(true);
            frame.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            frame.setBackgroundColor(Color.TRANSPARENT);
            frame.setLayerType(WebView.LAYER_TYPE_NONE, null);
            frame.clearHistory();
            frame.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    // TODO Auto-generated method stub
                    view.loadUrl(request.getUrl().toString());
                    return true;
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    super.onReceivedSslError(view, handler, error);
                }

                @Override
                public void onPageFinished(final WebView view, final String url) {
                    if (url.equals("https://app.griyabelajar.com/#/signin")) {
                        saveSessionLoggedIn(false);
                    } else {
                        saveSessionLoggedIn(true);
                    }
                }
            });
            frame.loadUrl(url);
        } catch (Exception err) {
        }
    }

    private void saveSessionLoggedIn(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void onBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isLoggedIn()) initConfirmationDialog();
                else finished();
            }
        });
    }

    private void initConfirmationDialog() {
        new AlertDialog.Builder(this).setTitle("PERINGATAN!!!").setMessage("Apakah kamu yakin akan keluar dari aplikasi ? kamu akan otomatis logout" + " dari aplikasi!").setIcon(R.drawable.warning).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finished();
            }
        }).setNegativeButton(android.R.string.no, null).show();
    }

    private void finished() {
        stopLockTask();
        finish();
        WebStorage.getInstance().deleteAllData();
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startLockTask();
            return false;
        } else {
            return super.onKeyLongPress(keyCode, event);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    protected void onUserLeaveHint() {
        finished();
    }
}
