package com.jmjsolution.solarup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    @BindView(R.id.identifiantTv) EditText mIdentifiantTv;
    @BindView(R.id.passwordTv) EditText mPasswordTv;
    @BindView(R.id.signInBtn) Button mSignInBtn;
    @BindView(R.id.progressBarLogin) ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

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

    }

    private void signIn(final String email, final String password) {

        mDatabase.collection("users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult() != null && task.getResult().getData() != null) {
                    boolean keyExists = task.getResult().getData().containsKey("user_is_enabled");
                    if(keyExists) {
                        long status = (long) task.getResult().get("user_is_enabled");
                        if(status == 1){
                            Toast.makeText(LoginActivity.this, "This is user is currently active.", Toast.LENGTH_SHORT).show();
                        } else if(status == 2){

                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {

                                        Map<String, Object> user = new HashMap<>();
                                        user.put("email", mAuth.getCurrentUser().getEmail());
                                        user.put("uid", mAuth.getCurrentUser().getUid());
                                        user.put("user_is_enabled", 1);

                                        mDatabase.collection("users").document(email).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mProgressBar.setVisibility(View.INVISIBLE);
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.putExtra("email", email);
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
                    }

                } else {

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful() && mAuth.getCurrentUser() != null) {

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date netDate = (new Date(System.currentTimeMillis()));
                                String date = sdf.format(netDate);

                                Map<String, Object> user = new HashMap<>();
                                user.put("email", mAuth.getCurrentUser().getEmail());
                                user.put("uid", mAuth.getCurrentUser().getUid());
                                user.put("date_connexion", date);
                                user.put("user_is_enabled", 1);

                                mDatabase.collection("users").document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.putExtra("email", email);
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

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Veuillez vous identifiez.", Toast.LENGTH_SHORT).show();
        }
    }
}
