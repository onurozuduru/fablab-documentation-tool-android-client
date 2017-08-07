package fi.oulu.fablab.myapplication1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fi.oulu.fablab.myapplication1.dirfactories.AlbumStorageDirFactory;
import fi.oulu.fablab.myapplication1.dirfactories.FroyoAlbumDirFactory;
import fi.oulu.fablab.myapplication1.models.Image;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class PhotoUploadActivity extends AppCompatActivity {
    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    RadioGroup radioGroupSize;
    RadioButton radioButtonSize1;
    RadioButton radioButtonSize2;
    RadioButton radioButtonSize3;

    ImageView imageViewUserImage;

    Button buttonGallery;
    Button buttonCamera;
    Button buttonUpload;

    MultiplePermissionsListener multiplePermissionsListener;

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;
    private String selectedFile;

    private String id;// postid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        radioGroupSize = (RadioGroup) findViewById(R.id.radioGroupSize);
        radioButtonSize1 = (RadioButton) findViewById(R.id.radioButtonSize1);
        radioButtonSize2 = (RadioButton) findViewById(R.id.radioButtonSize2);
        radioButtonSize3 = (RadioButton) findViewById(R.id.radioButtonSize3);

        imageViewUserImage = (ImageView) findViewById(R.id.imageViewUserImage);

        buttonGallery = (Button) findViewById(R.id.buttonGallery);
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);

        // Since min target API is greater than API 16.
        mAlbumStorageDirFactory = new FroyoAlbumDirFactory();

        // Get the postid.
        id = getIntent().getStringExtra("id");

        selectedFile = null;

        setListeners();
    }

    private void setListeners() {
        // Permission listener to get permissions dynamically by Dexter.
        multiplePermissionsListener = SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                .with((ViewGroup) findViewById(android.R.id.content), "Camera and storage access is needed to take picture")
                .withOpenSettingsButton("Settings")
                .withCallback(new Snackbar.Callback() {
                    @Override
                    public void onShown(Snackbar snackbar) {
                        // Event handler for when the given Snackbar has been dismissed
                        startCameraIntent();
                    }
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        // Event handler for when the given Snackbar is visible
                        super.onDismissed(snackbar, event);
                    }
                })
                .build();

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First check for permissions for Android 6+
                int currentAPIVersion = Build.VERSION.SDK_INT;
                if(currentAPIVersion>=android.os.Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(PhotoUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // Use Dexter to grant permissions
                        Dexter.withActivity(PhotoUploadActivity.this)
                                .withPermissions(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ).withListener(multiplePermissionsListener).check();
                    }
                    else {
                        // If permissions have been already granted, open camera.
                        startCameraIntent();
                    }
                }
                else {
                    // If it is less than Android 6.0
                    startCameraIntent();
                }
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGalleryIntent();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    protected void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkURI = FileProvider.getUriForFile(
                        getBaseContext(),
                        getApplicationContext()
                                .getPackageName() + ".provider", f);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI);
            }
            else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            }
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
    }

    protected void startGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    protected void uploadImage() {
        // If there is no file do not do anything.
        if(selectedFile == null)
            return;

        // Base URL of image upload page is different from common API service.
        // So, new adaptor is needed for Image Upload Service.
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ImageUploadService.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        ImageUploadService service = restAdapter.create(ImageUploadService.class);

        // Create file for multipart form.
        TypedFile typedFile = new TypedFile("multipart/form-data", new File(selectedFile));

        // Get selected size of the image. Default is size-2.
        int selectedRadioButtonId = radioGroupSize.getCheckedRadioButtonId();
        String size = "size-2";
        switch (selectedRadioButtonId) {
            case (R.id.radioButtonSize1):
                size = "size-1";
                break;
            case (R.id.radioButtonSize2):
                size = "size-2";
                break;
            case (R.id.radioButtonSize3):
                size = "size-3";
                break;
        }

        // Upload the image and show feedback to user.
        service.uploadImage(typedFile, id, size, new Callback<Image>() {
            @Override
            public void success(Image image, Response response) {
                Snackbar.make(findViewById(R.id.layoutPhotoUpload),
                        "Image is Uploaded!",
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(findViewById(R.id.layoutPhotoUpload),
                        "Something went wrong, Please try again later!",
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                galleryResult(data);
            }
            else if (requestCode == REQUEST_CAMERA) {
                cameraResult();
            }
        }
    }

    private void cameraResult() {
        if (mCurrentPhotoPath != null) {
            // Show image to user by loading it to an imageview.
            Picasso
                    .with(getBaseContext())
                    .load("file:" + mCurrentPhotoPath) // Add file: prefix to path, otherwise it won't work.
                    .fit()// Resize image for imageview.
                    .centerInside()// Respect aspect ratio.
                    .into(imageViewUserImage);
            // Save image to gallery which belongs to this application.
            galleryAddPic();
            // Reset path to get a new image from camera.
            mCurrentPhotoPath = null;
        }
    }

    private void galleryResult(Intent data) {
        if (data != null) {
            // Show image to user by loading it to an imageview.
            Picasso
                .with(getBaseContext())
                .load("file:" + getRealPathFromURI(this, data.getData()))// Real path is needed to get right orientation data in API 19.
                .fit()// Resize image for imageview.
                .centerInside()// Respect aspect ratio.
                .into(imageViewUserImage);
            // Keep real path of the image to upload.
            selectedFile = getRealPathFromURI(this, data.getData());
        }
    }

    /*
    * TODO This code comes from below Github page, but there is no license file in the repo
    * https://github.com/hmkcode/Android/blob/master/android-show-image-and-path/src/com/hmkcode/android/image/RealPathUtil.java
    * This solution is really needed for API 19 and above. Solution for API 18 and below is actually a standard solution.
     */
    public static String getRealPathFromURI(Context context, Uri uri){
        String filePath = "";
        if(Build.VERSION.SDK_INT < 19) {
            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader cursorLoader = new CursorLoader(context, uri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            if(cursor != null){
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
            }
        }
        else {
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{ id }, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath;
    }

    /* Photo album for this application */
    private String getAlbumName() {
    /*
    * This function is borrowed from Android Developer Platform examples.
    * Example page: https://developer.android.com/training/camera/photobasics.html
    */
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
    /*
    * This function is borrowed from Android Developer Platform examples.
    * Example page: https://developer.android.com/training/camera/photobasics.html
    */
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
    /*
    * This function is borrowed from Android Developer Platform examples.
    * Example page: https://developer.android.com/training/camera/photobasics.html
    */
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {
    /*
    * This function is borrowed from Android Developer Platform examples.
    * Example page: https://developer.android.com/training/camera/photobasics.html
    */
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void galleryAddPic() {
    /*
    * This function is borrowed from Android Developer Platform examples.
    * And modified for needs of this application.
    * Example page: https://developer.android.com/training/camera/photobasics.html
    */
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        selectedFile = contentUri.getPath();
    }
}
