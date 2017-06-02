package com.example.me.whatsmovie.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.me.whatsmovie.R;
import com.example.me.whatsmovie.helper.ItemClick;
import com.example.me.whatsmovie.model.ResultsItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Me on 5/24/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    List<com.example.me.whatsmovie.modelReviews.ResultsItem> list = new ArrayList<>();

    public ReviewAdapter(List<com.example.me.whatsmovie.modelReviews.ResultsItem> list) {
        this.list = list;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_review,parent,false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        ReviewViewHolder adapter = (ReviewViewHolder) holder;
        com.example.me.whatsmovie.modelReviews.ResultsItem item = list.get(position);
        adapter.bind(item);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvReview) TextView tvReview;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(com.example.me.whatsmovie.modelReviews.ResultsItem resultsItem) {
            tvName.setText(resultsItem.getAuthor());
            tvReview.setText(resultsItem.getContent());
        }

    }

}
