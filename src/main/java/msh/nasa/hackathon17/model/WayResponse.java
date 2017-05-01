package msh.nasa.hackathon17.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author  mohsen_shahini on 4/29/17.
 *
 * {
        "source": {
            "latitude": 53.9045,
            "longitude": 27.5615
        },
        "destination": {
            "latitude": 51.5074,
            "longitude": 0.1278
        },
        "path": [
                    {
                    "point": {
                        "latitude": 51.5074,
                        "longitude": 0.1278,
                        "doze": 1.555,
                        "height": 1000,
                        "speed": 900
                        }
                    }
                ]
    }
 */

public class WayResponse implements Parcelable {
    private String id;
    @SerializedName("source")
    @Expose
    private Source source;
    @SerializedName("destination")
    @Expose
    private Destination destination;
    @SerializedName("path")
    @Expose
    private List<Path> path = null;

    public WayResponse() {}

    public WayResponse(Parcel in) {
        id = in.readString();
        source = in.readParcelable(Source.class.getClassLoader());
        destination = in.readParcelable(Destination.class.getClassLoader());
        path = in.createTypedArrayList(Path.CREATOR);
    }

    public static final Creator<WayResponse> CREATOR = new Creator<WayResponse>() {

        @Override
        public WayResponse createFromParcel(Parcel in) {
            return new WayResponse(in);
        }

        @Override
        public WayResponse[] newArray(int size) {
            return new WayResponse[size];
        }
    };

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public List<Path> getPath() {
        return path;
    }

    public void setPath(List<Path> path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(source, flags);
        dest.writeParcelable(destination, flags);
        dest.writeTypedList(path);
    }
}
