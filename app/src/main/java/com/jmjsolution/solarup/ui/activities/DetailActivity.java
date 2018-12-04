package com.jmjsolution.solarup.ui.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmjsolution.solarup.ui.fragments.EmptyFragment;
import com.jmjsolution.solarup.ui.fragments.InformationsCustomerFragment;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.ui.fragments.AgendaFragment;
import com.jmjsolution.solarup.ui.fragments.SettingsFragment;
import com.jmjsolution.solarup.utils.CalendarService;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.ui.fragments.SettingsFragment.ACCOUNT_CHOOSER;
import static com.jmjsolution.solarup.utils.CalendarService.MY_PREFS_NAME;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.realisationLayout) CardView mRealisationWdw;
    @BindView(R.id.projetLayout) CardView mProjectWdw;
    @BindView(R.id.newProjectLayout) CardView mNewProjectWdw;
    @BindView(R.id.reglagesLayout) CardView mReglagesWdw;
    @BindView(R.id.contactUsLayout) CardView mContactUsWdw;
    @BindView(R.id.infosLayout) CardView mInfosWdw;
    @BindView(R.id.frameLayout) FrameLayout mFrameLayout;

    private FirebaseFirestore mDatabase;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mDatabase = FirebaseFirestore.getInstance();
        mEmail = getIntent().getStringExtra("email");

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
                startEmptyFrag(mProjectWdw, cardviewNumber);
            } else if(cardviewNumber == 2){
                startInfoCustomerFrag(mNewProjectWdw, 3);
            } else if(cardviewNumber == 3){
                mReglagesWdw.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                startSettingsFragment(mReglagesWdw, 4);
            } else if (cardviewNumber == 4) {
            }
        }

    }

    private void startEmptyFrag(CardView view, int cardviewNumber) {
        view.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        EmptyFragment emptyFragment = new EmptyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", cardviewNumber);
        emptyFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, emptyFragment);
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

    private void startInfoCustomerFrag(CardView view, int cardviewNumber) {
        view.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        InformationsCustomerFragment informationsCustomerFragment = new InformationsCustomerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", cardviewNumber);
        informationsCustomerFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, informationsCustomerFragment);
        fragmentTransaction.commit();
    }

    private void startSettingsFragment(CardView view, int cardviewNumber) {
        view.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", cardviewNumber);
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
                        mDatabase.collection("users").document(mEmail).update("user_is_enabled", 2).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            startEmptyFrag(mProjectWdw, 1);
        }
        if(view == mNewProjectWdw){
            resetDesign(mNewProjectWdw, mRealisationWdw, mProjectWdw, mReglagesWdw, mContactUsWdw, mInfosWdw);
            startInfoCustomerFrag(mNewProjectWdw, 2);
        }
        if (view == mReglagesWdw){
            resetDesign(mReglagesWdw, mRealisationWdw, mProjectWdw, mNewProjectWdw, mContactUsWdw, mInfosWdw);
            startSettingsFragment(mReglagesWdw, 4);
        }
        if (view == mContactUsWdw) {
            resetDesign(mContactUsWdw, mReglagesWdw, mRealisationWdw, mProjectWdw, mNewProjectWdw, mInfosWdw);
        }
        if (view == mInfosWdw) {
            resetDesign(mInfosWdw, mReglagesWdw, mRealisationWdw, mProjectWdw, mNewProjectWdw, mContactUsWdw);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACCOUNT_CHOOSER){
            String name = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("email", name).apply();
            CalendarService.syncCalendars(this);
        }
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
