package fi.oulu.fablab.myapplication1;


import fi.oulu.fablab.myapplication1.models.Content;
import fi.oulu.fablab.myapplication1.models.Image;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface ApiService {
    @GET("/contents/")
    void getProjectList(Callback<Content.ContentItems> cb);

    @GET("/contents/{id}")
    void getProject(@Path("id") String id, Callback<Content> cb);

    @PUT("/images/{id}")
    void saveNotes(@Path("id") String id, @Header("Content-Type") String content_type, @Body Image image, Callback<Image> cb);
}
