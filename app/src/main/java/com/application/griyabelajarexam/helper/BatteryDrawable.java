package com.application.griyabelajarexam.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BatteryDrawable extends Drawable {
    private Paint paint;
    private RectF batteryBody;
    private RectF batteryHead;
    private int batteryLevel; // Level baterai (0 - 100)

    public BatteryDrawable() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        batteryBody = new RectF();
        batteryHead = new RectF();
    }

    public void setBatteryLevel(int level) {
        batteryLevel = Math.max(0, Math.min(level, 100)); // Jaga level antara 0-100
        invalidateSelf(); // Refresh ikon
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();

        // Ukuran bagian baterai
        float bodyWidth = bounds.width() * 0.7f;
        float bodyHeight = bounds.height() * 0.85f;
        float cornerRadius = 15f; // Membulatkan sudut
        float headWidth = bodyWidth * 0.4f;
        float headHeight = bounds.height() * 0.1f;

        // Posisi tubuh baterai
        batteryBody.set(bounds.left + (bounds.width() - bodyWidth) / 2, bounds.top + headHeight,
                bounds.left + (bounds.width() + bodyWidth) / 2, bounds.top + headHeight + bodyHeight);

        // Posisi kepala baterai
        batteryHead.set(bounds.left + (bounds.width() - headWidth) / 2, bounds.top,
                bounds.left + (bounds.width() + headWidth) / 2, bounds.top + headHeight);

        // Gambar kepala baterai
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(batteryHead, cornerRadius, cornerRadius, paint);

        // Gambar tubuh baterai (Border dengan warna #C96B06)
        paint.setColor(Color.parseColor("#C96B06"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        canvas.drawRoundRect(batteryBody, cornerRadius, cornerRadius, paint);

        // Isi baterai berbentuk rounded sesuai level
        float levelHeight = batteryBody.height() * batteryLevel / 100;
        float fillCornerRadius = 12f; // Membulatkan sudut isi baterai
        paint.setStyle(Paint.Style.FILL);

        // Warna gradasi hijau ke merah
        int startColor = Color.parseColor("#00db49");
        int endColor = Color.parseColor("#e04802");
        float[] hsvStart = new float[3];
        float[] hsvEnd = new float[3];
        Color.colorToHSV(startColor, hsvStart);
        Color.colorToHSV(endColor, hsvEnd);
        float levelFraction = batteryLevel / 100f;
        int fillColor = Color.HSVToColor(new float[]{
                hsvStart[0] + (hsvEnd[0] - hsvStart[0]) * (1 - levelFraction),
                hsvStart[1],
                hsvStart[2]
        });
        paint.setColor(fillColor);

        // Isi baterai rounded
        canvas.drawRoundRect(batteryBody.left, batteryBody.bottom - levelHeight,
                batteryBody.right, batteryBody.bottom,
                fillCornerRadius, fillCornerRadius, paint);

        // Tambahkan teks persentase
//        paint.setColor(Color.WHITE);
//        paint.setTextSize(25);
//        paint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText(String.valueOf(batteryLevel), batteryBody.centerX(), batteryBody.centerY() + 15, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
