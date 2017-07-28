package fi.oulu.fablab.myapplication1;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class ApiClient {
    private static final String BASE_URL = "http://10.20.203.95/api";
    private static final String PARAM_KEY_USERID = "userid";
    private String userid;
    private ApiService apiService;

    public ApiClient(final String userid) {
        this.userid = userid;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam(PARAM_KEY_USERID, userid);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        this.apiService = restAdapter.create(ApiService.class);
    }

    public ApiClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        this.apiService = restAdapter.create(ApiService.class);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getParamKeyUserid() {
        return PARAM_KEY_USERID;
    }

    public String getUserid() {
        return userid;
    }

    public ApiService getApiService() {
        return apiService;
    }
}
