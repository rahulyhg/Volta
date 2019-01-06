package com.jmjsolution.solarup.model;

import java.util.List;

public class Pack {

    private String mNom;
    private List<String> mDescriptif;
    private int mPrix;
    private String mId;
    private int mNb_modules;
    private int mPuissance;

    public Pack(){}

    public Pack(String nom, List<String> descriptif, int prix, String id, int nb_modules, int puissance) {
        mNom = nom;
        mDescriptif = descriptif;
        mPrix = prix;
        mId = id;
        mNb_modules = nb_modules;
        mPuissance = puissance;
    }

    public int getNb_modules() {
        return mNb_modules;
    }

    public void setNb_modules(int nb_modules) {
        mNb_modules = nb_modules;
    }

    public int getPuissance() {
        return mPuissance;
    }

    public void setPuissance(int puissance) {
        mPuissance = puissance;
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
