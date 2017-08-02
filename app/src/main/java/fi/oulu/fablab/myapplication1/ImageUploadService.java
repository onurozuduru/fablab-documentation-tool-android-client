package fi.oulu.fablab.myapplication1;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface ImageUploadService {
    public static final String BASE_URL = "http://10.20.203.95";

    @Multipart
    @POST("/imageupload/")
    void uploadImage(@Part("files[]") TypedFile file,
                @Part("id") String id,
                @Part("size") String size,
                Callback<String> cb);
}
