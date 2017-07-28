package fi.oulu.fablab.myapplication1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import fi.oulu.fablab.myapplication1.models.Content;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ProjectsAdapter mAdapter;
    public static ApiClient API_CLIENT;
    public static ApiClient API_CLIENT_NO_USERID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        API_CLIENT = new ApiClient("1");
        API_CLIENT_NO_USERID = new ApiClient();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewProjectList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ProjectsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        setRestAdaptor();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRestAdaptor();
    }

    private void setRestAdaptor() {
        ApiService service = API_CLIENT.getApiService();
        service.getProjectList(new Callback<Content.ContentItems>() {
            @Override
            public void success(Content.ContentItems projectResult, Response response) {
                mAdapter.setProjectList(projectResult.getItems());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
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

}
