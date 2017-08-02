package fi.oulu.fablab.myapplication1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fi.oulu.fablab.myapplication1.dirfactories.AlbumStorageDirFactory;
import fi.oulu.fablab.myapplication1.dirfactories.BaseAlbumDirFactory;
import fi.oulu.fablab.myapplication1.dirfactories.FroyoAlbumDirFactory;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class PhotoUploadActivity extends AppCompatActivity {
    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;

    RadioGroup radioGroupSize;
    RadioButton radioButtonSize1;
    RadioButton radioButtonSize2;
    RadioButton radioButtonSize3;

    ImageView imageViewUserImage;

    Button buttonGallery;
    Button buttonCamera;
    Button buttonUpload;

    MultiplePermissionsListener multiplePermissionsListener;

    private Bitmap mImageBitmap;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;
    private String selectedFile = null;

    private String id;

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

        mImageBitmap = null;

        id = getIntent().getStringExtra("id");

        setListeners();
    }

    private void setListeners() {
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
                        // Use Dexter to grand permissions
                        Dexter.withActivity(PhotoUploadActivity.this)
                                .withPermissions(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ).withListener(multiplePermissionsListener).check();
                    }
                    else {
                        startCameraIntent();
                    }
                }
                else {
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
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, REQUEST_CAMERA);
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
        if(selectedFile == null)
            return;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ImageUploadService.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        ImageUploadService service = restAdapter.create(ImageUploadService.class);

        TypedFile typedFile = new TypedFile("multipart/form-data", new File(selectedFile));

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

        service.uploadImage(typedFile, id, size, new Callback<String>() {
            @Override
            public void success(String s, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

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
                //cameraResult(data);
                handleBigCameraPhoto();
            }
        }
    }

    private void cameraResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(thumbnail != null) {
            imageViewUserImage.setImageBitmap(thumbnail);
        }

    }

    private void galleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            //try {
                //bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
            //imageViewUserImage.setImageBitmap(bm);
            Picasso
                .with(getBaseContext())
                .load(data.getData())
                .fit()
                .centerInside()
                .into(imageViewUserImage);
            selectedFile = data.getData().getPath();
        }
    }

    /*

     */
    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
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
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setPic() {

//		/* There isn't enough memory to open up more than a couple camera photos */
//		/* So pre-scale the target bitmap into which the file is decoded */
//
//		/* Get the size of the ImageView */
//        int targetW = imageViewUserImage.getWidth();
//        int targetH = imageViewUserImage.getHeight();
//
//		/* Get the size of the image */
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//		/* Figure out which way needs to be reduced less */
//        int scaleFactor = 1;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//        }
//
//		/* Set bitmap options to scale the image decode target */
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//		/* Decode the JPEG file into a Bitmap */
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//
//		/* Associate the Bitmap to the ImageView */
//        imageViewUserImage.setImageBitmap(bitmap);
//        //mVideoUri = null;
//        imageViewUserImage.setVisibility(View.VISIBLE);
//        //imageViewUserImage.setVisibility(View.INVISIBLE);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        selectedFile = contentUri.getPath();
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            Picasso
                .with(getBaseContext())
                .load("file:" + mCurrentPhotoPath)
                .fit()
                .centerInside()
                .into(imageViewUserImage);
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }
}
