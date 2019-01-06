package com.jmjsolution.solarup.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.interfaces.GetDataSolarService;
import com.jmjsolution.solarup.model.Solar;
import com.jmjsolution.solarup.services.emailService.GmailService;
import com.jmjsolution.solarup.services.retrofitClient.RetrofitClientInstance;
import com.jmjsolution.solarup.ui.fragments.SettingsFragment;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;
import static com.jmjsolution.solarup.utils.Constants.Database.EMAIL_RECUPERATION;
import static com.jmjsolution.solarup.utils.Constants.Database.IS_FIRST_CONNECTION;
import static com.jmjsolution.solarup.utils.Constants.Database.IS_USER_CURRENTLY_ACTIVE;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;
import static com.jmjsolution.solarup.utils.Constants.PASSWORD;

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

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.WRITE_CALENDAR,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setCalendarHorizontal();
        changePassword();

        GetDataSolarService service = RetrofitClientInstance.getRetrofit().create(GetDataSolarService.class);
        Call<Solar> call = service.getSolarData(53.0, 13.2, 4, 1, 10, 1, 40, 180);
        call.enqueue(new Callback<Solar>() {
            @Override
            public void onResponse(Call<Solar> call, Response<Solar> response) {
                response.body();
            }

            @Override
            public void onFailure(Call<Solar> call, Throwable t) {
                t.getLocalizedMessage();
            }
        });

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        /*final ArrayList<Projexio> projexios = new ArrayList<>();
        mDatabase.collection(ROOT_CREDIT).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot credit: Objects.requireNonNull(task.getResult())){
                        String creditString = (String) credit.getString("0");
                        if(Objects.requireNonNull(creditString).equalsIgnoreCase("800")){
                            Projexio projexio = new Projexio(
                                    credit.getString("0"),
                                    credit.getString("1"),
                                    credit.getString("2"),
                                    credit.getString("3"),
                                    credit.getString("4"),
                                    credit.getString("5"),
                                    credit.getString("6"),
                                    credit.getString("7"),
                                    credit.getString("8"),
                                    credit.getString("9"),
                                    credit.getString("10"),
                                    credit.getString("11"),
                                    credit.getString("12"),
                                    credit.getString("13"),
                                    credit.getString("14"),
                                    credit.getString("15"),
                                    credit.getString("16"),
                                    credit.getString("17"),
                                    credit.getString("18"),
                                    credit.getString("19")
                            );
                            projexios.add(projexio);
                        }
                    }
                }
            }
        });*/

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

    private void changePassword() {
        if(getIntent().getBooleanExtra(IS_FIRST_CONNECTION, false)){
            final String password = getIntent().getStringExtra(PASSWORD);

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.password_changer_alertdialog);
            final EditText oldPasswordEt = dialog.findViewById(R.id.oldPasswordEt);
            final EditText newPasswordOnceEt = dialog.findViewById(R.id.newPasswordOnceEt);
            final EditText newPasswordTwiceEt = dialog.findViewById(R.id.newPasswordTwiceEt);
            final EditText mailRecupEt = dialog.findViewById(R.id.recupEmail);
            Button validPasswordChangeBtn = dialog.findViewById(R.id.validPasswordChangeBtn);

            validPasswordChangeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean valid = true;

                    if(!oldPasswordEt.getText().toString().equals(password)){
                        oldPasswordEt.setError("Mauvais mot de passe");
                        valid = false;
                    }

                    if(!newPasswordOnceEt.getText().toString().equals(newPasswordTwiceEt.getText().toString())){
                        valid = false;
                        newPasswordOnceEt.setError("Les mots de passe ne sont pas identiques");
                        newPasswordTwiceEt.setError("Les mots de passe ne sont pas identiques");
                    }

                    if(mailRecupEt.getText().toString().isEmpty()){
                        valid = false;
                        mailRecupEt.setError("Requis");
                    }

                    if(valid){
                        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()), password);
                        mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mAuth.getCurrentUser().updatePassword(newPasswordOnceEt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                dialog.dismiss();
                                                Map<String, String> password = new ArrayMap<>();
                                                ((ArrayMap<String, String>) password).put(EMAIL_RECUPERATION, mailRecupEt.getText().toString());
                                                Toast.makeText(HomeActivity.this, "Mot de passe changé avec succès.", Toast.LENGTH_SHORT).show();
                                                mDatabase.collection(ROOT).document(Objects.requireNonNull(mAuth.getCurrentUser().getEmail()))
                                                        .set(password, SetOptions.merge()).isSuccessful();

                                            } else {
                                                oldPasswordEt.getText().clear();
                                                newPasswordOnceEt.getText().clear();
                                                newPasswordTwiceEt.getText().clear();
                                                Toast.makeText(HomeActivity.this, "Erreur de changement du mot de passe.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(HomeActivity.this, "Erreur d'authentification.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

            dialog.show();

        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void startInfoCustomerActivity(){
        startActivity(new Intent(this, ProcessActivity.class));
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
                                .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())).update(IS_USER_CURRENTLY_ACTIVE, false)
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
