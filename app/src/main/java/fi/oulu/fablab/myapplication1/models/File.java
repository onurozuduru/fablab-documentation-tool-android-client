package fi.oulu.fablab.myapplication1.models;
/*
Model class for File Object.
JSON Object:
    {
        "id": Integer,
        "filepath": String
    }
*/

public class File {
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
}
