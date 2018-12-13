package com.jmjsolution.solarup.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.model.CalendarEvent;
import com.jmjsolution.solarup.ui.fragments.AgendaFragment;
import com.jmjsolution.solarup.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHodler> {


    private final Context mContext;
    private final ArrayList<CalendarEvent> mEvents;
    private FragmentActivity mFragmentActivity;
    private ArrayList<CalendarEvent> mEventsSorted = new ArrayList<>();

    public EventAdapter(Context context, ArrayList<CalendarEvent> events, FragmentActivity activity){
        mContext = context;
        mEvents = events;
        mFragmentActivity = activity;
    }

    @NonNull
    @Override
    public EventViewHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item, viewGroup, false);
        return new EventViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHodler eventViewHodler, int i) {
        eventViewHodler.mLinearLayout.setTag(i);
        eventViewHodler.mDeleteEventIv.setTag(i);
        eventViewHodler.bindEvent(mEvents.get(i));
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public ArrayList<CalendarEvent> selectDate(String timeStamp) {
        mEventsSorted.clear();
        for(CalendarEvent event : mEvents){
            @SuppressLint("SimpleDateFormat") String timeStampToCompare = new SimpleDateFormat("YYYY/MM/dd").format(event.getBegin());
            if(timeStamp.equalsIgnoreCase(timeStampToCompare)){
                mEventsSorted.add(event);
            }
        }

        return mEventsSorted;

    }

    public class EventViewHodler extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        private LinearLayout mLinearLayout;
        private TextView mTitleTv, mDateTv, mAddressTv, mBigDateTv;
        private ImageView mDeleteEventIv, mConfirmedIv;

        private EventViewHodler(@NonNull View itemView) {
            super(itemView);

            mLinearLayout = itemView.findViewById(R.id.infosEventLyt);
            mBigDateTv = itemView.findViewById(R.id.dateTextView);
            mTitleTv = itemView.findViewById(R.id.titleEventItem);
            mDateTv = itemView.findViewById(R.id.dateEventItem);
            mAddressTv = itemView.findViewById(R.id.addressEventItem);
            mDeleteEventIv = itemView.findViewById(R.id.deleteEventIv);
            mConfirmedIv = itemView.findViewById(R.id.confirmedDateImageView);
            itemView.setOnLongClickListener(this);
            mDeleteEventIv.setOnClickListener(this);
            mLinearLayout.setOnClickListener(this);
        }

        private void bindEvent(CalendarEvent event) {
            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd/MM").format(event.getBegin());
            @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("dd MMM YYYY HH:mm").format(event.getBegin());
            mTitleTv.setText(event.getTitle());
            mBigDateTv.setText(timeStamp);
            mDateTv.setText(timestamp);
            if(event.getEventStatus() == 3){
                mConfirmedIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fiber_manual_record_red_24dp));
            } else {
                mConfirmedIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fiber_manual_record_green_24dp));
            }
            if(event.getLocation().length() <= 0){
                mAddressTv.setVisibility(View.GONE);
            } else {
                mAddressTv.setText(event.getLocation());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int position = (int) view.getTag();
            deleteEvent(position);
            return true;
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public void onClick(View view) {
            final int position = (int) view.getTag();

            if(view == mLinearLayout){

                if(mEvents.get(position).getLocation().length() > 0) {

                    final Location location = Utils.getPlaceLocation(mContext, mEvents.get(position).getLocation());

                    if (location != null && mEvents.get(position).getEventStatus() != 3) {

                        Dialog dialog = new Dialog(mContext);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.map_dialog_event);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();

                        MapView mMapView;
                        final LinearLayout linearLayoutAcceptDecline = dialog.findViewById(R.id.acceptDeclineLyt);
                        final LinearLayout linearLayoutSearchingLoca = dialog.findViewById(R.id.searchingLocalisationLyt);
                        final LinearLayout linearLayoutFoundLoca = dialog.findViewById(R.id.localisationFoundLyt);
                        final TextView titleEventTv = dialog.findViewById(R.id.textEventTv);
                        final TextView locationTv = dialog.findViewById(R.id.locationEventTv);
                        final TextView dateTv = dialog.findViewById(R.id.dateEventTv);
                        linearLayoutFoundLoca.setVisibility(View.GONE);
                        linearLayoutAcceptDecline.setVisibility(View.GONE);
                        linearLayoutSearchingLoca.setVisibility(View.VISIBLE);

                        MapsInitializer.initialize(mContext);

                        mMapView = dialog.findViewById(R.id.mapView);
                        mMapView.onCreate(dialog.onSaveInstanceState());
                        mMapView.onResume();// needed to get the map to display immediately
                        mMapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                linearLayoutFoundLoca.setVisibility(View.VISIBLE);
                                linearLayoutSearchingLoca.setVisibility(View.GONE);
                                titleEventTv.setText(mEvents.get(position).getTitle());
                                locationTv.setText(mEvents.get(position).getLocation());
                                dateTv.setText(new SimpleDateFormat("dd MMM YYYY HH:mm").format(mEvents.get(position).getBegin()));

                                googleMap.clear();
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
                                googleMap.moveCamera(cameraUpdate);
                            }
                        });
                    } else if (location != null) {

                        final Dialog dialog = new Dialog(mContext);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.map_dialog_event);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();

                        MapView mMapView;
                        final LinearLayout linearLayoutAcceptDecline = dialog.findViewById(R.id.acceptDeclineLyt);
                        final LinearLayout linearLayoutSearchingLoca = dialog.findViewById(R.id.searchingLocalisationLyt);
                        final LinearLayout linearLayoutFoundLoca = dialog.findViewById(R.id.localisationFoundLyt);
                        final TextView titleEventTv = dialog.findViewById(R.id.textEventTv);
                        final TextView locationTv = dialog.findViewById(R.id.locationEventTv);
                        final Button acceptBtn = dialog.findViewById(R.id.acceptBtn);
                        final Button declineBtn = dialog.findViewById(R.id.declineBtn);
                        final TextView dateTv = dialog.findViewById(R.id.dateEventTv);
                        linearLayoutFoundLoca.setVisibility(View.GONE);
                        linearLayoutAcceptDecline.setVisibility(View.GONE);
                        linearLayoutSearchingLoca.setVisibility(View.VISIBLE);


                        MapsInitializer.initialize(mContext);

                        mMapView = dialog.findViewById(R.id.mapView);
                        mMapView.onCreate(dialog.onSaveInstanceState());
                        mMapView.onResume();// needed to get the map to display immediately
                        mMapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                linearLayoutFoundLoca.setVisibility(View.VISIBLE);
                                linearLayoutSearchingLoca.setVisibility(View.GONE);
                                linearLayoutAcceptDecline.setVisibility(View.VISIBLE);
                                titleEventTv.setText(mEvents.get(position).getTitle());
                                locationTv.setText(mEvents.get(position).getLocation());
                                dateTv.setText(new SimpleDateFormat("dd MMM YYYY HH:mm").format(mEvents.get(position).getBegin()));

                                googleMap.clear();
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
                                googleMap.moveCamera(cameraUpdate);

                                acceptBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UpdateCalendarEntry(position, true);
                                        dialog.dismiss();
                                    }
                                });

                                declineBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UpdateCalendarEntry(position, false);
                                        deleteEvent(position);
                                    }
                                });
                            }
                        });
                    }
                } else {

                    if(mEvents.get(position).getEventStatus() == 3){

                        final Dialog dialog = new Dialog(mContext);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.map_dialog_event);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();

                        final LinearLayout linearLayoutSearchingLoca = dialog.findViewById(R.id.searchingLocalisationLyt);
                        final LinearLayout linearLayoutAcceptDecline = dialog.findViewById(R.id.acceptDeclineLyt);
                        final LinearLayout linearLayoutFoundLoca = dialog.findViewById(R.id.localisationFoundLyt);
                        final TextView titleEventTv = dialog.findViewById(R.id.textEventTv);
                        final TextView locationTv = dialog.findViewById(R.id.locationEventTv);
                        final MapView mapView = dialog.findViewById(R.id.mapView);
                        final Button acceptBtn = dialog.findViewById(R.id.acceptBtn);
                        final Button declineBtn = dialog.findViewById(R.id.declineBtn);
                        final TextView dateTv = dialog.findViewById(R.id.dateEventTv);


                        mapView.setVisibility(View.GONE);
                        dateTv.setVisibility(View.GONE);
                        linearLayoutAcceptDecline.setVisibility(View.VISIBLE);
                        linearLayoutFoundLoca.setVisibility(View.VISIBLE);
                        linearLayoutSearchingLoca.setVisibility(View.GONE);

                        locationTv.setText(new SimpleDateFormat("dd MMM YYYY HH:mm").format(mEvents.get(position).getBegin()));
                        titleEventTv.setText(mEvents.get(position).getTitle());

                        acceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UpdateCalendarEntry(position, true);
                                dialog.dismiss();
                            }
                        });

                        declineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UpdateCalendarEntry(position, false);
                                deleteEvent(position);
                            }
                        });
                    }

                }
            }

            if(view == mDeleteEventIv) {
                showDeleteDialog(position);
            }


        }


        private void deleteEvent(int position) {
            ContentResolver cr = mContext.getContentResolver();
            Uri deleteUri;
            int id = getEventId(mEvents.get(position).getTitle());
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
            cr.delete(deleteUri, null, null);
            Toast.makeText(mContext, "Suppression de l'événement, la synchronisation peut prendre quelques instants", Toast.LENGTH_SHORT).show();
            mEvents.remove(position);
            Objects.requireNonNull(mFragmentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new AgendaFragment())
                    .commit());
        }

        @SuppressLint({"InlinedApi", "MissingPermission"})
        private void UpdateCalendarEntry(int position, boolean isAccepted) {
            ContentResolver cr = mContext.getContentResolver();
            final String query = "(" + CalendarContract.Attendees.EVENT_ID + " = ?)";
            final String[] args = new String[]{String.valueOf(getEventId(mEvents.get(position).getTitle()))};

            ContentValues values = new ContentValues();
            if(isAccepted) {
                values.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED);
            } else {
                values.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED);
            }

            cr.update(CalendarContract.Attendees.CONTENT_URI, values, query, args);
            Toast.makeText(mContext, "Modification en cours d'enregistrement, la synchronisation peut prendre quelques instants.", Toast.LENGTH_SHORT).show();
        }

        private int getEventId(String eventtitle) {

            Uri eventUri;

            eventUri = Uri.parse("content://com.android.calendar/events");

            int result = 0;
            String projection[] = {"_id", "title"};
            Cursor cursor = mContext.getContentResolver().query(eventUri, null, null, null, null);

            if (Objects.requireNonNull(cursor).moveToFirst()) {

                String calName;
                String calID;

                int nameCol = cursor.getColumnIndex(projection[1]);
                int idCol = cursor.getColumnIndex(projection[0]);
                do {
                    calName = cursor.getString(nameCol);
                    calID = cursor.getString(idCol);

                    if (calName != null && calName.contains(eventtitle)) {
                        result = Integer.parseInt(calID);
                    }

                } while (cursor.moveToNext());
                cursor.close();
            }

            return result;
        }

        private void showDeleteDialog(final int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Supprimer");
            builder.setMessage("Etes vous sur de voulour supprimer cet événement ?");
            builder.setPositiveButton("oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteEvent(position);
                }
            });
            builder.setNegativeButton("non", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    }
}
