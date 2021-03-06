package com.jmjsolution.solarup.ui.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmjsolution.solarup.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.utils.Constants.Database.CONNEXION_DATE;
import static com.jmjsolution.solarup.utils.Constants.Database.EMAIL_LOGGIN;
import static com.jmjsolution.solarup.utils.Constants.Database.EMAIL_RECUPERATION;
import static com.jmjsolution.solarup.utils.Constants.Database.IS_FIRST_CONNECTION;
import static com.jmjsolution.solarup.utils.Constants.Database.IS_USER_CURRENTLY_ACTIVE;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;
import static com.jmjsolution.solarup.utils.Constants.Database.UID;
import static com.jmjsolution.solarup.utils.Constants.PASSWORD;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    @BindView(R.id.identifiantTv) EditText mIdentifiantTv;
    @BindView(R.id.passwordTv) EditText mPasswordTv;
    @BindView(R.id.signInBtn) Button mSignInBtn;
    @BindView(R.id.progressBarLogin) ProgressBar mProgressBar;
    @BindView(R.id.forgotPasswordTv) TextView mForgotPasswordTv;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private String mMailRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                signIn(mIdentifiantTv.getText().toString(), mPasswordTv.getText().toString());
            }
        });

        mForgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

    }

    private void resetPassword(){
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(this))
                .setTitle(R.string.enter_location_label)
                .setView(taskEditText)
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMailRoot = taskEditText.getText().toString();
                            mDatabase.collection(ROOT).document(mMailRoot).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        mAuth.sendPasswordResetEmail((String) Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get(EMAIL_RECUPERATION))).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(LoginActivity.this, "Email envoyé.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Ce mail n'existe pas.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                })
                .create();
        dialog.show();
    }

    private void signIn(final String email, final String password) {

        mDatabase.collection(ROOT).document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult() != null && task.getResult().getData() != null) {

                    boolean status = (boolean) task.getResult().get(IS_USER_CURRENTLY_ACTIVE);
                    if(status){
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "This is user is currently active.", Toast.LENGTH_SHORT).show();
                    } else {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful() && mAuth.getCurrentUser() != null) {

                                    Map<String, Object> user = new HashMap<>();
                                    user.put(EMAIL_LOGGIN, mAuth.getCurrentUser().getEmail());
                                    user.put(UID, mAuth.getCurrentUser().getUid());
                                    user.put(IS_USER_CURRENTLY_ACTIVE, true);

                                    mDatabase.collection(ROOT).document(email).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.putExtra(IS_FIRST_CONNECTION, false);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                } else {

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful() && mAuth.getCurrentUser() != null) {

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date netDate = (new Date(System.currentTimeMillis()));
                                String date = sdf.format(netDate);

                                Map<String, Object> user = new HashMap<>();
                                user.put(EMAIL_LOGGIN, mAuth.getCurrentUser().getEmail());
                                user.put(UID, mAuth.getCurrentUser().getUid());
                                user.put(CONNEXION_DATE, date);
                                user.put(IS_USER_CURRENTLY_ACTIVE, true);

                                mDatabase.collection(ROOT).document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.putExtra(IS_FIRST_CONNECTION, true);
                                        intent.putExtra(PASSWORD, password);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }
}
