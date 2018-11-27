package com.jmjsolution.solarup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyFragment extends Fragment {


    @BindView(R.id.emptyFragmentTv) TextView mEmptyTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.empty_fragment, container, false);
        ButterKnife.bind(this, root);

        int cardviewNumber = getArguments().getInt("cardviewNumber");
        if(cardviewNumber == 0){
            mEmptyTv.setText("Vous n'avez pas encore de projet...");
        } else if (cardviewNumber == 1){
            mEmptyTv.setText("Vous n'avez pas de realisations.");
        }

        return root;
    }
}
