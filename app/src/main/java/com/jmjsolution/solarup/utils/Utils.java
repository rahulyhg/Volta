package com.jmjsolution.solarup.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static boolean isNetworkAvailable(Context context) {

        boolean isAvailable = false;
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                isAvailable = true;
            }
        }
        return isAvailable;
    }

    public static String getAddressName(Context context, Location location) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size()>0) {
            return addresses.get(0).getSubThoroughfare() + " " + addresses.get(0).getThoroughfare();
        }
        return null;
    }

    public static String getCityName(Context context, Location location) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size()>0) {
            return addresses.get(0).getLocality();
        }
        return null;
    }

    public static Location getPlaceLocation(Context context, String name) {

        Geocoder geo = new Geocoder(context, Locale.getDefault() );
        try {
            List<Address> addresses = geo.getFromLocationName(name, 1 );
            if (addresses!=null && !addresses.isEmpty() && addresses.get(0)!=null) {
                Address a = addresses.get(0);
                Location location = new Location("google");
                location.setLatitude(a.getLatitude());
                location.setLongitude(a.getLongitude());
                return location;
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
