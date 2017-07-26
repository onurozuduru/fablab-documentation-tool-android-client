package fi.oulu.fablab.myapplication1;


import fi.oulu.fablab.myapplication1.models.Content;
import retrofit.Callback;
import retrofit.http.GET;

public interface ContentApiService {
    @GET("/contents/")
    void getProjectList(Callback<Content.ContentItems> cb);
}
