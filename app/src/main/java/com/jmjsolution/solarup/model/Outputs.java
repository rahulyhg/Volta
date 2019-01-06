
package com.jmjsolution.solarup.model;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Outputs implements Parcelable
{

    @SerializedName("ac_monthly")
    @Expose
    private List<Double> acMonthly = null;
    @SerializedName("poa_monthly")
    @Expose
    private List<Double> poaMonthly = null;
    @SerializedName("solrad_monthly")
    @Expose
    private List<Double> solradMonthly = null;
    @SerializedName("dc_monthly")
    @Expose
    private List<Double> dcMonthly = null;
    @SerializedName("ac_annual")
    @Expose
    private Double acAnnual;
    @SerializedName("solrad_annual")
    @Expose
    private Double solradAnnual;
    @SerializedName("capacity_factor")
    @Expose
    private Double capacityFactor;
    public final static Parcelable.Creator<Outputs> CREATOR = new Creator<Outputs>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Outputs createFromParcel(Parcel in) {
            return new Outputs(in);
        }

        public Outputs[] newArray(int size) {
            return (new Outputs[size]);
        }

    }
    ;

    protected Outputs(Parcel in) {
        in.readList(this.acMonthly, (java.lang.Double.class.getClassLoader()));
        in.readList(this.poaMonthly, (java.lang.Double.class.getClassLoader()));
        in.readList(this.solradMonthly, (java.lang.Double.class.getClassLoader()));
        in.readList(this.dcMonthly, (java.lang.Double.class.getClassLoader()));
        this.acAnnual = ((Double) in.readValue((Double.class.getClassLoader())));
        this.solradAnnual = ((Double) in.readValue((Double.class.getClassLoader())));
        this.capacityFactor = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    public Outputs() {
    }

    public List<Double> getAcMonthly() {
        return acMonthly;
    }

    public void setAcMonthly(List<Double> acMonthly) {
        this.acMonthly = acMonthly;
    }

    public List<Double> getPoaMonthly() {
        return poaMonthly;
    }

    public void setPoaMonthly(List<Double> poaMonthly) {
        this.poaMonthly = poaMonthly;
    }

    public List<Double> getSolradMonthly() {
        return solradMonthly;
    }

    public void setSolradMonthly(List<Double> solradMonthly) {
        this.solradMonthly = solradMonthly;
    }

    public List<Double> getDcMonthly() {
        return dcMonthly;
    }

    public void setDcMonthly(List<Double> dcMonthly) {
        this.dcMonthly = dcMonthly;
    }

    public Double getAcAnnual() {
        return acAnnual;
    }

    public void setAcAnnual(Double acAnnual) {
        this.acAnnual = acAnnual;
    }

    public Double getSolradAnnual() {
        return solradAnnual;
    }

    public void setSolradAnnual(Double solradAnnual) {
        this.solradAnnual = solradAnnual;
    }

    public Double getCapacityFactor() {
        return capacityFactor;
    }

    public void setCapacityFactor(Double capacityFactor) {
        this.capacityFactor = capacityFactor;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(acMonthly);
        dest.writeList(poaMonthly);
        dest.writeList(solradMonthly);
        dest.writeList(dcMonthly);
        dest.writeValue(acAnnual);
        dest.writeValue(solradAnnual);
        dest.writeValue(capacityFactor);
    }

    public int describeContents() {
        return  0;
    }

}
