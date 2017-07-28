package fi.oulu.fablab.myapplication1.models;
/*
Model class for Image Object.
JSON Object:
    {
        "notes": String,
        "imagepath": String,
        "thumbpath": String,
        "id": Integer
    }
*/

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {
    private String notes;
    private String imagepath;
    private String thumbpath;
    private int id;

    public Image(String notes, String imagepath, String thumbpath, int id) {
        this.notes = notes;
        this.imagepath = imagepath;
        this.thumbpath = thumbpath;
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getThumbpath() {
        return thumbpath;
    }

    public void setThumbpath(String thumbpath) {
        this.thumbpath = thumbpath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.notes);
        dest.writeString(this.imagepath);
        dest.writeString(this.thumbpath);
        dest.writeInt(this.id);
    }

    protected Image(Parcel in) {
        this.notes = in.readString();
        this.imagepath = in.readString();
        this.thumbpath = in.readString();
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
