package com.gamechange.gitissue.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechange.gitissue.R;
import com.gamechange.gitissue.model.IssueResponse;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<IssueResponse> data;

    private onPressListener listener;

    // flag for footer ProgressBar (i.e. last item of list)
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    public interface  onPressListener{
        void onClick(String titlr, String comment);
    }

    public MainAdapter( onPressListener listner) {
        data= new ArrayList<>();
        this.listener =listner;
    }

    public List<IssueResponse> getIssues() {
        return data;
    }

    public void setIssues(List<IssueResponse> issueResponses) {
        this.data = issueResponses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (i) {
            case ITEM:
                viewHolder = new ViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_issue, viewGroup, false));
                break;
            case LOADING:
                viewHolder = new Loading(DataBindingUtil.inflate(inflater, R.layout.item_progress, viewGroup, false).getRoot());
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int i) {

        switch (getItemViewType(i)) {
            case ITEM:
                MainAdapter.ViewHolder viewHolder = (MainAdapter.ViewHolder) holder;
                viewHolder.bind(data.get(i),listener);

                break;
            case LOADING:
//                Do nothing
                break;
        }
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    @Override
    public int getItemViewType(int position) {
        return (position == data.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{
        private ViewDataBinding binding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }


        public void bind(Object obj,onPressListener listener) {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onClick(((IssueResponse)obj).getTitle(), ((IssueResponse)obj).getCommentsUrl());
                }
            });

            binding.setVariable(BR.issue, obj);
            binding.executePendingBindings();
        }


    }

    protected class Loading extends RecyclerView.ViewHolder {

        public Loading(View itemView) {
            super(itemView);
        }
    }

    public void add(IssueResponse response) {
        data.add(response);
        notifyItemInserted(data.size() - 1);
    }



    public void addAll(List < IssueResponse > mcList) {
        for (IssueResponse response: mcList) {
            add(response);
        }
    }

    //    public void remove(IssueResponse issueResponse) {
//        int position = data.indexOf(issueResponse);
//        if (position > -1) {
//            data.remove(position);
//            notifyItemRemoved(position);
//        }
//    }

//    public void clear() {
//        isLoadingAdded = false;
//        while (getItemCount() > 0) {
//            remove(getItem(0));
//        }
//    }
//
//    public boolean isEmpty() {
//        return getItemCount() == 0;
//    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new IssueResponse());
    }

    public void removeLoadingFooter() {
        if(!data.isEmpty()) {
            isLoadingAdded = false;

            int position = data.size() - 1;
            IssueResponse item = getItem(position);
            if (item != null) {
                data.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public IssueResponse getItem(int position) {
        return data.get(position);
    }
}
