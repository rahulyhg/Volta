package com.jmjsolution.solarup.model;

import java.util.List;
import java.util.Map;

public class Materiel {

    private String mNom, mCategorie;
    private int mPrix;
    private List<String> mDescriptif;

    public Materiel(String nom, String categorie, int prix, String id, List<String> descriptif) {
        mNom = nom;
        mCategorie = categorie;
        mPrix = prix;
        mId = id;
        mDescriptif = descriptif;
    }

    public Materiel() {
    }

    public List<String> getDescriptif() {
        return mDescriptif;
    }

    public void setDescriptif(List<String> descriptif) {
        mDescriptif = descriptif;
    }


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    private String mId;

    public String getNom() {
        return mNom;
    }

    public void setNom(String nom) {
        mNom = nom;
    }

    public String getCategorie() {
        return mCategorie;
    }

    public void setCategorie(String categorie) {
        mCategorie = categorie;
    }

    public int getPrix() {
        return mPrix;
    }

    public void setPrix(int prix) {
        mPrix = prix;
    }
}
