package com.jmjsolution.solarup.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.jmjsolution.solarup.interfaces.UserLocationListener;

/**
 * Created by Samuel on 05/04/2018.
 */

public class UserLocation {

    private final Context mContext;
    private LocationManager mLocationManager;
    private UserLocationListener mListener;
    private boolean mIsGPSEnabled = false;
    private boolean mIsNetworkEnabled = false;

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null && mListener != null) {
                Log.i("User Location", "lat: " + location.getLatitude());
                Log.i("User Location", "lng: " + location.getLongitude());
                mListener.onLocationFound(location);
            }
            mLocationManager.removeUpdates(this);
            mLocationManager = null;
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public UserLocation(Context context, UserLocationListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void fetch() {

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                mIsGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (mIsGPSEnabled || mIsNetworkEnabled) {

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setCostAllowed(true);
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                String provider = mLocationManager.getBestProvider(criteria, false);

                //Request location for a more precise location

                mLocationManager.requestSingleUpdate(provider, mLocationListener, null);
            }
        } else {
            if (mListener != null) {
                mListener.onLocationFound(null);
            }
        }
    }

    public void stop(){
        if(mLocationManager != null && mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

}