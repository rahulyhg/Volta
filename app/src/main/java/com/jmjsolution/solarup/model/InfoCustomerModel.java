package com.jmjsolution.solarup.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InfoCustomerModel {

    private double mLatitude, mLongitude;
    private String mAddress;
    private String mCity;
    private String mConsoElectrique;
    private String mCoutElectrique;
    private String mInclinaison;
    private String mAzimuth;
    private String mPrenom;
    private String mNom;
    private String mTelephone;
    private String mEmail;
    private int mChauffageType, mChauffeEauType, mHauteurPlafond, mGrandeurToit, mGenre;
    private long mDate;

    public InfoCustomerModel(double latitude, double longitude, String address, String city, String consoElectrique, String coutElectrique, String inclinaison, String azimuth, String prenom, String nom, String telephone, String email, int chauffageType, int chauffeEauType, int hauteurPlafond, int grandeurToit, int genre, long date) {
        mLatitude = latitude;
        mLongitude = longitude;
        mAddress = address;
        mCity = city;
        mConsoElectrique = consoElectrique;
        mCoutElectrique = coutElectrique;
        mInclinaison = inclinaison;
        mAzimuth = azimuth;
        mPrenom = prenom;
        mNom = nom;
        mTelephone = telephone;
        mEmail = email;
        mChauffageType = chauffageType;
        mChauffeEauType = chauffeEauType;
        mHauteurPlafond = hauteurPlafond;
        mGrandeurToit = grandeurToit;
        mGenre = genre;
        mDate = date;
    }

    public InfoCustomerModel(){}


    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getConsoElectrique() {
        return mConsoElectrique;
    }

    public void setConsoElectrique(String consoElectrique) {
        mConsoElectrique = consoElectrique;
    }

    public String getCoutElectrique() {
        return mCoutElectrique;
    }

    public void setCoutElectrique(String coutElectrique) {
        mCoutElectrique = coutElectrique;
    }

    public String getInclinaison() {
        return mInclinaison;
    }

    public void setInclinaison(String inclinaison) {
        mInclinaison = inclinaison;
    }

    public String getAzimuth() {
        return mAzimuth;
    }

    public void setAzimuth(String azimuth) {
        mAzimuth = azimuth;
    }

    public String getPrenom() {
        return mPrenom;
    }

    public void setPrenom(String prenom) {
        mPrenom = prenom;
    }

    public String getNom() {
        return mNom;
    }

    public void setNom(String nom) {
        mNom = nom;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public void setTelephone(String telephone) {
        mTelephone = telephone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public int getChauffageType() {
        return mChauffageType;
    }

    public void setChauffageType(int chauffageType) {
        mChauffageType = chauffageType;
    }

    public int getChauffeEauType() {
        return mChauffeEauType;
    }

    public void setChauffeEauType(int chauffeEauType) {
        mChauffeEauType = chauffeEauType;
    }

    public int getHauteurPlafond() {
        return mHauteurPlafond;
    }

    public void setHauteurPlafond(int hauteurPlafond) {
        mHauteurPlafond = hauteurPlafond;
    }

    public int getGrandeurToit() {
        return mGrandeurToit;
    }

    public void setGrandeurToit(int grandeurToit) {
        mGrandeurToit = grandeurToit;
    }

    public int getGenre() {
        return mGenre;
    }

    public void setGenre(int genre) {
        mGenre = genre;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public String getDateFormatted(){
        return getDate(mDate, "dd/MM/yyyy");
    }

    public String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
