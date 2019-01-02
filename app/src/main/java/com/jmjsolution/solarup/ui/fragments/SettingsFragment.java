package com.jmjsolution.solarup.ui.fragments;

import android.accounts.AccountManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.AccountPicker;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.services.calendarService.CalendarService;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.jmjsolution.solarup.utils.Constants.ACCOUNT_TYPE;
import static com.jmjsolution.solarup.utils.Constants.GMAIL;
import static com.jmjsolution.solarup.utils.Constants.IS_EMAIL_LINKED;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_CODE_PICK_ACCOUNT = 307;
    @BindView(R.id.gmailLyt) LinearLayout mGmailLyt;
    @BindView(R.id.emailProTv) TextView mEmailProTv;
    @BindView(R.id.gmailIv) ImageView mGmailIv;
    @BindView(R.id.packFragLyt) LinearLayout mPackFragLyt;
    @BindView(R.id.materialFragLyt) LinearLayout mMaterialFragLyt;
    @BindView(R.id.installationTypeLyt) LinearLayout mInstallationTypeLyt;
    @BindView(R.id.notifLyt) LinearLayout mNotifLyt;

    private SharedPreferences mSharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, view);

        mSharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(CalendarService.MY_PREFS_NAME, Context.MODE_PRIVATE);
        configureGmailAccount();

        return view;
    }

    private void configureGmailAccount() {
        if (mSharedPref.getBoolean(IS_EMAIL_LINKED, false)) {
            mEmailProTv.setText(mSharedPref.getString(GMAIL, null));
            mGmailLyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchAccountPicker();
                }
            });
        } else {
            mGmailLyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchAccountPicker();
                }
            });

        }
    }

    private void launchAccountPicker() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                null, false, null, null, null, null);
        SettingsFragment.this.startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SettingsFragment.REQUEST_CODE_PICK_ACCOUNT && resultCode == RESULT_OK) {
            String mEmail = Objects.requireNonNull(data).getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            String mType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
            mSharedPref.edit().putString(GMAIL, mEmail).apply();
            mSharedPref.edit().putString(ACCOUNT_TYPE, mType).apply();
            mSharedPref.edit().putBoolean(IS_EMAIL_LINKED, true).apply();
            Objects.requireNonNull(getFragmentManager()).beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(CalendarService.MY_PREFS_NAME, Context.MODE_PRIVATE);
        if(mSharedPref.getBoolean(IS_EMAIL_LINKED, false)){
            mGmailIv.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        } else {
            mGmailIv.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == mPackFragLyt){
            launchFrag(new PackFragment());
        }
        if(view == mMaterialFragLyt){
            launchFrag(new MaterialFragment());
        }
        if(view == mInstallationTypeLyt){
            launchFrag(new TypeInstallationFragment());
        }
        if(view == mNotifLyt){
            launchFrag(new ReminderMailSettingFragment());
        }
    }

    private void launchFrag(Fragment fragment){
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameLayout, fragment)
                .commit();
    }
}
