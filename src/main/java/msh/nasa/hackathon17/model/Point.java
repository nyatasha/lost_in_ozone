package msh.nasa.hackathon17.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * @author mohsen_shahini on 4/30/17.
 */

public class Point implements Parcelable {
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("doze")
    @Expose
    private String doze;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("speed")
    @Expose
    private String speed;

    public Point() {

    }

    protected Point(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
        doze = in.readString();
        height = in.readString();
        speed = in.readString();
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {

        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDoze() {
        return doze;
    }

    public void setDoze(String doze) {
        this.doze = doze;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(doze);
        dest.writeString(height);
        dest.writeString(speed);
    }
}
