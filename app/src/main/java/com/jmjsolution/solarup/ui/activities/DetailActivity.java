package com.jmjsolution.solarup.ui.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmjsolution.solarup.services.emailService.GmailService;
import com.jmjsolution.solarup.ui.fragments.ContactUsFragment;
import com.jmjsolution.solarup.ui.fragments.EmptyFragment;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.ui.fragments.AgendaFragment;
import com.jmjsolution.solarup.ui.fragments.ProjectsFragment;
import com.jmjsolution.solarup.ui.fragments.SettingsFragment;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;
import static com.jmjsolution.solarup.utils.Constants.ACCOUNT_TYPE;
import static com.jmjsolution.solarup.utils.Constants.Database.IS_USER_CURRENTLY_ACTIVE;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;
import static com.jmjsolution.solarup.utils.Constants.GMAIL;
import static com.jmjsolution.solarup.utils.Constants.IS_EMAIL_LINKED;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.realisationLayout) CardView mRealisationWdw;
    @BindView(R.id.projetLayout) CardView mProjectWdw;
    @BindView(R.id.newProjectLayout) CardView mNewProjectWdw;
    @BindView(R.id.reglagesLayout) CardView mReglagesWdw;
    @BindView(R.id.contactUsLayout) CardView mContactUsWdw;
    @BindView(R.id.infosLayout) CardView mInfosWdw;
    @BindView(R.id.frameLayout) FrameLayout mFrameLayout;
    @BindView(R.id.progressBarFrmLyt) ProgressBar mProgressBar;

    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mSharedPref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mRealisationWdw.setOnClickListener(this);
        mProjectWdw.setOnClickListener(this);
        mNewProjectWdw.setOnClickListener(this);
        mReglagesWdw.setOnClickListener(this);
        mContactUsWdw.setOnClickListener(this);
        mInfosWdw.setOnClickListener(this);

        checkIfUserStillValid();

        int cardviewNumber = getIntent().getIntExtra("cardviewNumber", -1);
        if(cardviewNumber != -1){
            if(cardviewNumber == 0){
                startAgendaFrag(mRealisationWdw ,cardviewNumber);
            } else if(cardviewNumber == 1){
                startProjectsFragment(mProjectWdw, cardviewNumber);
            }  else if(cardviewNumber == 3){
                startSettingsFragment(mReglagesWdw);
            } else if (cardviewNumber == 4) {
                if(mSharedPref.getBoolean(IS_EMAIL_LINKED, false)){
                    startContactUsFragment(mContactUsWdw);
                } else {
                    startEmptyFrag(mContactUsWdw);
                }
            }
        }

    }

    private void startEmptyFrag(CardView view) {
        view.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        EmptyFragment emptyFragment = new EmptyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", 5);
        emptyFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, emptyFragment);
        fragmentTransaction.commit();
    }

    private void startContactUsFragment(CardView view) {
        view.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ContactUsFragment contactUsFragment = new ContactUsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", 5);
        contactUsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, contactUsFragment);
        fragmentTransaction.commit();
    }

    private void startProjectsFragment(CardView view, int cardviewNumber) {
        view.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ProjectsFragment projectsFragment = new ProjectsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", cardviewNumber);
        projectsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, projectsFragment);
        fragmentTransaction.commit();
    }

    private void startAgendaFrag(CardView cardView, int cardviewNumber){
        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AgendaFragment agendaFragment = new AgendaFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", cardviewNumber);
        agendaFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, agendaFragment);
        fragmentTransaction.commit();
    }

    private void startSettingsFragment(CardView view) {
        view.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", 4);
        settingsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, settingsFragment);
        fragmentTransaction.commit();
    }



    private void checkIfUserStillValid() {
        if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().getCurrentUser().reload().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        mDatabase.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())).update(IS_USER_CURRENTLY_ACTIVE, false).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(DetailActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
                    }
                }
            });
        }
    }


    @Override
    public void onClick(View view) {
        if(view == mRealisationWdw){
            resetDesign(mRealisationWdw, mProjectWdw, mNewProjectWdw, mReglagesWdw, mContactUsWdw, mInfosWdw);
            startAgendaFrag(mRealisationWdw, 0);
        }
        if(view == mProjectWdw){
            resetDesign(mProjectWdw, mRealisationWdw, mNewProjectWdw, mReglagesWdw, mContactUsWdw, mInfosWdw);
            startProjectsFragment(mProjectWdw, 1);
        }
        if(view == mNewProjectWdw){
            resetDesign(mNewProjectWdw, mProjectWdw, mRealisationWdw, mReglagesWdw, mContactUsWdw, mInfosWdw);
            startInfoCustomerActivity();
        }
        if (view == mReglagesWdw){
            resetDesign(mReglagesWdw, mRealisationWdw, mProjectWdw, mNewProjectWdw, mContactUsWdw, mInfosWdw);
            startSettingsFragment(mReglagesWdw);
        }
        if (view == mContactUsWdw) {
            resetDesign(mContactUsWdw, mReglagesWdw, mRealisationWdw, mProjectWdw, mNewProjectWdw, mInfosWdw);
            startContactUsFragment(mContactUsWdw);
        }
        if (view == mInfosWdw) {
            resetDesign(mInfosWdw, mReglagesWdw, mRealisationWdw, mProjectWdw, mNewProjectWdw, mContactUsWdw);
        }
    }

    private void startInfoCustomerActivity(){
        startActivity(new Intent(this, ProcessActivity.class));
    }

    private void resetDesign(CardView cardViewToChange, CardView cardView1, CardView cardView2, CardView cardView3, CardView cardView4, CardView cardView5) {
        cardViewToChange.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        cardView1.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardView2.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardView3.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardView4.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        cardView5.setCardBackgroundColor(getResources().getColor(android.R.color.white));
    }
}
