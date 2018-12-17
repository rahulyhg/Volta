package com.jmjsolution.solarup.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.model.Step;

import java.util.List;

public class VerticalStepView extends StepView implements StepViewIndicator.onUpdateIndicatorListener {

    public VerticalStepView(Context context) {
        this(context, null);
    }

    public VerticalStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void updateView() {
        List<Float> centerPointPositionList = mStepViewIndicator.getCircleCenterPointPositionList();
        if (mStepList == null || centerPointPositionList.size() == 0) {
            return;
        }

        if (mStepList.size() != mTextViewList.size()) {
            return;
        }

        for (int i = 0; i < mStepList.size(); i++) {
            Step step = mStepList.get(i);
            TextView textView = mTextViewList.get(i);

            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
            textView.setText(step.getName());

            final int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            textView.measure(spec, spec);
            textView.setY(centerPointPositionList.get(i) - (mStepViewIndicator.getCircleRadiusPx() / 2) - (mTextSize / 2));
            switch (step.getState()) {
                case CURRENT:
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setTextColor(mCurrentStepTextColor);
                    break;
                case COMPLETED:
                    textView.setTypeface(null, Typeface.NORMAL);
                    textView.setTextColor(mCompletedStepTextColor);
                    break;
                case NOT_COMPLETED:
                    textView.setTypeface(null, Typeface.NORMAL);
                    textView.setTextColor(mNotCompletedStepTextColor);
                    break;
            }
        }

        mTextContainer.requestLayout();
    }

    private void init() {
        View rootView = inflate(getContext(), R.layout.vertical_step_view, this);
        mStepViewIndicator = rootView.findViewById(R.id.steps_indicator);
        mStepViewIndicator.setOnUpdateIndicatorListener(this);
        mTextContainer = rootView.findViewById(R.id.rl_text_container);
    }
}