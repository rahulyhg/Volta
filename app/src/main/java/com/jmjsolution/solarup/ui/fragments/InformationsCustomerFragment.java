package com.jmjsolution.solarup.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.interfaces.UserLocationListener;
import com.jmjsolution.solarup.views.SeekbarWithIntervals;
import com.jmjsolution.solarup.utils.UserLocation;
import com.jmjsolution.solarup.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.utils.Constants.Database.ADDRESS;
import static com.jmjsolution.solarup.utils.Constants.Database.AZIMUTH;
import static com.jmjsolution.solarup.utils.Constants.Database.CHAUFFAGE_EAU_TYPE;
import static com.jmjsolution.solarup.utils.Constants.Database.CHAUFFAGE_TYPE;
import static com.jmjsolution.solarup.utils.Constants.Database.CITY;
import static com.jmjsolution.solarup.utils.Constants.Database.CONSOMMATION_ELECTRIQUE;
import static com.jmjsolution.solarup.utils.Constants.Database.COUT_ELECTRIQUE;
import static com.jmjsolution.solarup.utils.Constants.Database.DATE;
import static com.jmjsolution.solarup.utils.Constants.Database.EMAIL;
import static com.jmjsolution.solarup.utils.Constants.Database.GENRE;
import static com.jmjsolution.solarup.utils.Constants.Database.GRANDEUR_TOIT;
import static com.jmjsolution.solarup.utils.Constants.Database.HAUTEUR_PLAFOND;
import static com.jmjsolution.solarup.utils.Constants.Database.INCLINAISON;
import static com.jmjsolution.solarup.utils.Constants.Database.LATITUDE;
import static com.jmjsolution.solarup.utils.Constants.Database.LONGITUDE;
import static com.jmjsolution.solarup.utils.Constants.Database.NOM;
import static com.jmjsolution.solarup.utils.Constants.Database.PRENOM;
import static com.jmjsolution.solarup.utils.Constants.Database.PROJECTS_BRANCH;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;
import static com.jmjsolution.solarup.utils.Constants.Database.TELEPHONE;

public class InformationsCustomerFragment extends Fragment implements UserLocationListener, View.OnClickListener, OnMapReadyCallback {


    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 6;
    public static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 7;

    private Context mContext;
    private UserLocation myLocation;
    private LatLng mLatLng;
    private SupportMapFragment mMapFragment;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;

    private int mChauffageType = -1;
    private int mCEType = -1;
    private String mAddressName;
    private String mCityName;
    private int mIntervalHauteurPlafond = -1;
    private int mIntervalGrandeurToiture = -1;
    private int mGenre = -1;

    @BindView(R.id.placeCityTv) TextView mPlaceCityTv;
    @BindView(R.id.placeAddressTv) TextView mAddressTv;
    @BindView(R.id.searchAddressBtn) FloatingActionButton mSearchAddressBtn;
    @BindView(R.id.editAddressBtn) FloatingActionButton mEditAddressBtn;
    @BindView(R.id.searchingLocalisationLyt) LinearLayout mSearchingLocaLyt;
    @BindView(R.id.localisationFoundLyt) LinearLayout mLocaFoundLyt;
    @BindView(R.id.localisationButtonsLyt) LinearLayout mLocaButtonsLyt;
    @BindView(R.id.seekBarWithIntervals) SeekbarWithIntervals mSeekbarWithIntervalsPlafond;
    @BindView(R.id.seekBarWithIntervalsToiture) SeekbarWithIntervals mSeekbarWithIntervalsToiture;
    @BindView(R.id.consoElectriqueEt) EditText mConsoElectriqueEt;
    @BindView(R.id.coutElectriciteEt) EditText mCoutElectriciteEt;
    @BindView(R.id.inclinaisonEt) EditText mInclinaisonEt;
    @BindView(R.id.azimuthEt) EditText mAzimuthEt;
    @BindView(R.id.nameEt) EditText mNameEt;
    @BindView(R.id.lastNameEt) EditText mLastNameEt;
    @BindView(R.id.phoneEt) EditText mPhoneEt;
    @BindView(R.id.mailEt) EditText mEmailEt;
    @BindView(R.id.gplChauffageIv) ImageView mGplIv;
    @BindView(R.id.electricChauffageIv) ImageView mElectricIv;
    @BindView(R.id.gazChauffageIv) ImageView mGazIv;
    @BindView(R.id.charbonChauffageIv) ImageView mCharbonIv;
    @BindView(R.id.pacChauffageIv) ImageView mPacIv;
    @BindView(R.id.boisChauffageIv) ImageView mBoisIv;
    @BindView(R.id.fioulChauffageIv) ImageView mFioulIv;
    @BindView(R.id.chauffeEauElectriqueTv) TextView mCEEletriqueTv;
    @BindView(R.id.chauffeEauGazAv89Tv) TextView mCEGazTv;
    @BindView(R.id.chauffeEauThermoTv) TextView mCEThermoTv;
    @BindView(R.id.submitBtn) Button mSubmitBtn;
    @BindView(R.id.mrRbtn) RadioButton mMrBtn;
    @BindView(R.id.mmeRbtn) RadioButton mMmeBtn;
    @BindView(R.id.mlleRbtn) RadioButton mMlleBtn;
    @BindView(R.id.socityBtn) RadioButton mSocityBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.infos_customer_fragment, container, false);
        ButterKnife.bind(this, view);

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mContext = getActivity();
        myLocation = new UserLocation(mContext, this);

        checkPermissions();

        setSeekbarHauteurPlafond();
        setSeekbarGrandeurToiture();

        mSearchAddressBtn.setOnClickListener(this);
        mEditAddressBtn.setOnClickListener(this);
        mGplIv.setOnClickListener(this);
        mElectricIv.setOnClickListener(this);
        mGazIv.setOnClickListener(this);
        mCharbonIv.setOnClickListener(this);
        mPacIv.setOnClickListener(this);
        mBoisIv.setOnClickListener(this);
        mFioulIv.setOnClickListener(this);
        mCEEletriqueTv.setOnClickListener(this);
        mCEGazTv.setOnClickListener(this);
        mCEThermoTv.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
        mMrBtn.setOnClickListener(this);
        mMmeBtn.setOnClickListener(this);
        mMlleBtn.setOnClickListener(this);
        mSocityBtn.setOnClickListener(this);

        return view;

    }

    private void setSeekbarHauteurPlafond() {
        List<String> intervals = new ArrayList<>();
        intervals.add("2m");
        intervals.add("2.5m");
        intervals.add("3m");
        intervals.add("3.5m");
        mSeekbarWithIntervalsPlafond.setIntervals(intervals);
        mSeekbarWithIntervalsPlafond.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mIntervalHauteurPlafond = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setSeekbarGrandeurToiture() {
        List<String> intervals = new ArrayList<>();
        intervals.add("<50");
        intervals.add("50-100");
        intervals.add("100-150");
        intervals.add(">150");
        mSeekbarWithIntervalsToiture.setIntervals(intervals);
        mSeekbarWithIntervalsToiture.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mIntervalGrandeurToiture = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myLocation.fetch();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(mLatLng));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, 16);
        googleMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onLocationFound(Location location) {
        if (location!=null && Utils.isNetworkAvailable(mContext)) {
            fetchLocation(location);
        }
    }

    private void fetchLocation(Location location) {
        mAddressName = Utils.getAddressName(getActivity(), location);
        mCityName = Utils.getCityName(getActivity(), location);

        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMapFragment.getMapAsync(this);

        mSearchingLocaLyt.setVisibility(View.GONE);
        mLocaFoundLyt.setVisibility(View.VISIBLE);
        mLocaButtonsLyt.setVisibility(View.VISIBLE);

        mAddressTv.setText(mAddressName);
        mPlaceCityTv.setText(mCityName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (myLocation != null) {
                    }

                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myLocation.stop();
    }

    @Override
    public void onClick(View view) {
        if(view == mSearchAddressBtn){
            mSearchingLocaLyt.setVisibility(View.VISIBLE);
            mLocaFoundLyt.setVisibility(View.GONE);
            myLocation.fetch();
        }
        if(view == mEditAddressBtn){
            final EditText taskEditText = new EditText(getActivity());
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Entrer une addresse")
                    .setView(taskEditText)
                    .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSearchingLocaLyt.setVisibility(View.VISIBLE);
                            mLocaFoundLyt.setVisibility(View.GONE);
                            Location location = Utils.getPlaceLocation(getActivity(), taskEditText.getText().toString());
                            if(location == null){
                                Toast.makeText(getActivity(), "Aucune addresse trouvée pour cette saisie.", Toast.LENGTH_SHORT).show();
                                myLocation.fetch();
                            } else {
                                fetchLocation(location);
                            }
                        }
                    })
                    .setNegativeButton("Annuler", null)
                    .create();
            dialog.show();
        }
        if(view == mGplIv){
            setChauffageType(mGplIv, mElectricIv, mGazIv, mCharbonIv, mPacIv, mBoisIv, mFioulIv);
            mChauffageType = 0;
        }
        if(view == mElectricIv){
            setChauffageType(mElectricIv, mGplIv, mGazIv, mCharbonIv, mPacIv, mBoisIv, mFioulIv);
            mChauffageType = 1;
        }
        if(view == mGazIv){
            setChauffageType(mGazIv, mElectricIv, mGplIv, mCharbonIv, mPacIv, mBoisIv, mFioulIv);
            mChauffageType = 2;
        }
        if(view == mCharbonIv){
            setChauffageType(mCharbonIv, mElectricIv, mGazIv, mGplIv, mPacIv, mBoisIv, mFioulIv);
            mChauffageType = 3;
        }
        if(view == mPacIv){
            setChauffageType(mPacIv, mElectricIv, mGazIv, mCharbonIv, mGplIv, mBoisIv, mFioulIv);
            mChauffageType = 4;
        }
        if(view == mBoisIv){
            setChauffageType(mBoisIv, mElectricIv, mGazIv, mCharbonIv, mPacIv, mGplIv, mFioulIv);
            mChauffageType = 5;
        }
        if(view == mFioulIv){
            setChauffageType(mFioulIv, mElectricIv, mGazIv, mCharbonIv, mPacIv, mBoisIv, mGplIv);
            mChauffageType = 6;
        }
        if(view == mCEEletriqueTv){
            setChauffeEauType(mCEEletriqueTv, mCEGazTv, mCEThermoTv);
            mCEType = 0;
        }
        if (view == mCEGazTv) {
            setChauffeEauType(mCEGazTv, mCEEletriqueTv , mCEThermoTv);
            mCEType = 1;
        }
        if(view == mCEThermoTv){
            setChauffeEauType(mCEThermoTv, mCEGazTv, mCEEletriqueTv);
            mCEType = 2;
        }
        if (view == mMrBtn) {
            mGenre = 0;
            mLastNameEt.setHint("Prénom");
            mSocityBtn.setChecked(false);
            mMmeBtn.setChecked(false);
            mMlleBtn.setChecked(false);
        }
        if (view == mMmeBtn) {
            mGenre = 1;
            mLastNameEt.setHint("Prénom");
            mSocityBtn.setChecked(false);
            mMrBtn.setChecked(false);
            mMlleBtn.setChecked(false);
        }
        if (view == mMlleBtn) {
            mGenre = 2;
            mLastNameEt.setHint("Prénom");
            mSocityBtn.setChecked(false);
            mMmeBtn.setChecked(false);
            mMrBtn.setChecked(false);
        }
        if(view == mSocityBtn){
            mGenre = 3;
            mLastNameEt.setHint("N° Siret");
            mMrBtn.setChecked(false);
            mMmeBtn.setChecked(false);
            mMlleBtn.setChecked(false);

        }
        if(view == mSubmitBtn){
            if(submitForm()){
                Map<String, Object> infosCustomer = new ArrayMap<>();
                infosCustomer.put(LATITUDE, mLatLng.latitude);
                infosCustomer.put(LONGITUDE, mLatLng.longitude);
                infosCustomer.put(ADDRESS, mAddressName);
                infosCustomer.put(CITY, mCityName);
                infosCustomer.put(CHAUFFAGE_TYPE, mChauffageType);
                infosCustomer.put(CHAUFFAGE_EAU_TYPE, mCEType);
                infosCustomer.put(HAUTEUR_PLAFOND, mIntervalHauteurPlafond);
                infosCustomer.put(GRANDEUR_TOIT, mIntervalGrandeurToiture);
                infosCustomer.put(CONSOMMATION_ELECTRIQUE, mConsoElectriqueEt.getText().toString());
                infosCustomer.put(COUT_ELECTRIQUE, mCoutElectriciteEt.getText().toString());
                infosCustomer.put(INCLINAISON, mInclinaisonEt.getText().toString());
                infosCustomer.put(AZIMUTH, mAzimuthEt.getText().toString());
                infosCustomer.put(PRENOM, mNameEt.getText().toString());
                infosCustomer.put(NOM, mLastNameEt.getText().toString());
                infosCustomer.put(TELEPHONE, mPhoneEt.getText().toString());
                infosCustomer.put(EMAIL, mEmailEt.getText().toString());
                infosCustomer.put(GENRE, mGenre);
                infosCustomer.put(DATE, Calendar.getInstance(Locale.FRANCE).getTimeInMillis());
                mDatabase.collection(ROOT)
                        .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                        .collection(PROJECTS_BRANCH)
                        .document(mNameEt.getText().toString())
                        .set(infosCustomer).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "succes to store info user", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private boolean submitForm(){
        boolean validate = true;

        if(mChauffageType == -1 || mCEType == -1 || mIntervalHauteurPlafond == -1 || mIntervalGrandeurToiture == -1){
            validate = false;
        }

        if (mAddressName == null && mCityName == null && mLatLng == null) {
            validate = false;
        }

        if(TextUtils.isEmpty(mConsoElectriqueEt.getText().toString())){
            mConsoElectriqueEt.setError("Required");
            validate = false;
        }

        if(TextUtils.isEmpty(mCoutElectriciteEt.getText().toString())){
            mCoutElectriciteEt.setError("Required");
            validate = false;
        }

        if(TextUtils.isEmpty(mInclinaisonEt.getText().toString())){
            mInclinaisonEt.setError("Required");
            validate = false;
        }

        if(TextUtils.isEmpty(mAzimuthEt.getText().toString())){
            mAzimuthEt.setError("Required");
            validate = false;
        }

        if(TextUtils.isEmpty(mNameEt.getText().toString())){
            mNameEt.setError("Required");
            validate = false;
        }

        if(TextUtils.isEmpty(mLastNameEt.getText().toString())){
            mLastNameEt.setError("Required");
            validate = false;
        }

        if(TextUtils.isEmpty(mPhoneEt.getText().toString())){
            mPhoneEt.setError("Required");
            validate = false;
        }

        if(TextUtils.isEmpty(mEmailEt.getText().toString())){
            mEmailEt.setError("Required");
            validate = false;
        }



        return validate;
    }

    private void setChauffeEauType(TextView tv, TextView tv1, TextView tv2) {
        tv.setBackground(getResources().getDrawable(R.drawable.rounded_shape_yellow_stroke));
        tv1.setBackground(getResources().getDrawable(R.drawable.rounded_shape_green_stroke));
        tv2.setBackground(getResources().getDrawable(R.drawable.rounded_shape_green_stroke));
    }

    private void setChauffageType(ImageView iv, ImageView iv1, ImageView iv2, ImageView iv3, ImageView iv4, ImageView iv5, ImageView iv6) {
        iv.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
        iv1.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        iv2.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        iv3.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        iv4.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        iv5.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        iv6.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
    }
}
