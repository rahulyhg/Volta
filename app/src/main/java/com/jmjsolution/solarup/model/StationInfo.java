
package com.jmjsolution.solarup.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StationInfo implements Parcelable
{

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("elev")
    @Expose
    private Double elev;
    @SerializedName("tz")
    @Expose
    private Double tz;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("solar_resource_file")
    @Expose
    private String solarResourceFile;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    public final static Parcelable.Creator<StationInfo> CREATOR = new Creator<StationInfo>() {


        @SuppressWarnings({
            "unchecked"
        })
        public StationInfo createFromParcel(Parcel in) {
            return new StationInfo(in);
        }

        public StationInfo[] newArray(int size) {
            return (new StationInfo[size]);
        }

    }
    ;

    protected StationInfo(Parcel in) {
        this.lat = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lon = ((Double) in.readValue((Double.class.getClassLoader())));
        this.elev = ((Double) in.readValue((Double.class.getClassLoader())));
        this.tz = ((Double) in.readValue((Double.class.getClassLoader())));
        this.location = ((String) in.readValue((String.class.getClassLoader())));
        this.city = ((String) in.readValue((String.class.getClassLoader())));
        this.state = ((String) in.readValue((String.class.getClassLoader())));
        this.solarResourceFile = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public StationInfo() {
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getElev() {
        return elev;
    }

    public void setElev(Double elev) {
        this.elev = elev;
    }

    public Double getTz() {
        return tz;
    }

    public void setTz(Double tz) {
        this.tz = tz;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSolarResourceFile() {
        return solarResourceFile;
    }

    public void setSolarResourceFile(String solarResourceFile) {
        this.solarResourceFile = solarResourceFile;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(lat);
        dest.writeValue(lon);
        dest.writeValue(elev);
        dest.writeValue(tz);
        dest.writeValue(location);
        dest.writeValue(city);
        dest.writeValue(state);
        dest.writeValue(solarResourceFile);
        dest.writeValue(distance);
    }

    public int describeContents() {
        return  0;
    }

}
