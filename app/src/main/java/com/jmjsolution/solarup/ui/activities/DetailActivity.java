package com.jmjsolution.solarup.ui.activities;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.ArrayMap;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.jmjsolution.solarup.ui.fragments.ContactUsFragment;
import com.jmjsolution.solarup.ui.fragments.EmptyFragment;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.ui.fragments.AgendaFragment;
import com.jmjsolution.solarup.ui.fragments.PackFragment;
import com.jmjsolution.solarup.ui.fragments.ProjectsFragment;
import com.jmjsolution.solarup.ui.fragments.SettingsFragment;
import com.jmjsolution.solarup.services.calendarService.CalendarService;

import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.ui.fragments.SettingsFragment.ACCOUNT_CHOOSER;
import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;
import static com.jmjsolution.solarup.utils.Constants.Database.IS_ENABLED_USER;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;
import static com.jmjsolution.solarup.utils.Constants.GMAIL;
import static com.jmjsolution.solarup.utils.Constants.IS_EMAIL_LINKED;
import static com.jmjsolution.solarup.utils.Constants.IS_PASSWORD_STORED;
import static com.jmjsolution.solarup.utils.Constants.PASSWORD;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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
                if(sharedPref.getBoolean(IS_EMAIL_LINKED, false) && sharedPref.getBoolean(IS_PASSWORD_STORED, false)){
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
                        mDatabase.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())).update(IS_ENABLED_USER, 2).addOnSuccessListener(new OnSuccessListener<Void>() {
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACCOUNT_CHOOSER){
            if(data != null) {
                final SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                String name = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                if(!getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getBoolean(IS_PASSWORD_STORED, false)
                        || !Objects.requireNonNull(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(GMAIL, null)).equalsIgnoreCase(name)){
                    final EditText taskEditText = new EditText(this);
                    taskEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    new AlertDialog.Builder(this)
                            .setTitle(name)
                            .setMessage("Entrez votre mot de passe Gmail")
                            .setView(taskEditText)
                            .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                                @SuppressLint("SetJavaScriptEnabled")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mProgressBar.setVisibility(View.VISIBLE);
                                    Map<String, Object> password = new ArrayMap<>();
                                    password.put(PASSWORD, taskEditText.getText().toString());
                                    mDatabase.collection(ROOT)
                                            .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())).set(password, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProgressBar.setVisibility(View.GONE);
                                            getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit().putBoolean(IS_PASSWORD_STORED, true).apply();
                                            Toast.makeText(DetailActivity.this, "Veuillez autoriser l'accés aux applications moins sécurisées", Toast.LENGTH_LONG).show();

                                            String url = "https://myaccount.google.com/lesssecureapps";
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });



                                }
                            }).create().show();
                }

                // TODO : resolve clear events list after changing account
            /*if(!name.equalsIgnoreCase(getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString("email", null))){
                EventAdapter.mEvents.clear();
            }*/

                editor.putString(GMAIL, name).apply();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new SettingsFragment())
                        .commit();
                CalendarService.syncCalendars(this);
            }
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
