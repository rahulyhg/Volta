
package com.jmjsolution.solarup.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Inputs implements Parcelable
{

    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("system_capacity")
    @Expose
    private String systemCapacity;
    @SerializedName("azimuth")
    @Expose
    private String azimuth;
    @SerializedName("tilt")
    @Expose
    private String tilt;
    @SerializedName("array_type")
    @Expose
    private String arrayType;
    @SerializedName("module_type")
    @Expose
    private String moduleType;
    @SerializedName("losses")
    @Expose
    private String losses;
    public final static Parcelable.Creator<Inputs> CREATOR = new Creator<Inputs>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Inputs createFromParcel(Parcel in) {
            return new Inputs(in);
        }

        public Inputs[] newArray(int size) {
            return (new Inputs[size]);
        }

    }
    ;

    protected Inputs(Parcel in) {
        this.lat = ((String) in.readValue((String.class.getClassLoader())));
        this.lon = ((String) in.readValue((String.class.getClassLoader())));
        this.systemCapacity = ((String) in.readValue((String.class.getClassLoader())));
        this.azimuth = ((String) in.readValue((String.class.getClassLoader())));
        this.tilt = ((String) in.readValue((String.class.getClassLoader())));
        this.arrayType = ((String) in.readValue((String.class.getClassLoader())));
        this.moduleType = ((String) in.readValue((String.class.getClassLoader())));
        this.losses = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Inputs() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getSystemCapacity() {
        return systemCapacity;
    }

    public void setSystemCapacity(String systemCapacity) {
        this.systemCapacity = systemCapacity;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    public String getTilt() {
        return tilt;
    }

    public void setTilt(String tilt) {
        this.tilt = tilt;
    }

    public String getArrayType() {
        return arrayType;
    }

    public void setArrayType(String arrayType) {
        this.arrayType = arrayType;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getLosses() {
        return losses;
    }

    public void setLosses(String losses) {
        this.losses = losses;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(lat);
        dest.writeValue(lon);
        dest.writeValue(systemCapacity);
        dest.writeValue(azimuth);
        dest.writeValue(tilt);
        dest.writeValue(arrayType);
        dest.writeValue(moduleType);
        dest.writeValue(losses);
    }

    public int describeContents() {
        return  0;
    }

}
