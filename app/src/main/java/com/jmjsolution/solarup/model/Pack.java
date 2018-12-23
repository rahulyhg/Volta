package com.jmjsolution.solarup.model;

import java.util.List;

public class Pack {

    private String mNom;
    private List<String> mDescriptif;
    private int mPrix;
    private String mId;

    public Pack(){}

    public Pack(String nom, List<String> descriptif, int prix, String id) {
        mNom = nom;
        mDescriptif = descriptif;
        mPrix = prix;
        mId = id;
    }

    public String getNom() {
        return mNom;
    }

    public void setNom(String nom) {
        mNom = nom;
    }

    public List<String> getDescriptif() {
        return mDescriptif;
    }

    public void setDescriptif(List<String> descriptif) {
        mDescriptif = descriptif;
    }

    public int getPrix() {
        return mPrix;
    }

    public void setPrix(int prix) {
        mPrix = prix;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
