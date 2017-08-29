package fi.oulu.fablab.myapplication1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fi.oulu.fablab.myapplication1.models.Content;
import fi.oulu.fablab.myapplication1.models.Page;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ProjectsAdapter mAdapter;
    private Integer totalPages;
    private Integer currentPage;
    public static ApiClient API_CLIENT;
    public static ApiClient API_CLIENT_NO_USERID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        API_CLIENT = new ApiClient("4");
        API_CLIENT_NO_USERID = new ApiClient();

        totalPages = 1;
        currentPage = 1;

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewProjectList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ProjectsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        setRestAdaptor();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewProject();
                Snackbar.make(view, "Project is created!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRestAdaptor();
    }

    public void setRestAdaptor() {
        // Get Content list from response.
        //Since there is pagination on response, 2 callbacks are needed.
        //  First callback is for the first page and other are for the additional pages if it is needed.
        //  Even it looks ugly 2 callbacks are needed in any case. Creating new classes, functions, etc.
        //      will not change the fact that 2 callbacks are needed.

        totalPages = 1;
        currentPage = 1;
        final ApiService service = API_CLIENT.getApiService();
        // First callback to see how many pages are in the response.
        service.getProjectList(currentPage, new Callback<Page<Content>>() {
            @Override
            public void success(Page<Content> contentPage, Response response) {
                totalPages = contentPage.getPages();
                currentPage = contentPage.getPage();
                final List<Content> items = contentPage.getItems();
                // If more callbacks are needed, handle them.
                while(currentPage <= totalPages) {
                    service.getProjectList(++currentPage, new Callback<Page<Content>>() {
                        @Override
                        public void success(Page<Content> contentPage, Response response) {
                            totalPages = contentPage.getPages();
                            currentPage = contentPage.getPage();
                            items.addAll(contentPage.getItems());
                            // Create list and make it visible to user.
                            mAdapter.setProjectList(items);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }


    private void createNewProject() {
        ApiService service = API_CLIENT.getApiService();
        Content newProject = new Content("", Integer.parseInt(API_CLIENT.getUserid()), "New Project");
        service.createProject("application/json", newProject, new Callback<Content>() {
            @Override
            public void success(Content content, Response response) {
                setRestAdaptor();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewProjectName;
        public TextView textViewProjectId;
        public Button buttonRemoveProject;

        public ProjectViewHolder(View itemView)
        {
            super(itemView);
            textViewProjectName = (TextView) itemView.findViewById(R.id.textViewProjectName);
            textViewProjectId = (TextView) itemView.findViewById(R.id.textViewProjectId);
            buttonRemoveProject = (Button) itemView.findViewById(R.id.buttonProjectDelete);
        }
    }

    public class ProjectsAdapter extends RecyclerView.Adapter<MainActivity.ProjectViewHolder> {
        private List<Content> mProjectList;
        private LayoutInflater mInflater;
        private Context mContext;

        public ProjectsAdapter(Context context)
        {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
            this.mProjectList = new ArrayList<>();
        }

        @Override
        public MainActivity.ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_project, parent, false);
            final MainActivity.ProjectViewHolder viewHolder = new MainActivity.ProjectViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = viewHolder.getAdapterPosition();
                    Intent intent = new Intent(mContext, ProjectDetailActivity.class);
                    intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT, mProjectList.get(position));
                    mContext.startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MainActivity.ProjectViewHolder holder, final int position) {
            final Content project = mProjectList.get(position);
            holder.textViewProjectName.setText(project.getTitle());
            holder.textViewProjectId.setText(String.valueOf(project.getId()));
            holder.buttonRemoveProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    // Ask users if they sure about their actions.
                    new AlertDialog.Builder(mContext)
                            .setTitle("Remove Project")
                            .setMessage("Do you really want to remove project?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // If user approve, remove the project and update list.
                                    ApiService service = MainActivity.API_CLIENT.getApiService();
                                    service.removeProject(String.valueOf(project.getId()), new Callback<Void>() {
                                        @Override
                                        public void success(Void aVoid, Response response) {
                                            Snackbar.make(view, "Project is removed!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            //Remove from list
                                            mProjectList.remove(position);
                                            //Update the list.
                                            setRestAdaptor();
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
            return (mProjectList == null) ? 0 : mProjectList.size();
        }

        public void setProjectList(List<Content> projectList) {
            this.mProjectList.clear();
            this.mProjectList.addAll(projectList);
            notifyDataSetChanged();
        }

    }

}
