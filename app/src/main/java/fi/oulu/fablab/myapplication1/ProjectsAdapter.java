package fi.oulu.fablab.myapplication1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fi.oulu.fablab.myapplication1.models.Content;

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
        MainActivity.ProjectViewHolder viewHolder = new MainActivity.ProjectViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainActivity.ProjectViewHolder holder, int position) {
        Content project = mProjectList.get(position);
        holder.textViewProjectName.setText(project.getTitle());
        holder.textViewProjectId.setText(String.valueOf(project.getId()));
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
