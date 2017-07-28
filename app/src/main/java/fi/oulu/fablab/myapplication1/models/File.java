package fi.oulu.fablab.myapplication1.models;
/*
Model class for File Object.
JSON Object:
    {
        "id": Integer,
        "filepath": String
    }
*/

import android.os.Parcel;
import android.os.Parcelable;

public class File implements Parcelable {
    private int id;
    private String filepath;

    public File(int id, String filepath) {
        this.id = id;
        this.filepath = filepath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.filepath);
    }

    protected File(Parcel in) {
        this.id = in.readInt();
        this.filepath = in.readString();
    }

    public static final Parcelable.Creator<File> CREATOR = new Parcelable.Creator<File>() {
        @Override
        public File createFromParcel(Parcel source) {
            return new File(source);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };
}
