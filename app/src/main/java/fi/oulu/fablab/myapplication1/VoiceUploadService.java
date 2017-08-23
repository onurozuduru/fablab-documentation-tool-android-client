package fi.oulu.fablab.myapplication1;

import fi.oulu.fablab.myapplication1.models.File;
import fi.oulu.fablab.myapplication1.models.Image;
import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface VoiceUploadService {
    public static final String BASE_URL = "http://10.20.204.64";

    @Multipart
    @POST("/voiceupload/")
    void uploadVoice(@Part("files[]") TypedFile file,
                     @Part("id") String id, // postid
                     @Part("imageid") String imageid,
                     Callback<File> cb);
}
