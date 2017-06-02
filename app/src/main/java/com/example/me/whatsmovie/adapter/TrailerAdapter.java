package com.example.me.whatsmovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.me.whatsmovie.R;
import com.example.me.whatsmovie.config.BaseConfig;
import com.example.me.whatsmovie.helper.GlideClient;
import com.example.me.whatsmovie.helper.ItemClick;
import com.example.me.whatsmovie.model.ResultsItem;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Me on 5/22/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<com.example.me.whatsmovie.modelTrailer.ResultsItem> model = new ArrayList<>();
    private ItemClick itemClick;

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public TrailerAdapter(List<com.example.me.whatsmovie.modelTrailer.ResultsItem> model) {
        this.model = model;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_trailer,parent,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        TrailerAdapter.TrailerViewHolder adapter = (TrailerAdapter.TrailerViewHolder) holder;
        com.example.me.whatsmovie.modelTrailer.ResultsItem data = model.get(position);
        adapter.bind(data,itemClick,position);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvTrailer)
        TextView textView;
        @BindView(R.id.bottom)
        LinearLayout linearLayout;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(final com.example.me.whatsmovie.modelTrailer.ResultsItem resultsItem, final ItemClick itemClick, int position) {
            textView.setText(resultsItem.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.onTrailerClick(resultsItem.getKey());
                }
            });
            if (position+1 == model.size()){
                linearLayout.setVisibility(View.GONE);
            }
        }

    }
}
