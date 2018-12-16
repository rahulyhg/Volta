package com.jmjsolution.solarup.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jmjsolution.solarup.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyFragment extends Fragment {

    @BindView(R.id.emptyFragmentTv) TextView mEmptyTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.empty_fragment, container, false);
        ButterKnife.bind(this, root);

        int cardviewNumber = Objects.requireNonNull(getArguments()).getInt("cardviewNumber");

        if(cardviewNumber == 0 || cardviewNumber == 1){
            mEmptyTv.setText(R.string.no_project_empty_frag);
        }  else if(cardviewNumber == 5){
            mEmptyTv.setText(R.string.associer_compte_empty_frag);
            mEmptyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

        return root;
    }
}
