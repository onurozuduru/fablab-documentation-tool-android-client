package fi.oulu.fablab.myapplication1;


import fi.oulu.fablab.myapplication1.models.Content;
import fi.oulu.fablab.myapplication1.models.Image;
import fi.oulu.fablab.myapplication1.models.Page;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ApiService {
    /////////////////// Content End Points

//    @GET("/contents/")
//    void getProjectList(Callback<Content.ContentItems> cb);
    @GET("/contents/")
    void getProjectList(@Query("page") Integer page, Callback<Page<Content>> cb);

    @GET("/contents/{id}")
    void getProject(@Path("id") String id, Callback<Content> cb);

    @DELETE("/contents/{id}")
    void removeProject(@Path("id") String id, Callback<Void> cb);

    @POST("/contents/")
    void createProject(@Header("Content-Type") String content_type, @Body Content content, Callback<Content> cb);

    /////////////////// Image End Points

    @DELETE("/images/{id}")
    void removeImage(@Path("id") String id, Callback<Void> cb);

    @PUT("/images/{id}")
    void saveNotes(@Path("id") String id, @Header("Content-Type") String content_type, @Body Image image, Callback<Image> cb);
}
