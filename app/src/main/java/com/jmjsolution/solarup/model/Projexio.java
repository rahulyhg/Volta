package com.jmjsolution.solarup.model;

public class Projexio {

    private String mMontantTotalCredit, mMdureeContrat, mNbEcheanceMensuelle,
            mMontantEcheanceMensuelleHorsAssurance, mMontantDerniereMensualiteAjustee,
            mMontantTotalDuHorsAssurance, mTauxDebiteur, mTAEG, mNbMoisReport, mFraisDossier,
            mNbEchanceSansRembCapital;

    private String mAssuranceDC_PTIA_TAEA, mAssuranceDC_PTIA_MontantTotalDuADE, mAssuranceDC_PTIA_CoutMensuelADE;

    private String mAssuranceDC_PTIA_ITT_PE_TAEA, mAssurance_DC_PTIA_ITT_PE_MontantTotalDuADE, mAssuranceDC_PTIA_ITT_PE_CoutMensuelADE;

    private String mAssuranceSeniorTAEA, mAssuranceSeniorMontantTotalDuADE, mAssuranceSeniorCoutMensuelADE;

    public Projexio(String montantTotalCredit, String mdureeContrat, String nbEcheanceMensuelle, String montantEcheanceMensuelleHorsAssurance, String montantDerniereMensualiteAjustee, String montantTotalDuHorsAssurance, String tauxDebiteur, String TAEG, String nbMoisReport, String fraisDossier, String nbEchanceSansRembCapital, String assuranceDC_PTIA_TAEA, String assuranceDC_PTIA_MontantTotalDuADE, String assuranceDC_PTIA_CoutMensuelADE, String assuranceDC_PTIA_ITT_PE_TAEA, String assurance_DC_PTIA_ITT_PE_MontantTotalDuADE, String assuranceDC_PTIA_ITT_PE_CoutMensuelADE, String assuranceSeniorTAEA, String assuranceSeniorMontantTotalDuADE, String assuranceSeniorCoutMensuelADE) {
        mMontantTotalCredit = montantTotalCredit;
        mMdureeContrat = mdureeContrat;
        mNbEcheanceMensuelle = nbEcheanceMensuelle;
        mMontantEcheanceMensuelleHorsAssurance = montantEcheanceMensuelleHorsAssurance;
        mMontantDerniereMensualiteAjustee = montantDerniereMensualiteAjustee;
        mMontantTotalDuHorsAssurance = montantTotalDuHorsAssurance;
        mTauxDebiteur = tauxDebiteur;
        mTAEG = TAEG;
        mNbMoisReport = nbMoisReport;
        mFraisDossier = fraisDossier;
        mNbEchanceSansRembCapital = nbEchanceSansRembCapital;
        mAssuranceDC_PTIA_TAEA = assuranceDC_PTIA_TAEA;
        mAssuranceDC_PTIA_MontantTotalDuADE = assuranceDC_PTIA_MontantTotalDuADE;
        mAssuranceDC_PTIA_CoutMensuelADE = assuranceDC_PTIA_CoutMensuelADE;
        mAssuranceDC_PTIA_ITT_PE_TAEA = assuranceDC_PTIA_ITT_PE_TAEA;
        mAssurance_DC_PTIA_ITT_PE_MontantTotalDuADE = assurance_DC_PTIA_ITT_PE_MontantTotalDuADE;
        mAssuranceDC_PTIA_ITT_PE_CoutMensuelADE = assuranceDC_PTIA_ITT_PE_CoutMensuelADE;
        mAssuranceSeniorTAEA = assuranceSeniorTAEA;
        mAssuranceSeniorMontantTotalDuADE = assuranceSeniorMontantTotalDuADE;
        mAssuranceSeniorCoutMensuelADE = assuranceSeniorCoutMensuelADE;
    }

    public String getMontantTotalCredit() {
        return mMontantTotalCredit;
    }

    public void setMontantTotalCredit(String montantTotalCredit) {
        mMontantTotalCredit = montantTotalCredit;
    }

    public String getMdureeContrat() {
        return mMdureeContrat;
    }

    public void setMdureeContrat(String mdureeContrat) {
        mMdureeContrat = mdureeContrat;
    }

    public String getNbEcheanceMensuelle() {
        return mNbEcheanceMensuelle;
    }

    public void setNbEcheanceMensuelle(String nbEcheanceMensuelle) {
        mNbEcheanceMensuelle = nbEcheanceMensuelle;
    }

    public String getMontantEcheanceMensuelleHorsAssurance() {
        return mMontantEcheanceMensuelleHorsAssurance;
    }

    public void setMontantEcheanceMensuelleHorsAssurance(String montantEcheanceMensuelleHorsAssurance) {
        mMontantEcheanceMensuelleHorsAssurance = montantEcheanceMensuelleHorsAssurance;
    }

    public String getMontantDerniereMensualiteAjustee() {
        return mMontantDerniereMensualiteAjustee;
    }

    public void setMontantDerniereMensualiteAjustee(String montantDerniereMensualiteAjustee) {
        mMontantDerniereMensualiteAjustee = montantDerniereMensualiteAjustee;
    }

    public String getMontantTotalDuHorsAssurance() {
        return mMontantTotalDuHorsAssurance;
    }

    public void setMontantTotalDuHorsAssurance(String montantTotalDuHorsAssurance) {
        mMontantTotalDuHorsAssurance = montantTotalDuHorsAssurance;
    }

    public String getTauxDebiteur() {
        return mTauxDebiteur;
    }

    public void setTauxDebiteur(String tauxDebiteur) {
        mTauxDebiteur = tauxDebiteur;
    }

    public String getTAEG() {
        return mTAEG;
    }

    public void setTAEG(String TAEG) {
        mTAEG = TAEG;
    }

    public String getNbMoisReport() {
        return mNbMoisReport;
    }

    public void setNbMoisReport(String nbMoisReport) {
        mNbMoisReport = nbMoisReport;
    }

    public String getFraisDossier() {
        return mFraisDossier;
    }

    public void setFraisDossier(String fraisDossier) {
        mFraisDossier = fraisDossier;
    }

    public String getNbEchanceSansRembCapital() {
        if(!mNbEchanceSansRembCapital.equalsIgnoreCase("")) return mNbEchanceSansRembCapital;
        else return "0";
    }

    public void setNbEchanceSansRembCapital(String nbEchanceSansRembCapital) {
        mNbEchanceSansRembCapital = nbEchanceSansRembCapital;
    }

    public String getAssuranceDC_PTIA_TAEA() {
        return mAssuranceDC_PTIA_TAEA;
    }

    public void setAssuranceDC_PTIA_TAEA(String assuranceDC_PTIA_TAEA) {
        mAssuranceDC_PTIA_TAEA = assuranceDC_PTIA_TAEA;
    }

    public String getAssuranceDC_PTIA_MontantTotalDuADE() {
        return mAssuranceDC_PTIA_MontantTotalDuADE;
    }

    public void setAssuranceDC_PTIA_MontantTotalDuADE(String assuranceDC_PTIA_MontantTotalDuADE) {
        mAssuranceDC_PTIA_MontantTotalDuADE = assuranceDC_PTIA_MontantTotalDuADE;
    }

    public String getAssuranceDC_PTIA_CoutMensuelADE() {
        return mAssuranceDC_PTIA_CoutMensuelADE;
    }

    public void setAssuranceDC_PTIA_CoutMensuelADE(String assuranceDC_PTIA_CoutMensuelADE) {
        mAssuranceDC_PTIA_CoutMensuelADE = assuranceDC_PTIA_CoutMensuelADE;
    }

    public String getAssuranceDC_PTIA_ITT_PE_TAEA() {
        return mAssuranceDC_PTIA_ITT_PE_TAEA;
    }

    public void setAssuranceDC_PTIA_ITT_PE_TAEA(String assuranceDC_PTIA_ITT_PE_TAEA) {
        mAssuranceDC_PTIA_ITT_PE_TAEA = assuranceDC_PTIA_ITT_PE_TAEA;
    }

    public String getAssurance_DC_PTIA_ITT_PE_MontantTotalDuADE() {
        return mAssurance_DC_PTIA_ITT_PE_MontantTotalDuADE;
    }

    public void setAssurance_DC_PTIA_ITT_PE_MontantTotalDuADE(String assurance_DC_PTIA_ITT_PE_MontantTotalDuADE) {
        mAssurance_DC_PTIA_ITT_PE_MontantTotalDuADE = assurance_DC_PTIA_ITT_PE_MontantTotalDuADE;
    }

    public String getAssuranceDC_PTIA_ITT_PE_CoutMensuelADE() {
        return mAssuranceDC_PTIA_ITT_PE_CoutMensuelADE;
    }

    public void setAssuranceDC_PTIA_ITT_PE_CoutMensuelADE(String assuranceDC_PTIA_ITT_PE_CoutMensuelADE) {
        mAssuranceDC_PTIA_ITT_PE_CoutMensuelADE = assuranceDC_PTIA_ITT_PE_CoutMensuelADE;
    }

    public String getAssuranceSeniorTAEA() {
        return mAssuranceSeniorTAEA;
    }

    public void setAssuranceSeniorTAEA(String assuranceSeniorTAEA) {
        mAssuranceSeniorTAEA = assuranceSeniorTAEA;
    }

    public String getAssuranceSeniorMontantTotalDuADE() {
        return mAssuranceSeniorMontantTotalDuADE;
    }

    public void setAssuranceSeniorMontantTotalDuADE(String assuranceSeniorMontantTotalDuADE) {
        mAssuranceSeniorMontantTotalDuADE = assuranceSeniorMontantTotalDuADE;
    }

    public String getAssuranceSeniorCoutMensuelADE() {
        return mAssuranceSeniorCoutMensuelADE;
    }

    public void setAssuranceSeniorCoutMensuelADE(String assuranceSeniorCoutMensuelADE) {
        mAssuranceSeniorCoutMensuelADE = assuranceSeniorCoutMensuelADE;
    }
}
