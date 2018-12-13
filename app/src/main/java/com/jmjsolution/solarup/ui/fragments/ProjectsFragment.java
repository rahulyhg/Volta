package com.jmjsolution.solarup.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.adapters.EventAdapter;
import com.jmjsolution.solarup.adapters.ProjectAdapter;
import com.jmjsolution.solarup.model.InfoCustomerModel;
import com.jmjsolution.solarup.model.Project;
import com.jmjsolution.solarup.utils.CalendarService;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.utils.Constants.Database.PROJECTS_BRANCH;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;

public class ProjectsFragment extends Fragment {

    @BindView(R.id.projectsRv) RecyclerView mRecyclerView;
    private FirebaseFirestore mDatabase;
    private FirebaseAuth mAuth;
    private ProjectAdapter mProjectAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.projects_fragment, container, false);
        ButterKnife.bind(this, view);

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final ArrayList<InfoCustomerModel> infoCustomerModels = new ArrayList<>();
        final ArrayList<Project> projects = new ArrayList<>();
        mProjectAdapter = new ProjectAdapter(getContext(), projects);

        mDatabase.collection(ROOT)
                .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                .collection(PROJECTS_BRANCH)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        infoCustomerModels.add(document.toObject(InfoCustomerModel.class));
                        projects.add(new Project(document.toObject(InfoCustomerModel.class).getNom(), document.toObject(InfoCustomerModel.class).getAddress(),
                                document.toObject(InfoCustomerModel.class).getDateFormatted()));
                        mProjectAdapter.notifyDataSetChanged();
                    }


                } else { }
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mProjectAdapter);
        mProjectAdapter.notifyDataSetChanged();


        return view;
    }
}
