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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Content implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeInt(this.authorid);
        dest.writeString(this.title);
        dest.writeTypedList(this.images);
        dest.writeParcelable(this.author, flags);
        dest.writeInt(this.id);
        dest.writeTypedList(this.files);
        dest.writeString(this.createtime);
    }

    protected Content(Parcel in) {
        this.content = in.readString();
        this.status = in.readByte() != 0;
        this.authorid = in.readInt();
        this.title = in.readString();
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.author = in.readParcelable(User.class.getClassLoader());
        this.id = in.readInt();
        this.files = in.createTypedArrayList(File.CREATOR);
        this.createtime = in.readString();
    }

    public static final Parcelable.Creator<Content> CREATOR = new Parcelable.Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel source) {
            return new Content(source);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };
}
