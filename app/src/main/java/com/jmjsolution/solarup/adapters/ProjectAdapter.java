package com.jmjsolution.solarup.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.model.Project;

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private Context mContext;
    private ArrayList<Project> mProjects;

    public ProjectAdapter(Context context, ArrayList<Project> projects){
        mContext = context;
        mProjects = projects;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item, viewGroup, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder projectViewHolder, int i) {
        projectViewHolder.bindProject(i);
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLinearLayout;
        private TextView mTitleTv, mDateTv, mAddressTv, mBigDateTv;
        private ImageView mDeleteEventIv, mConfirmedIv;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);

            mLinearLayout = itemView.findViewById(R.id.infosEventLyt);
            mBigDateTv = itemView.findViewById(R.id.dateTextView);
            mTitleTv = itemView.findViewById(R.id.titleEventItem);
            mDateTv = itemView.findViewById(R.id.dateEventItem);
            mAddressTv = itemView.findViewById(R.id.addressEventItem);
            mDeleteEventIv = itemView.findViewById(R.id.deleteEventIv);
            mConfirmedIv = itemView.findViewById(R.id.confirmedDateImageView);
        }

        public void bindProject(int i) {
            mTitleTv.setText(mProjects.get(i).getTitle());
            mAddressTv.setText(mProjects.get(i).getLocation());
            mBigDateTv.setText(mProjects.get(i).getBigDate());
            mDateTv.setText(mProjects.get(i).getDate());
        }
    }
}
