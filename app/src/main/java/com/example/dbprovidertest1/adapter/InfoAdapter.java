package com.example.dbprovidertest1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dbprovidertest1.R;
import com.example.dbprovidertest1.Utils.LogUtil;

import java.util.List;


public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
    private static final String TAG = InfoAdapter.class.getSimpleName();
    private List<String> infos;

    public InfoAdapter(List<String> jobInfos) {
        this.infos = jobInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (infos != null && position >= infos.size()) {
            return;
        }
        LogUtil.i(TAG, "position: " + position + " jobInfos.get(position).toString(): " + infos.get(position));
        viewHolder.jobInfo.setText(infos.get(position));
    }

    @Override
    public int getItemCount() {
        return infos == null ? 0 : infos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobInfo;

        public ViewHolder(View view) {
            super(view);
            jobInfo = view.findViewById(R.id.data_info_item);
        }
    }
}
