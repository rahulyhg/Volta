package com.jmjsolution.solarup.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.interfaces.StepViewUpdated;
import com.jmjsolution.solarup.interfaces.UserLocationListener;
import com.jmjsolution.solarup.utils.BitmapFromViewHelper;
import com.jmjsolution.solarup.views.SeekbarWithIntervals;
import com.jmjsolution.solarup.utils.UserLocation;
import com.jmjsolution.solarup.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;
import static com.jmjsolution.solarup.utils.Constants.BOIS;
import static com.jmjsolution.solarup.utils.Constants.CHARBON;
import static com.jmjsolution.solarup.utils.Constants.Database.ADDRESS;
import static com.jmjsolution.solarup.utils.Constants.Database.AZIMUTH;
import static com.jmjsolution.solarup.utils.Constants.Database.BALLON_THERMODYNAMIQUE;
import static com.jmjsolution.solarup.utils.Constants.Database.CHAUFFAGE_EAU_TYPE;
import static com.jmjsolution.solarup.utils.Constants.Database.CHAUFFAGE_TYPE;
import static com.jmjsolution.solarup.utils.Constants.Database.CITY;
import static com.jmjsolution.solarup.utils.Constants.Database.CONFIGURATION;
import static com.jmjsolution.solarup.utils.Constants.Database.CONSOMMATION_ELECTRIQUE;
import static com.jmjsolution.solarup.utils.Constants.Database.COUT_ELECTRIQUE;
import static com.jmjsolution.solarup.utils.Constants.Database.DATE;
import static com.jmjsolution.solarup.utils.Constants.Database.EMAIL;
import static com.jmjsolution.solarup.utils.Constants.Database.GENRE;
import static com.jmjsolution.solarup.utils.Constants.Database.GRANDEUR_TOIT;
import static com.jmjsolution.solarup.utils.Constants.Database.HAUTEUR_PLAFOND;
import static com.jmjsolution.solarup.utils.Constants.Database.INCLINAISON;
import static com.jmjsolution.solarup.utils.Constants.Database.INSTALLATION_TYPE;
import static com.jmjsolution.solarup.utils.Constants.Database.LATITUDE;
import static com.jmjsolution.solarup.utils.Constants.Database.LONGITUDE;
import static com.jmjsolution.solarup.utils.Constants.Database.NOM;
import static com.jmjsolution.solarup.utils.Constants.Database.PERGOLA_CHOICE;
import static com.jmjsolution.solarup.utils.Constants.Database.PERGOLA_FIXATION;
import static com.jmjsolution.solarup.utils.Constants.Database.PERSPECTIVE;
import static com.jmjsolution.solarup.utils.Constants.Database.PHOTOVOLTAIQUE;
import static com.jmjsolution.solarup.utils.Constants.Database.POMPE_A_CHALEUR;
import static com.jmjsolution.solarup.utils.Constants.Database.PRENOM;
import static com.jmjsolution.solarup.utils.Constants.Database.PROJECTS_BRANCH;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;
import static com.jmjsolution.solarup.utils.Constants.Database.TELEPHONE;
import static com.jmjsolution.solarup.utils.Constants.ELECTRIQUE;
import static com.jmjsolution.solarup.utils.Constants.FIOUL;
import static com.jmjsolution.solarup.utils.Constants.GAZ;
import static com.jmjsolution.solarup.utils.Constants.GPL;
import static com.jmjsolution.solarup.utils.Constants.PAC;
import static com.jmjsolution.solarup.utils.Constants.THERMODYNAMIQUE;

public class InformationsCustomerFragment extends Fragment implements UserLocationListener, View.OnClickListener, OnMapReadyCallback {


    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 6;
    public static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 7;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 8;

    private Context mContext;
    private UserLocation myLocation;
    private LatLng mLatLng;
    private SupportMapFragment mMapFragment;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    private StepViewUpdated mStepViewUpdated;
    private boolean mIsPvPresent;
    private boolean mIsPacPresent;
    private boolean mIsBtPresent;

    private String mChauffageType = null;
    private String mCEType = null;
    private String mAddressName;
    private String mCityName;
    private int mIntervalHauteurPlafond = -1;
    private int mIntervalGrandeurToiture = -1;
    private int mGenre = -1;
    private int mPergolaChoice = -1;
    private int mFixationChoice = -1;
    private int mPerspective = -1;

    @BindView(R.id.llScroll) LinearLayout llScroll;

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
    @BindView(R.id.fixationDeuxRb) RadioButton mFixationDeuxPiedsBtn;
    @BindView(R.id.fixationQuatreRb) RadioButton mFixationQuatrePiedsBtn;
    @BindView(R.id.augmenterPouvoirAchatTv) TextView mAugmentationPvrAchatTv;
    @BindView(R.id.transfertChargesTV) TextView mTransfertChargesTv;
    @BindView(R.id.independantTv) TextView mIndependantTv;
    @BindView(R.id.pergola8) TextView mPergola8Tv;
    @BindView(R.id.pergola16) TextView mPergola16Tv;
    @BindView(R.id.pergola18) TextView mPergola18Tv;
    @BindView(R.id.pergola24) TextView mPergola24Tv;
    @BindView(R.id.surfaceToitureLabel) TextView mSurfaceToitureLabel;
    @BindView(R.id.inclinaisonAzimuthLyt) LinearLayout mInclinaisonAzimuthLyt;
    @BindView(R.id.pergolaLabel) TextView mPergolaLabel;
    @BindView(R.id.pergolaCv) CardView mPergolaCv;
    @BindView(R.id.surfaceToitureCv) CardView mSurfaceToitureCv;
    @BindView(R.id.hauteurSousPlafondLabel) TextView mHauteurSousPlafondLabel;
    @BindView(R.id.hauteurSousPlafondCv) CardView mHauteurSousPlafondCv;
    @BindView(R.id.typeChauffageLabel) TextView mTypeChauffageLabel;
    @BindView(R.id.typeChauffageCv) CardView mTypeChauffageCv;
    @BindView(R.id.eauChaudeSanitaireLabel) TextView mEauChaudeSanitaireLabel;
    @BindView(R.id.eauChaudeSanitaireCv) CardView mEauChaudeSanitaireCv;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.infos_customer_fragment, container, false);
        ButterKnife.bind(this, view);

        setViews();

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
        mFixationDeuxPiedsBtn.setOnClickListener(this);
        mFixationQuatrePiedsBtn.setOnClickListener(this);
        mAugmentationPvrAchatTv.setOnClickListener(this);
        mTransfertChargesTv.setOnClickListener(this);
        mIndependantTv.setOnClickListener(this);
        mPergola8Tv.setOnClickListener(this);
        mPergola16Tv.setOnClickListener(this);
        mPergola18Tv.setOnClickListener(this);
        mPergola24Tv.setOnClickListener(this);

        return view;

    }

    private void setViews() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db.collection(ROOT)
                .document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()))
                .collection(CONFIGURATION)
                .document(INSTALLATION_TYPE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        mIsPvPresent = (boolean) Objects.requireNonNull(task.getResult()).get(PHOTOVOLTAIQUE);
                        mIsPacPresent = (boolean) Objects.requireNonNull(task.getResult()).get(POMPE_A_CHALEUR);
                        mIsBtPresent = (boolean) Objects.requireNonNull(task.getResult()).get(BALLON_THERMODYNAMIQUE);

                        if(!mIsPvPresent){
                            mSurfaceToitureLabel.setVisibility(View.GONE);
                            mInclinaisonAzimuthLyt.setVisibility(View.GONE);
                            mPergolaLabel.setVisibility(View.GONE);
                            mPergolaCv.setVisibility(View.GONE);
                            mSurfaceToitureCv.setVisibility(View.GONE);
                        }

                        if(!mIsPacPresent && !mIsBtPresent){
                            mHauteurSousPlafondLabel.setVisibility(View.GONE);
                            mHauteurSousPlafondCv.setVisibility(View.GONE);
                            mTypeChauffageLabel.setVisibility(View.GONE);
                            mTypeChauffageCv.setVisibility(View.GONE);
                            mEauChaudeSanitaireLabel.setVisibility(View.GONE);
                            mEauChaudeSanitaireCv.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mStepViewUpdated = (StepViewUpdated) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
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
            mChauffageType = GPL;
        }
        if(view == mElectricIv){
            setChauffageType(mElectricIv, mGplIv, mGazIv, mCharbonIv, mPacIv, mBoisIv, mFioulIv);
            mChauffageType = ELECTRIQUE;
        }
        if(view == mGazIv){
            setChauffageType(mGazIv, mElectricIv, mGplIv, mCharbonIv, mPacIv, mBoisIv, mFioulIv);
            mChauffageType = GAZ;
        }
        if(view == mCharbonIv){
            setChauffageType(mCharbonIv, mElectricIv, mGazIv, mGplIv, mPacIv, mBoisIv, mFioulIv);
            mChauffageType = CHARBON;
        }
        if(view == mPacIv){
            setChauffageType(mPacIv, mElectricIv, mGazIv, mCharbonIv, mGplIv, mBoisIv, mFioulIv);
            mChauffageType = PAC;
        }
        if(view == mBoisIv){
            setChauffageType(mBoisIv, mElectricIv, mGazIv, mCharbonIv, mPacIv, mGplIv, mFioulIv);
            mChauffageType = BOIS;
        }
        if(view == mFioulIv){
            setChauffageType(mFioulIv, mElectricIv, mGazIv, mCharbonIv, mPacIv, mBoisIv, mGplIv);
            mChauffageType = FIOUL;
        }
        if(view == mCEEletriqueTv){
            setChauffeEauType(mCEEletriqueTv, mCEGazTv, mCEThermoTv);
            mCEType = ELECTRIQUE;
        }
        if (view == mCEGazTv) {
            setChauffeEauType(mCEGazTv, mCEEletriqueTv , mCEThermoTv);
            mCEType = GAZ;
        }
        if(view == mCEThermoTv){
            setChauffeEauType(mCEThermoTv, mCEGazTv, mCEEletriqueTv);
            mCEType = THERMODYNAMIQUE;
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
        if(view == mFixationDeuxPiedsBtn){
            mFixationChoice = 2;
            mFixationQuatrePiedsBtn.setChecked(false);
        }
        if(view == mFixationQuatrePiedsBtn){
            mFixationChoice = 4;
            mFixationDeuxPiedsBtn.setChecked(false);
        }
        if(view == mAugmentationPvrAchatTv){
            mPerspective = 0;
            setPerspective(mAugmentationPvrAchatTv, mTransfertChargesTv, mIndependantTv);
        }
        if(view == mTransfertChargesTv){
            mPerspective = 1;
            setPerspective(mTransfertChargesTv, mAugmentationPvrAchatTv, mIndependantTv);
        }
        if(view == mIndependantTv){
            mPerspective = 2;
            setPerspective(mIndependantTv, mAugmentationPvrAchatTv, mTransfertChargesTv);
        }
        if(view == mPergola8Tv){
            mPergolaChoice = 0;
            setPergola(mPergola8Tv, mPergola16Tv, mPergola18Tv, mPergola24Tv);
        }
        if(view == mPergola16Tv){
            mPergolaChoice = 1;
            setPergola(mPergola16Tv, mPergola8Tv, mPergola18Tv, mPergola24Tv);
        }
        if(view == mPergola18Tv){
            mPergolaChoice = 2;
            setPergola(mPergola18Tv, mPergola8Tv, mPergola16Tv, mPergola24Tv);
        }
        if(view == mPergola24Tv){
            mPergolaChoice = 3;
            setPergola(mPergola24Tv, mPergola8Tv, mPergola16Tv, mPergola18Tv);
        }
        if(view == mSubmitBtn){

            if(submitForm()){
                Map<String, Object> infosCustomer = new ArrayMap<>();
                if(mIsPvPresent){
                    infosCustomer.put(GRANDEUR_TOIT, mIntervalGrandeurToiture);
                    infosCustomer.put(PERGOLA_CHOICE, mPergolaChoice);
                    infosCustomer.put(PERGOLA_FIXATION, mFixationChoice);
                    infosCustomer.put(INCLINAISON, mInclinaisonEt.getText().toString());
                    infosCustomer.put(AZIMUTH, mAzimuthEt.getText().toString());
                }
                infosCustomer.put(LATITUDE, mLatLng.latitude);
                infosCustomer.put(LONGITUDE, mLatLng.longitude);
                infosCustomer.put(ADDRESS, mAddressName);
                infosCustomer.put(CITY, mCityName);
                infosCustomer.put(CHAUFFAGE_TYPE, mChauffageType);
                infosCustomer.put(CHAUFFAGE_EAU_TYPE, mCEType);
                infosCustomer.put(HAUTEUR_PLAFOND, mIntervalHauteurPlafond);
                infosCustomer.put(CONSOMMATION_ELECTRIQUE, mConsoElectriqueEt.getText().toString());
                infosCustomer.put(COUT_ELECTRIQUE, mCoutElectriciteEt.getText().toString());
                infosCustomer.put(PRENOM, mNameEt.getText().toString());
                infosCustomer.put(NOM, mLastNameEt.getText().toString());
                infosCustomer.put(TELEPHONE, mPhoneEt.getText().toString());
                infosCustomer.put(EMAIL, mEmailEt.getText().toString());
                infosCustomer.put(GENRE, mGenre);
                infosCustomer.put(PERSPECTIVE, mPerspective);
                infosCustomer.put(DATE, Calendar.getInstance(Locale.FRANCE).getTimeInMillis());
                mDatabase.collection(ROOT)
                        .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                        .collection(PROJECTS_BRANCH)
                        .document(mNameEt.getText().toString())
                        .set(infosCustomer).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "succes to store info user", Toast.LENGTH_SHORT).show();
                        mStepViewUpdated.onStepViewUpdated(0);
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

        if(mIsBtPresent || mIsPacPresent){
            if(mIntervalHauteurPlafond == -1){
                validate = false;
            }

            if(mChauffageType == null || mCEType == null){
                validate = false;
            }
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
        tv1.setBackground(getResources().getDrawable(R.drawable.rounded_shape_black_stroke));
        tv2.setBackground(getResources().getDrawable(R.drawable.rounded_shape_black_stroke));
    }

    private void setPerspective(TextView tv, TextView tv1, TextView tv2) {
        tv.setBackground(getResources().getDrawable(R.drawable.rounded_shape_yellow_stroke));
        tv1.setBackground(getResources().getDrawable(R.drawable.rounded_shape_black_stroke));
        tv2.setBackground(getResources().getDrawable(R.drawable.rounded_shape_black_stroke));
    }

    private void setPergola(TextView tv, TextView tv1, TextView tv2, TextView tv3) {
        tv.setBackground(getResources().getDrawable(R.drawable.rounded_shape_yellow_stroke));
        tv1.setBackground(getResources().getDrawable(R.drawable.rounded_shape_black_stroke));
        tv2.setBackground(getResources().getDrawable(R.drawable.rounded_shape_black_stroke));
        tv3.setBackground(getResources().getDrawable(R.drawable.rounded_shape_black_stroke));
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
