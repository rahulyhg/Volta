package com.jmjsolution.solarup.ui.fragments;

import android.content.Context;
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

import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.services.emailService.GmailService;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;

public class ContactUsFragment extends Fragment {

    @BindView(R.id.send) Button mSendButton;
    @BindView(R.id.bodyEmailEt) EditText mBodyEmailEt;
    @BindView(R.id.contactUsPb)ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_us_fragment, container, false);
        ButterKnife.bind(this, view);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(mBodyEmailEt.getText().toString())){
                    mBodyEmailEt.setError("Required");
                } else {
                    GmailService gmailService = new GmailService(getActivity(),
                            mBodyEmailEt.getText().toString(),
                            "jmjsolution26@gmail.com",
                            "Demande Reclamation Client",
                            mProgressBar, Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE));
                    gmailService.initToken();
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}
