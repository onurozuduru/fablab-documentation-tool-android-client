package fi.oulu.fablab.myapplication1.models;
/*
Model class for Content Object.
JSON Object:
    {
        "content": String,
        "status": Boolean,
        "authorid": Integer,
        "title": String,
        "images": List of Image,
        "author": User,
        "id": Integer,
        "files": List of File,
        "createtime": String
    }
*/

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Content {
    private String content;
    private boolean status;
    private int authorid;
    private String title;
    private List<Image> images;
    private User author;
    private int id;
    private List<File> files;
    private String createtime;

    public Content(String content, int authorid, String title, int id) {
        this.content = content;
        this.authorid = authorid;
        this.title = title;
        this.id = id;

        this.status = false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getAuthorid() {
        return authorid;
    }

    public void setAuthorid(int authorid) {
        this.authorid = authorid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public static class ContentItems {
        private List<Content> items;

        public List<Content> getItems() {
            return items;
        }
    }
}
