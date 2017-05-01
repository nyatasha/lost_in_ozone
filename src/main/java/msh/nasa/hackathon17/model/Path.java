package msh.nasa.hackathon17.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author mohsen_shahini on 4/30/17.
 */

public class Path implements Parcelable {
    @SerializedName("point")
    @Expose
    private Point point;

    public Path() {

    }

    public Path(Parcel in) {
        point = in.readParcelable(Point.class.getClassLoader());
    }

    public static final Creator<Path> CREATOR = new Creator<Path>() {

        @Override
        public Path createFromParcel(Parcel in) {
            return new Path(in);
        }

        @Override
        public Path[] newArray(int size) {
            return new Path[size];
        }
    };

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(point, flags);
    }
}
