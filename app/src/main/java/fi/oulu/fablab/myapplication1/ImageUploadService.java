package fi.oulu.fablab.myapplication1;

import fi.oulu.fablab.myapplication1.models.Image;
import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface ImageUploadService {
    public static final String BASE_URL = "http://10.20.204.64";

    @Multipart
    @POST("/imageupload/")
    void uploadImage(@Part("files[]") TypedFile file,
                @Part("id") String id, // postid
                @Part("size") String size, // size-1, size-2, size-3
                Callback<Image> cb);
}
