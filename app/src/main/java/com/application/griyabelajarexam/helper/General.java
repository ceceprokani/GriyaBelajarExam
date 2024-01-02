package com.application.griyabelajarexam.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class General {
    private final String SESSION = "GRIYA_SESSION";
    private Context context;

    public General(Context context) {
        this.context = context;
    }

    // Fungsi ini digunakan untuk menyimpan session sesuai dengan key dan value yang diinginkan
    public void saveSession(String name, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }

    // Fungsi ini digunakan untuk mengambil data session sesuai dengan key yang diinginkan
    public String getSession(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    // Fungsi ini digunakan untuk membersihkan session / proses logout aplikasi
    public void clearSession() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /** @noinspection unused*/ // Fungsi ini digunakan untuk membersihkan session / proses logout aplikasi
    public void clearSessionByKey(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }
}
