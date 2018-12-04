package com.jmjsolution.solarup.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.model.CalendarEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHodler> {


    private final Context mContext;
    private final ArrayList<CalendarEvent> mEvents;
    private ArrayList<CalendarEvent> mEventsSorted = new ArrayList<>();

    public EventAdapter(Context context, ArrayList<CalendarEvent> events){
        mContext = context;
        mEvents = events;
    }

    @NonNull
    @Override
    public EventViewHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item, viewGroup, false);
        return new EventViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHodler eventViewHodler, int i) {
        eventViewHodler.bindEvent(mEvents.get(i));
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public ArrayList<CalendarEvent> selectDate(String timeStamp) {
        mEventsSorted.clear();
        for(CalendarEvent event : mEvents){
            String timeStampToCompare = new SimpleDateFormat("YYYY/MM/dd").format(event.getBegin());
            if(timeStamp.equalsIgnoreCase(timeStampToCompare)){
                mEventsSorted.add(event);
            }
        }

        return mEventsSorted;

    }

    public class EventViewHodler extends RecyclerView.ViewHolder {

        private TextView mTitleTv, mDateTv, mAddressTv, mBigDateTv;

        public EventViewHodler(@NonNull View itemView) {
            super(itemView);

            mBigDateTv = itemView.findViewById(R.id.dateTextView);
            mTitleTv = itemView.findViewById(R.id.titleEventItem);
            mDateTv = itemView.findViewById(R.id.dateEventItem);
            mAddressTv = itemView.findViewById(R.id.addressEventItem);
        }

        public void bindEvent(CalendarEvent event) {
            String timeStamp = new SimpleDateFormat("dd/MM").format(event.getBegin());
            String timestamp = new SimpleDateFormat("dd MMM YYYY").format(event.getBegin());
            mTitleTv.setText(event.getTitle());
            mBigDateTv.setText(timeStamp);
            mDateTv.setText(timestamp);
            mAddressTv.setText("68 chemin du vallon de Toulouse");
        }
    }
}
