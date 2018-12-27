package com.jmjsolution.solarup.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmjsolution.solarup.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.utils.Constants.Database.BALLON_THERMODYNAMIQUE;
import static com.jmjsolution.solarup.utils.Constants.Database.CONFIGURATION;
import static com.jmjsolution.solarup.utils.Constants.Database.INSTALLATION_TYPE;
import static com.jmjsolution.solarup.utils.Constants.Database.PHOTOVOLTAIQUE;
import static com.jmjsolution.solarup.utils.Constants.Database.POMPE_A_CHALEUR;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;

public class TypeInstallationFragment extends Fragment {

    FirebaseFirestore mDb;
    FirebaseAuth mAuth;
    @BindView(R.id.thermoCb) CheckBox mBallonThermoCheckBox;
    @BindView(R.id.pacCb) CheckBox mPacCheckBox;
    @BindView(R.id.pvCb) CheckBox mPvCheckBox;
    @BindView(R.id.loadingInstallationType) ProgressBar mLoadingInstallationTypePb;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.installation_type_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingInstallationTypePb.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                .collection(CONFIGURATION).document(INSTALLATION_TYPE).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                boolean isPvPresent = (boolean) Objects.requireNonNull(task.getResult()).get(PHOTOVOLTAIQUE);
                boolean isPacPresent = (boolean) Objects.requireNonNull(task.getResult()).get(POMPE_A_CHALEUR);
                boolean isBallonThermoPresent = (boolean) Objects.requireNonNull(task.getResult()).get(BALLON_THERMODYNAMIQUE);

                if(isPvPresent){
                    mPvCheckBox.setChecked(true);
                }
                if(isBallonThermoPresent){
                    mBallonThermoCheckBox.setChecked(true);
                }
                if(isPacPresent){
                    mPacCheckBox.setChecked(true);
                }

                mLoadingInstallationTypePb.setVisibility(View.GONE);
            }
        });

        mBallonThermoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateInstallationType("bt", b);
                mLoadingInstallationTypePb.setVisibility(View.VISIBLE);
            }
        });

        mPacCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateInstallationType("pac", b);
                mLoadingInstallationTypePb.setVisibility(View.VISIBLE);
            }
        });

        mPvCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateInstallationType("pv", b);
                mLoadingInstallationTypePb.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void updateInstallationType(String name, boolean b){
        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                .collection(CONFIGURATION).document(INSTALLATION_TYPE).update(name, b).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoadingInstallationTypePb.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoadingInstallationTypePb.setVisibility(View.GONE);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
