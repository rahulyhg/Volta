package com.jmjsolution.solarup.notUsedForNow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;

public class BitmapFromViewHelper {

    public static Bitmap getImageFromLayout(Context context, @LayoutRes int viewLayout, int width, int height) {
        View layoutTemplate = LayoutInflater.from(context).inflate(viewLayout, null);

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        return getBitmapFromView(layoutTemplate, widthMeasureSpec, heightMeasureSpec);
    }

    public static Bitmap getImageFromLayout(Context context, @LayoutRes int viewLayout) {
        View layoutTemplate = LayoutInflater.from(context).inflate(viewLayout, null);

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        return getBitmapFromView(layoutTemplate, widthMeasureSpec, heightMeasureSpec);
    }

    private static Bitmap getBitmapFromView(View view, int widthMeasureSpec, int heightMeasureSpec) {
        view.measure(widthMeasureSpec, heightMeasureSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap resultBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(resultBitmap);
        view.draw(c);
        return resultBitmap;
    }
}