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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.adapters.MaterielAdapter;
import com.jmjsolution.solarup.interfaces.DeleteMaterial;
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

public class MaterialFragment extends Fragment implements DeleteMaterial {


    @BindView(R.id.materialsRv) RecyclerView mMaterialRv;
    @BindView(R.id.addMaterialFab) FloatingActionButton mAddMaterialFab;
    @BindView(R.id.loadingMaterialPb) ProgressBar mLoadingMaterialPb;
    private FirebaseFirestore mDb;
    private FirebaseAuth mFirebaseAuth;
    private MaterielAdapter mMaterielAdapter;
    private ArrayList<Materiel> mMateriels;
    private String mTitleMaterial;
    private String mIdMaterial;
    private String mPriceMaterial;
    private List<String> mDesc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.material_fragment, container, false);
        ButterKnife.bind(this, view);

        mDesc = new ArrayList<>();
        mLoadingMaterialPb.setVisibility(View.VISIBLE);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mMateriels = new ArrayList<>();
        mMaterielAdapter = new MaterielAdapter(getContext(), mMateriels, this);


        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail()))
                .collection(MATERIAL_BRANCH).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot materiel : Objects.requireNonNull(task.getResult())){
                        mMateriels.add(materiel.toObject(Materiel.class));
                        mMaterielAdapter.notifyDataSetChanged();
                        mLoadingMaterialPb.setVisibility(View.GONE);
                    }
                }
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mMaterialRv.setLayoutManager(layoutManager);
        mMaterialRv.setAdapter(mMaterielAdapter);
        mMaterielAdapter.notifyDataSetChanged();

        mAddMaterialFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setterTitleMaterialDialog();
            }
        });

        return view;
    }

    private void setterTitleMaterialDialog(){
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.enter_title_label)
                .setView(taskEditText)
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTitleMaterial = taskEditText.getText().toString();
                        mIdMaterial = taskEditText.getText().toString() + String.valueOf(System.currentTimeMillis());
                        setterPriceMaterialDialog();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }


    private void setterPriceMaterialDialog(){
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.enter_price_label)
                .setView(taskEditText)
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPriceMaterial = taskEditText.getText().toString();
                        setterDescMaterialDialog();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void setterDescMaterialDialog(){
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.enter_price_label)
                .setView(taskEditText)
                .setPositiveButton(R.string.add_more, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDesc.add(taskEditText.getText().toString());
                        setterDescMaterialDialog();
                    }
                })
                .setNegativeButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDesc.add(taskEditText.getText().toString());
                        onCreateMaterial();
                    }
                })
                .create();
        dialog.show();
    }

    private void onCreateMaterial(){
        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail()))
                .collection(MATERIAL_BRANCH).document(mIdMaterial).set(new Materiel(mTitleMaterial, null, Integer.parseInt(mPriceMaterial), mIdMaterial, mDesc), SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                ft.detach(MaterialFragment.this).attach(MaterialFragment.this).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteMaterial(final int position) {
        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail()))
                .collection(MATERIAL_BRANCH).document(mMateriels.get(position).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mMateriels.remove(position);
                mMaterielAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
