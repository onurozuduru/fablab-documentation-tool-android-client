package fi.oulu.fablab.myapplication1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fi.oulu.fablab.myapplication1.models.Content;
import fi.oulu.fablab.myapplication1.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProjectDetailActivity extends AppCompatActivity {
    public static final String EXTRA_PROJECT = "project";

    private RecyclerView mRecyclerView;
    private ProjectDetailAdapter mAdapter;

    private Content mProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        mProject = getIntent().getParcelableExtra(EXTRA_PROJECT);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewProjectDetail);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ProjectDetailAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        if(savedInstanceState != null) {
            mProject = savedInstanceState.getParcelable("project");
        }
        mAdapter.setImageList(mProject.getImages());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mProject.getTitle());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(view.getContext(), PhotoUploadActivity.class);
                intent.putExtra("id", String.valueOf(mProject.getId()));
                view.getContext().startActivity(intent);
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProject();
    }

    private void updateProject() {
        ApiService service = MainActivity.API_CLIENT.getApiService();
        service.getProject(String.valueOf(mProject.getId()), new Callback<Content>() {
            @Override
            public void success(Content projectResult, Response response) {
                mProject = projectResult;
                mAdapter.setImageList(mProject.getImages());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewThumbnail;
        public TextView textViewId;
        public Button buttonRemoveImage;

        public ImageViewHolder(View itemView)
        {
            super(itemView);
            imageViewThumbnail = (ImageView) itemView.findViewById(R.id.imageViewThumbnail);
            textViewId = (TextView) itemView.findViewById(R.id.textViewImageId);
            buttonRemoveImage = (Button) itemView.findViewById(R.id.buttonImageDelete);
        }
    }

    public class ProjectDetailAdapter extends RecyclerView.Adapter<ImageViewHolder> {
        private List<Image> mImageList;
        private LayoutInflater mInflater;
        private Context mContext;

        public ProjectDetailAdapter(Context context)
        {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
            this.mImageList = new ArrayList<>();
        }

        @Override
        public ProjectDetailActivity.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_image, parent, false);
            final ImageViewHolder viewHolder = new ImageViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = viewHolder.getAdapterPosition();
                    Intent intent = new Intent(mContext, ImageDetailActivity.class);
                    intent.putExtra(ImageDetailActivity.EXTRA_IMAGE, mImageList.get(position));
                    intent.putExtra(EXTRA_PROJECT, mProject);
                    mContext.startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, final int position) {
            final Image image = mImageList.get(position);
            holder.textViewId.setText(String.valueOf(image.getId()));
            Picasso.with(mContext)
                    .load(image.getThumbpath())
                    .into(holder.imageViewThumbnail);

            holder.buttonRemoveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    // Ask users if they sure about their actions.
                    new AlertDialog.Builder(mContext)
                            .setTitle("Remove Image")
                            .setMessage("Do you really want to remove image?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // If user approve, remove the image and update list.
                                    ApiService service = MainActivity.API_CLIENT.getApiService();
                                    service.removeImage(String.valueOf(image.getId()), new Callback<Void>() {
                                        @Override
                                        public void success(Void aVoid, Response response) {
                                            //Remove from list
                                            mImageList.remove(position);
                                            //Update the list.
                                            updateProject();
                                            // Inform user about consequences of their actions.
                                            Snackbar.make(view, "Image is removed!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {

                                        }
                                    });
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return (mImageList == null) ? 0 : mImageList.size();
        }

        public void setImageList(List<Image> imageList) {
            this.mImageList.clear();
            this.mImageList.addAll(imageList);
            notifyDataSetChanged();
        }
    }
}
