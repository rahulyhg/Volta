package com.jmjsolution.solarup.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.utils.BitmapFromViewHelper;
import com.jmjsolution.solarup.utils.PDFUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.jmjsolution.solarup.ui.fragments.InformationsCustomerFragment.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.jmjsolution.solarup.ui.fragments.InformationsCustomerFragment.MY_PERMISSIONS_REQUEST_READ_CALENDAR;
import static com.jmjsolution.solarup.utils.Constants.Database.IS_ENABLED_USER;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.realisationLayout) CardView mRealisationWdw;
    @BindView(R.id.projetLayout) CardView mProjectWdw;
    @BindView(R.id.newProjectLayout) CardView mNewProjectWdw;
    @BindView(R.id.reglagesLayout) CardView mReglagesWdw;
    @BindView(R.id.contactUsLayout) CardView mContactUsWdw;
    @BindView(R.id.infosLayout) CardView mInfosWdw;
    @BindView(R.id.rootView) LinearLayout mRootView;

    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setCalendarHorizontal();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            checkPermissions(MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PERMISSION_GRANTED) {
            checkPermissions(MY_PERMISSIONS_REQUEST_READ_CALENDAR, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            checkPermissions(MY_PERMISSIONS_REQUEST_READ_CALENDAR, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mRealisationWdw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionIntent(0);
            }
        });
        mProjectWdw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionIntent(1);
            }
        });
        mNewProjectWdw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startInfoCustomerActivity();
            }
        });
        mReglagesWdw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionIntent(3);
            }
        });
        mContactUsWdw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionIntent(4);
            }
        });
        mInfosWdw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionIntent(5);
            }
        });
    }



    private void startInfoCustomerActivity(){
        startActivity(new Intent(this, ScrollActivity.class));
    }


    private void checkPermissions(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }

    private void transitionIntent(int cardviewNumber) {
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
        intent.putExtra("cardviewNumber", cardviewNumber);
        Pair<View, String> p1 = Pair.create((View)mRealisationWdw, "realisationTransition");
        Pair<View, String> p2 = Pair.create((View)mProjectWdw, "projectTransition");
        Pair<View, String> p3 = Pair.create((View)mNewProjectWdw, "newProjectTransition");
        Pair<View, String> p4 = Pair.create((View)mReglagesWdw, "reglagesTransition");
        Pair<View, String> p5 = Pair.create((View)mContactUsWdw, "contactUsTransition");
        Pair<View, String> p6 = Pair.create((View)mInfosWdw, "infosTransition");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this, p1, p2, p3, p4, p5, p6);
        startActivity(intent, optionsCompat.toBundle());
    }

    private void setCalendarHorizontal(){
        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance(Locale.FRANCE);
        startDate.add(Calendar.MONTH, 0);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance(Locale.FRANCE);
        endDate.add(Calendar.MONTH, 2);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                    .showBottomText(false)
                    .formatTopText("MMM")
                .end()
                .build();

        horizontalCalendar.goToday(true);
        }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfUserStillValid();
    }

    private void checkIfUserStillValid() {
        if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().getCurrentUser().reload().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        mDatabase.collection(ROOT)
                                .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())).update(IS_ENABLED_USER, 2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
                    }
                }
            });
        }
    }
}
