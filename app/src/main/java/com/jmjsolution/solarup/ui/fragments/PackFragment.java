package com.jmjsolution.solarup.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.adapters.PackAdapter;
import com.jmjsolution.solarup.interfaces.DeletePack;
import com.jmjsolution.solarup.model.EventDots;
import com.jmjsolution.solarup.model.Materiel;
import com.jmjsolution.solarup.model.Pack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.utils.Constants.Database.MATERIAL_BRANCH;
import static com.jmjsolution.solarup.utils.Constants.Database.PACKS_BRANCH;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;

public class PackFragment extends Fragment implements DeletePack {

    @BindView(R.id.packsRv) RecyclerView mPackRv;
    @BindView(R.id.addPackFab) FloatingActionButton mAddPackFab;
    @BindView(R.id.loadingPackPb) ProgressBar mLoadingPackPb;
    private FirebaseFirestore mDb;
    private FirebaseAuth mFirebaseAuth;
    private ArrayList<Pack> mPacks;
    private PackAdapter mPackAdapter;
    private String mTitlePack;
    private String mIdPack;
    private List<String> mDescPack;
    private int mPricePack;
    private int mPowerPack;
    private int mNbModules;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pack_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingPackPb.setVisibility(View.VISIBLE);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mPacks = new ArrayList<>();
        mPackAdapter = new PackAdapter(getContext(), mPacks, this);

        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail()))
                .collection(PACKS_BRANCH).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot pack : Objects.requireNonNull(task.getResult())){
                        mPacks.add(pack.toObject(Pack.class));
                        mPackAdapter.notifyDataSetChanged();
                        mLoadingPackPb.setVisibility(View.GONE);
                    }
                }
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mPackRv.setLayoutManager(layoutManager);
        mPackRv.setAdapter(mPackAdapter);
        mPackAdapter.notifyDataSetChanged();

        mAddPackFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setterTitlePackDialog();
            }
        });

        return view;
    }

    private void setterTitlePackDialog(){
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.enter_title_label)
                .setView(taskEditText)
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTitlePack = taskEditText.getText().toString();
                        mIdPack = taskEditText.getText().toString() + String.valueOf(System.currentTimeMillis());
                        setterPowerPackDialog();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void setterPowerPackDialog(){
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.power_pack)
                .setView(taskEditText)
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPowerPack = Integer.parseInt(taskEditText.getText().toString());
                        setterNumberModuls();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void setterNumberModuls(){
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.nb_modules_pack)
                .setView(taskEditText)
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNbModules = Integer.parseInt(taskEditText.getText().toString());
                        setterChoiceMaterial();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void setterChoiceMaterial(){
        mDescPack = new ArrayList<>();
        final ArrayList<String> nameMaterialList = new ArrayList<>();
        final ArrayList<Long> priceMaterialList = new ArrayList<>();

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builderSingle.setTitle("Selectionner votre materiel : ");

        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail()))
                .collection(MATERIAL_BRANCH).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot materiel : Objects.requireNonNull(task.getResult())) {
                        nameMaterialList.add((String) materiel.get("nom"));
                        priceMaterialList.add((long) materiel.get("prix"));
                    }

                        String[] nameMaterialArray = new String[nameMaterialList.size()];
                        nameMaterialArray = nameMaterialList.toArray(nameMaterialArray);

                        final boolean[] listBool = new boolean[nameMaterialList.size()];


                        builderSingle.setMultiChoiceItems(nameMaterialArray, listBool, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                listBool[i] = b;
                            }
                        });

                        builderSingle.setNegativeButton("annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                    final String[] finalNameMaterialArray = nameMaterialArray;
                    builderSingle.setPositiveButton("valider", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for(int y = 0; y < listBool.length; y++){
                                    if(listBool[y]){
                                        mDescPack.add(finalNameMaterialArray[y]);
                                        mPricePack += priceMaterialList.get(y);
                                        onCreatePack();
                                    }
                                }
                            }
                        });

                        builderSingle.show();
                    }
                }
        });

    }

    private void onCreatePack(){
        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail()))
                .collection(PACKS_BRANCH).document(mIdPack).set(new Pack(mTitlePack, mDescPack, mPricePack, mIdPack, mNbModules, mPowerPack), SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                ft.detach(PackFragment.this).attach(PackFragment.this).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDeletePack(final int i) {
     mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail()))
     .collection(PACKS_BRANCH).document(mPacks.get(i).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void aVoid) {
             mPacks.remove(i);
             mPackAdapter.notifyDataSetChanged();
         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
         }
     });
    }
}
