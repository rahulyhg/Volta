package com.jmjsolution.solarup.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.services.emailService.GMailSender;
import com.jmjsolution.solarup.ui.activities.HomeActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;
import static com.jmjsolution.solarup.utils.Constants.GMAIL;
import static com.jmjsolution.solarup.utils.Constants.PASSWORD;

public class ContactUsFragment extends Fragment {

    @BindView(R.id.send) Button mSendButton;
    @BindView(R.id.bodyEmailEt) EditText mBodyEmailEt;
    @BindView(R.id.contactUsPb)ProgressBar mProgressBar;
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
                if(TextUtils.isEmpty(mBodyEmailEt.getText().toString())){
                    mBodyEmailEt.setError("Required");
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mDatabase
                            .collection(ROOT)
                            .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.get(PASSWORD) != null) {
                                sendEmailFromApp((String) documentSnapshot.get(PASSWORD), mBodyEmailEt.getText().toString());
                            } else {
                                Toast.makeText(getActivity(), "Veuillez configurer correctement votre compte gmail.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    private void sendEmailFromApp(final String password, final String body) {
         new Thread(new Runnable() {
                    public void run() {
                        try {
                            String gmail = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString(GMAIL, null);
                            GMailSender sender = new GMailSender(gmail, password);
                            sender.sendMail(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), body, getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString(GMAIL, null), "jmjsolution26@gmail.com");
                            startActivity(new Intent(getActivity(), HomeActivity.class));

                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Email envoyé.", Toast.LENGTH_LONG).show();
                                }
                            });

                        } catch (final Exception e) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
}
