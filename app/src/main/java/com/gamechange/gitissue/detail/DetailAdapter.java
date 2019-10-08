package com.gamechange.gitissue.detail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechange.gitissue.R;
import com.gamechange.gitissue.model.CommentsResponse;

import java.util.ArrayList;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CommentsResponse> data;

    public DetailAdapter() {
        data= new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        viewHolder = new ViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_comment, viewGroup, false));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int i) {
        DetailAdapter.ViewHolder viewHolder = (DetailAdapter.ViewHolder) holder;
        viewHolder.bind(data.get(i));

    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{
        private ViewDataBinding binding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }


        public void bind(Object obj) {
            binding.setVariable(BR.comment, obj);
            binding.executePendingBindings();
        }
    }


    public void add(CommentsResponse response) {
        data.add(response);
        notifyItemInserted(data.size() - 1);
    }


    public void addAll(List <CommentsResponse > mcList) {
        for (CommentsResponse response: mcList) {
            add(response);
        }
    }

}
