package fi.oulu.fablab.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import retrofit.Callback;

import fi.oulu.fablab.myapplication1.models.Image;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ImageDetailActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE = "image";

    private Image mImage;
    ImageView imageViewImage;
    EditText editTextNotes;
    private boolean isUpdated;

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

        imageViewImage = (ImageView) findViewById(R.id.imageViewImage);
        editTextNotes = (EditText) findViewById(R.id.editTextNotes);

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
        ApiService service = MainActivity.API_CLIENT_NO_USERID.getApiService();

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

}
