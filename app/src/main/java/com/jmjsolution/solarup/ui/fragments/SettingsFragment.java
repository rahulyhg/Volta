package com.jmjsolution.solarup.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.AccountPicker;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.services.calendarService.CalendarService;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.utils.Constants.GMAIL;
import static com.jmjsolution.solarup.utils.Constants.IS_EMAIL_LINKED;
import static com.jmjsolution.solarup.utils.Constants.IS_PASSWORD_STORED;

public class SettingsFragment extends Fragment {

    public static final int ACCOUNT_CHOOSER = 12;
    @BindView(R.id.gmailLyt) LinearLayout mGmailLyt;
    @BindView(R.id.settingSubmitBtn) Button mSubmitBtn;
    @BindView(R.id.emailProTv) TextView mEmailProTv;
    @BindView(R.id.gmailIv) ImageView mGmailIv;
    private SharedPreferences mSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, view);

        mSharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(CalendarService.MY_PREFS_NAME, Context.MODE_PRIVATE);
        if (mSharedPreferences.getBoolean(IS_EMAIL_LINKED, false)) {
            mEmailProTv.setText(mSharedPreferences.getString(GMAIL, null));
            mGmailLyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(IS_EMAIL_LINKED, false).apply();
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google", "com.google.android.legacyimap"}, false, null, null, null, null);
                    Objects.requireNonNull(getActivity()).startActivityForResult(intent, ACCOUNT_CHOOSER);
                }
            });
        } else {
            mGmailLyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(IS_EMAIL_LINKED, false).apply();
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google", "com.google.android.legacyimap"}, false, null, null, null, null);
                    Objects.requireNonNull(getActivity()).startActivityForResult(intent, ACCOUNT_CHOOSER);
                }
            });

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(CalendarService.MY_PREFS_NAME, Context.MODE_PRIVATE);
        if(mSharedPreferences.getBoolean(IS_EMAIL_LINKED, false) && mSharedPreferences.getBoolean(IS_PASSWORD_STORED, false)){
            mGmailIv.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        } else {
            mGmailIv.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
    }
}
