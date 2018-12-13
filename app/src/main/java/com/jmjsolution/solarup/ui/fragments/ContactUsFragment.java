package com.jmjsolution.solarup.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.v2.GMailSender;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.utils.CalendarService.MY_PREFS_NAME;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;
import static com.jmjsolution.solarup.utils.Constants.GMAIL;
import static com.jmjsolution.solarup.utils.Constants.PASSWORD;

public class ContactUsFragment extends Fragment {

    @BindView(R.id.send) Button mSendButton;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_us_fragment, container, false);
        ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        sendEmailFromApp((String) documentSnapshot.get("password"));
                    }
                });
            }
        });

        return view;
    }

    private void sendEmailFromApp(final String password) {
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String gmail = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString(GMAIL, null);

                            GMailSender sender = new GMailSender(gmail, password);
                            sender.sendMail("Test mail", "This mail has been sent from android app along with attachment",
                                    getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString(GMAIL, null),
                                    "zaffransamuelpro@gmail.com");

                        } catch (Exception e) {
                            Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }
        });
    }
}
