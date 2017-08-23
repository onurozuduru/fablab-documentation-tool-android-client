package fi.oulu.fablab.myapplication1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;

import fi.oulu.fablab.myapplication1.models.Content;
import retrofit.Callback;

import fi.oulu.fablab.myapplication1.models.Image;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class ImageDetailActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE = "image";
    private final int REQUEST_AUDIO = 3;

    private Image mImage;
    ImageView imageViewImage;
    EditText editTextNotes;
    Button buttonRecord;
    Button buttonPlay;
    private boolean isUpdated;

    MultiplePermissionsListener multiplePermissionsListener;
    Uri audioFileUri = null;
    Content mProject;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getIntent().putExtra(ProjectDetailActivity.EXTRA_PROJECT, getIntent().getParcelableExtra(ProjectDetailActivity.EXTRA_PROJECT));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        isUpdated = true;
        if (getIntent().hasExtra(EXTRA_IMAGE)) {
            mImage = getIntent().getParcelableExtra(EXTRA_IMAGE);
        } else {
            throw new IllegalArgumentException("Detail activity must receive an image parcelable");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Notes");
        setSupportActionBar(toolbar);

        mProject = getIntent().getParcelableExtra(ProjectDetailActivity.EXTRA_PROJECT);

        imageViewImage = (ImageView) findViewById(R.id.imageViewImage);
        editTextNotes = (EditText) findViewById(R.id.editTextNotes);

        buttonPlay = (Button) findViewById(R.id.PlayButton);
        buttonRecord = (Button) findViewById(R.id.RecordButton);
        buttonPlay.setEnabled(false);
        setListeners();

        editTextNotes.setText(mImage.getNotes());
        Picasso.with(this)
                .load(mImage.getImagepath())
                .into(imageViewImage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                saveNotes(view);
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void saveNotes(final View view) {
        if(audioFileUri != null) {
            uploadVoice(view);
        }

        ApiService service = MainActivity.API_CLIENT.getApiService(); //MainActivity.API_CLIENT_NO_USERID

        String notes = editTextNotes.getText().toString().trim();
        String id = String.valueOf(mImage.getId());

        if(!TextUtils.isEmpty(notes)) {
            mImage.setNotes(notes);

            service.saveNotes(id, "application/json", mImage, new Callback<Image>() {
                @Override
                public void success(Image image, Response response) {
                    Snackbar.make(view, "Saved", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    isUpdated = false;
                }

                @Override
                public void failure(RetrofitError error) {
                    Snackbar.make(view, "Something went wrong!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

    }

    private void uploadVoice(final View view) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(VoiceUploadService.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        VoiceUploadService service = restAdapter.create(VoiceUploadService.class);
        String filePath = _getRealPathFromURI(this, audioFileUri);
        // Create file for multipart form.
        TypedFile typedFile = new TypedFile("multipart/form-data", new File(filePath));

        String imageid = String.valueOf(mImage.getId());
        String postid = String.valueOf(mProject.getId());

        service.uploadVoice(typedFile, postid, imageid, new Callback<fi.oulu.fablab.myapplication1.models.File>() {
            @Override
            public void success(fi.oulu.fablab.myapplication1.models.File file, Response response) {
                audioFileUri = null;
                Snackbar.make(view, "Audio is Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });


    }

    public void setListeners() {
        // Permission listener to get permissions dynamically by Dexter.
        multiplePermissionsListener = SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                .with((ViewGroup) findViewById(android.R.id.content), "Voice record and storage access is needed to record voice")
                .withOpenSettingsButton("Settings")
                .withCallback(new Snackbar.Callback() {
                    @Override
                    public void onShown(Snackbar snackbar) {
                        // Event handler for when the given Snackbar has been dismissed
                        startRecordIntent();
                    }
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        // Event handler for when the given Snackbar is visible
                        super.onDismissed(snackbar, event);
                    }
                })
                .build();

        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First check for permissions for Android 6+
                int currentAPIVersion = Build.VERSION.SDK_INT;
                if(currentAPIVersion>=android.os.Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ImageDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // Use Dexter to grant permissions
                        Dexter.withActivity(ImageDetailActivity.this)
                                .withPermissions(
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ).withListener(multiplePermissionsListener).check();
                    }
                    else {
                        // If permissions have been already granted, open voice recorder.
                        startRecordIntent();
                    }
                }
                else {
                    // If it is less than Android 6.0
                    startRecordIntent();
                }
            }
        });

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mediaPlayer = MediaPlayer.create(ImageDetailActivity.this, audioFileUri);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        buttonPlay.setEnabled(true);
                    }
                });
                mediaPlayer.start();
                buttonPlay.setEnabled(false);
            }
        });
    }

    public void startRecordIntent() {
        Intent intent = new Intent(
                MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, REQUEST_AUDIO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_AUDIO) {
                audioFileUri = data.getData();
                buttonPlay.setEnabled(true);
            }
        }
    }

    private String _getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
