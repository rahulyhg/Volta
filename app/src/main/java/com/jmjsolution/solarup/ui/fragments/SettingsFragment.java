package com.jmjsolution.solarup.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.AccountPicker;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.utils.CalendarService;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    public static final int ACCOUNT_CHOOSER = 12;
    @BindView(R.id.emailProEt) EditText mEmailEt;
    @BindView(R.id.settingSubmitBtn) Button mSubmitBtn;
    @BindView(R.id.emailProTv) TextView mEmailProTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, view);

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(CalendarService.MY_PREFS_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isEmailConfigured", false)) {
            mEmailEt.setVisibility(View.GONE);
            mEmailProTv.setVisibility(View.VISIBLE);
            mEmailProTv.setText(sharedPreferences.getString("email", null));
            mEmailProTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isEmailConfigured", false).apply();
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google", "com.google.android.legacyimap"}, false, null, null, null, null);
                    Objects.requireNonNull(getActivity()).startActivityForResult(intent, ACCOUNT_CHOOSER);
                }
            });
        } else {

            mEmailEt.setVisibility(View.VISIBLE);
            mEmailProTv.setVisibility(View.GONE);
            mEmailEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isEmailConfigured", false).apply();
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google", "com.google.android.legacyimap"}, false, null, null, null, null);
                    Objects.requireNonNull(getActivity()).startActivityForResult(intent, ACCOUNT_CHOOSER);
                }
            });

        }

        /*mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmailEt.getText() != null) {
                    if (CalendarService.determineCalendar(getContext(), mEmailEt.getText().toString())) {
                        mEmailEt.setVisibility(View.GONE);
                        mEmailProTv.setVisibility(View.VISIBLE);
                        mEmailProTv.setText(sharedPreferences.getString("email", null));
                    }
                }
            }
        });*/

        return view;
    }
}
