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

public class Image {
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
}
