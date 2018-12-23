package com.jmjsolution.solarup.ui.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;
import com.jmjsolution.solarup.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.Date;

public class ScrollActivity extends AppCompatActivity {

    private static final int SHARE_IMAGE_NAME = 12;
    private LinearLayout llScroll;
    private Bitmap bitmap;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infos_customer_fragment_copie);;

        AbstractViewRenderer page = new AbstractViewRenderer(this, R.layout.infos_customer_fragment_copie) {
            @Override
            protected void initView(View view) {

            }
        };

        page.setReuseBitmap(true);

        PdfDocument doc = new PdfDocument(this);

// add as many pages as you have
        doc.addPage(page);

        doc.setRenderWidth(1200);
        doc.setRenderHeight(1692);
        doc.setOrientation(PdfDocument.A4_MODE.PORTRAIT);
        doc.setProgressTitle(R.string.a_propos);
        doc.setProgressMessage(R.string.configurer_mon_compte);
        doc.setFileName("good");
        doc.setSaveDirectory(getExternalFilesDir(null));
        doc.setInflateOnMainThread(false);
        doc.setListener(new PdfDocument.Callback() {
            @Override
            public void onComplete(File file) {
                Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete");
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(file);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    //startActivity(intent);

                    String[] mailto = {"samuel2629@gmail.com"};
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Calc PDF Report");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, " PDF Report");
                    emailIntent.setType("application/pdf");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(emailIntent, "Send email using:"));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.i(PdfDocument.TAG_PDF_MY_XML, "Error");
            }
        });

        doc.createPdf(this);

    }



}