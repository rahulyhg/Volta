
package com.jmjsolution.solarup.model;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Solar implements Parcelable
{

    @SerializedName("inputs")
    @Expose
    private Inputs inputs;
    @SerializedName("errors")
    @Expose
    private List<Object> errors = null;
    @SerializedName("warnings")
    @Expose
    private List<Object> warnings = null;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("ssc_info")
    @Expose
    private SscInfo sscInfo;
    @SerializedName("station_info")
    @Expose
    private StationInfo stationInfo;
    @SerializedName("outputs")
    @Expose
    private Outputs outputs;
    public final static Parcelable.Creator<Solar> CREATOR = new Creator<Solar>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Solar createFromParcel(Parcel in) {
            return new Solar(in);
        }

        public Solar[] newArray(int size) {
            return (new Solar[size]);
        }

    }
    ;

    protected Solar(Parcel in) {
        this.inputs = ((Inputs) in.readValue((Inputs.class.getClassLoader())));
        in.readList(this.errors, (java.lang.Object.class.getClassLoader()));
        in.readList(this.warnings, (java.lang.Object.class.getClassLoader()));
        this.version = ((String) in.readValue((String.class.getClassLoader())));
        this.sscInfo = ((SscInfo) in.readValue((SscInfo.class.getClassLoader())));
        this.stationInfo = ((StationInfo) in.readValue((StationInfo.class.getClassLoader())));
        this.outputs = ((Outputs) in.readValue((Outputs.class.getClassLoader())));
    }

    public Solar() {
    }

    public Inputs getInputs() {
        return inputs;
    }

    public void setInputs(Inputs inputs) {
        this.inputs = inputs;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    public List<Object> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Object> warnings) {
        this.warnings = warnings;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SscInfo getSscInfo() {
        return sscInfo;
    }

    public void setSscInfo(SscInfo sscInfo) {
        this.sscInfo = sscInfo;
    }

    public StationInfo getStationInfo() {
        return stationInfo;
    }

    public void setStationInfo(StationInfo stationInfo) {
        this.stationInfo = stationInfo;
    }

    public Outputs getOutputs() {
        return outputs;
    }

    public void setOutputs(Outputs outputs) {
        this.outputs = outputs;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(inputs);
        dest.writeList(errors);
        dest.writeList(warnings);
        dest.writeValue(version);
        dest.writeValue(sscInfo);
        dest.writeValue(stationInfo);
        dest.writeValue(outputs);
    }

    public int describeContents() {
        return  0;
    }

}
