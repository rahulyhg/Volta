package com.jmjsolution.solarup.ui.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.interfaces.StepViewUpdated;
import com.jmjsolution.solarup.model.Step;
import com.jmjsolution.solarup.ui.fragments.InformationsCustomerFragment;
import com.jmjsolution.solarup.views.HorizontalStepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProcessActivity extends AppCompatActivity implements StepViewUpdated {

    @BindView(R.id.horizontalStepview) HorizontalStepView mHorizontalStepView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        ButterKnife.bind(this);


        List<Step> stepBeans = new ArrayList<>();
        stepBeans.add(new Step("Etude Technique", Step.State.CURRENT));
        stepBeans.add(new Step("Offres"));
        stepBeans.add(new Step("Résumé", Step.State.NOT_COMPLETED));
        stepBeans.add(new Step("Eligibilité", Step.State.NOT_COMPLETED));
        stepBeans.add(new Step("Photos", Step.State.NOT_COMPLETED));
        stepBeans.add(new Step("Finalisation", Step.State.NOT_COMPLETED));
        mHorizontalStepView
                .setSteps(stepBeans)
                .setCompletedStepTextColor(getResources().getColor(android.R.color.black))
                .setNotCompletedStepTextColor(getResources().getColor(android.R.color.white))
                .setCurrentStepTextColor(getResources().getColor(R.color.colorAccent))
                .setCompletedLineColor(getResources().getColor(android.R.color.white))
                .setNotCompletedLineColor(getResources().getColor(android.R.color.white))
                .setTextSize(14)
                .setCircleRadius(14)
                .setLineLength(68);

        startInfoCustomerFrag();
    }

    private void startInfoCustomerFrag() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        InformationsCustomerFragment informationsCustomerFragment = new InformationsCustomerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", 2);
        informationsCustomerFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.processFrameLayout, informationsCustomerFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onStepViewUpdated(int positionFrag) {
        mHorizontalStepView.setStepState(Step.State.COMPLETED, positionFrag);
        mHorizontalStepView.setStepState(Step.State.CURRENT, positionFrag + 1);
    }
}
