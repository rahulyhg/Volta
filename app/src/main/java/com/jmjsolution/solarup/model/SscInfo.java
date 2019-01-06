
package com.jmjsolution.solarup.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SscInfo implements Parcelable
{

    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("build")
    @Expose
    private String build;
    public final static Parcelable.Creator<SscInfo> CREATOR = new Creator<SscInfo>() {


        @SuppressWarnings({
            "unchecked"
        })
        public SscInfo createFromParcel(Parcel in) {
            return new SscInfo(in);
        }

        public SscInfo[] newArray(int size) {
            return (new SscInfo[size]);
        }

    }
    ;

    protected SscInfo(Parcel in) {
        this.version = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.build = ((String) in.readValue((String.class.getClassLoader())));
    }

    public SscInfo() {
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(version);
        dest.writeValue(build);
    }

    public int describeContents() {
        return  0;
    }

}
