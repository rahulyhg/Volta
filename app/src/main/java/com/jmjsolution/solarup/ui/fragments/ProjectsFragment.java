package com.jmjsolution.solarup.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.adapters.ProjectAdapter;
import com.jmjsolution.solarup.model.InfoCustomerModel;
import com.jmjsolution.solarup.model.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.utils.Constants.Database.PROJECTS_BRANCH;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;

public class ProjectsFragment extends Fragment {

    @BindView(R.id.projectsRv) RecyclerView mRecyclerView;
    @BindView(R.id.loadingProjectsPb) ProgressBar mLoadingProjectsPb;
    private ProjectAdapter mProjectAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.projects_fragment, container, false);
        ButterKnife.bind(this, view);

        mLoadingProjectsPb.setVisibility(View.VISIBLE);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        final ArrayList<InfoCustomerModel> infoCustomerModels = new ArrayList<>();
        final ArrayList<Project> projects = new ArrayList<>();
        mProjectAdapter = new ProjectAdapter(getContext(), projects);

        database.collection(ROOT)
                .document(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()))
                .collection(PROJECTS_BRANCH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if(task.getResult() != null){
                            infoCustomerModels.add(document.toObject(InfoCustomerModel.class));
                            projects.add(new Project(
                                    document.toObject(InfoCustomerModel.class).getNom(),
                                    document.toObject(InfoCustomerModel.class).getAddress(),
                                    document.toObject(InfoCustomerModel.class).getDateFormatted(),
                                    document.toObject(InfoCustomerModel.class).getBigDateFormatted(),
                                    document.toObject(InfoCustomerModel.class).getDate()));
                            Collections.sort(projects, new Comparator<Project>() {
                                @Override
                                public int compare(Project project, Project t1) {
                                    return project.getLongDate().compareTo(t1.getLongDate());
                                }
                            });
                            mProjectAdapter.notifyDataSetChanged();
                            mLoadingProjectsPb.setVisibility(View.GONE);
                        } else {
                            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frameLayout, new EmptyFragment())
                                    .commit();
                        }
                    }

                } else {
                    Toast.makeText(getContext(), "Erreur, veuillez reessayer.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mProjectAdapter);
        mProjectAdapter.notifyDataSetChanged();


        return view;
    }
}
