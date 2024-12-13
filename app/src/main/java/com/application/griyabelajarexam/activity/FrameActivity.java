package com.application.griyabelajarexam.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.application.griyabelajarexam.R;

import java.util.Timer;
import java.util.TimerTask;

public class FrameActivity extends Base {
    private WebView frame;
    private String url;
    private boolean isPause = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isScreenLockedMode = false;

    private boolean onQuizPage = false;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_content);

        handler = new Handler(Looper.getMainLooper());

        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isInLockTaskMode()) {
                    finished();
                }

                handler.postDelayed(this, 1000);
            }
        };

        // Menjalankan Runnable pertama kali
        handler.post(runnable);

        initView();
        init();

        onBackPress();
    }

    @Override
    protected void initView() {
        super.initView();

        swipeRefreshLayout = findViewById(R.id.swipe);
        frame = findViewById(R.id.frame);
    }

    private void init() {
        this.loadWeb();
        if (this.checkViolation()) {
            new Timer().scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    finished();
                }
            },0,1000);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                frame.reload();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn()) initConfirmationDialog();
                else finished();
            }
        });
    }

    private void loadWeb() {
        try {
            this.url = getIntent().getStringExtra("url");

            frame.clearCache(true);
            frame.setVerticalScrollBarEnabled(true);
            frame.setHorizontalScrollBarEnabled(true);
            frame.getSettings().setBuiltInZoomControls(false);
            frame.getSettings().setDisplayZoomControls(false);
            frame.getSettings().setSupportZoom(false);
            frame.setLongClickable(false);
            frame.getSettings().setDomStorageEnabled(true);
            frame.setHapticFeedbackEnabled(false);
            frame.getSettings().setJavaScriptEnabled(true);
            frame.getSettings().setLoadWithOverviewMode(false);
            frame.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            frame.getSettings().setUserAgentString(frame.getSettings().getUserAgentString() + " griyaexam");
            frame.setBackgroundColor(Color.TRANSPARENT);
            frame.setLayerType(WebView.LAYER_TYPE_NONE, null);
            frame.clearHistory();
//            frame.setWebChromeClient(new WebChromeClient() {
//                private ProgressDialog mProgress;
//
//                @Override
//                public void onProgressChanged(WebView view, int progress) {
//                    try {
//                        if (mProgress == null) {
//                            mProgress = new ProgressDialog(getBaseContext());
//                            mProgress.show();
//                        }
//                        mProgress.setMessage("Loading " + String.valueOf(progress) + "%");
//                        if (progress == 100) {
//                            mProgress.dismiss();
//                            mProgress = null;
//                        }
//                    } catch (Exception e) {}
//                }
//            });
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
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    if (error.getDescription().length() > 0) {
                        helper.saveSession("isLoggedIn", "false");
//                        Toast.makeText(FrameActivity.this, "Laman yang kamu kunjungi tidak ditemukan!", Toast.LENGTH_SHORT).show();
//                        finished();
                    }
                    super.onReceivedError(view, request, error);
                }

                @Override
                public void onPageFinished(final WebView view, final String url) {
                    super.onPageFinished(view, url);

                    if (url.contains("griyabelajar.com") && !url.equals("https://app.griyabelajar.com/#/signin")) {
                        helper.saveSession("isLoggedIn", "1");
                    } else {
                        helper.saveSession("isLoggedIn", "0");
                    }

                    if (url.contains("/start/")) {
                        onQuizPage = true;
                    }
                }
            });

            frame.loadUrl(url);
        } catch (Exception ignored) {
        }
    }

    private boolean isLoggedIn() {
        return this.helper.getSession("isLoggedIn") != null && this.helper.getSession("isLoggedIn").equals("1");
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
                if (onQuizPage) {
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
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                            super.onReceivedError(view, request, error);
                        }

                        @Override
                        public void onPageFinished(final WebView view, final String url) {
                            super.onPageFinished(view, url);

                            finished();
                        }
                    });
                    frame.loadUrl("javascript:(function(){document.getElementById('block_users').click();})();");
                } else {
                    finished();
                }
            }
        }).setNegativeButton(android.R.string.no, null).show();
    }

    private void finished() {
        stopLockTask();
        WebStorage.getInstance().deleteAllData();
        helper.clearSession();
        finish();
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (!isScreenLockedMode) {
                finish();
            }
            return false;
        } else {
            return super.onKeyLongPress(keyCode, event);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        this.isPause = true;

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    public void onResume() {
        super.onResume();

        if (this.checkViolation() && this.isPause) {
            new Timer().scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    finished();
                }
            },0,1000);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        if (this.checkViolation()) {
            new Timer().scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    finished();
                }
            },0,1000);
        } else {
            frame.reload();
        }
//        finished();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Jangan lupa untuk menghentikan handler saat activity dihancurkan
        handler.removeCallbacks(runnable);
    }
}
