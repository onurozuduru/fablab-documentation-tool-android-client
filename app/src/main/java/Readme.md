# FabLab Documentation Tool - Android Client

This Readme explains briefly which parts must be changed to use
Android app.

## Requirements
 
`build.gradle` file handles the requirements however, it is better
to list main additional libraries here.

This code depends below libraries:

- [Retrofit](http://square.github.io/retrofit/): To handle API requests
and responses.
- [Picasso](http://square.github.io/picasso/): To handle image and
imageview operations.
- [Dexter](https://github.com/Karumi/Dexter): To handle runtime permissions.

## Before Building

Following steps must be done before build the code.

- Make sure that server is up and running.
- Change `BASE_URL` field under `ApiClient.java` for your API domain
and endpoint.
- Change `BASE_URL` field under `ImageUploadService.java` for your domain.
- Change `BASE_URL` field under `VoiceUploadService.java` for your domain.
- Since there is no login, it is important to give user ID for services.
Please give an existing user ID by modifying below line under
`MainActivity.java`.

```java
protected void onCreate(Bundle savedInstanceState) {
    ...
    API_CLIENT = new ApiClient("4"); // Change User ID
    ...
}
```

