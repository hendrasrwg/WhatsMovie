package com.example.me.whatsmovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.me.whatsmovie.MainActivity;
import com.example.me.whatsmovie.R;
import com.example.me.whatsmovie.config.BaseConfig;
import com.example.me.whatsmovie.helper.GlideClient;
import com.example.me.whatsmovie.helper.ItemClick;
import com.example.me.whatsmovie.model.PopulerResponse;
import com.example.me.whatsmovie.model.ResultsItem;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Me on 5/21/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<ResultsItem> model = new ArrayList<>();
    Context context;
    private ItemClick itemClick;

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public MovieAdapter(List<ResultsItem> model, Context context) {
        this.model = model;
        this.context = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_movie, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        MovieViewHolder forecastItemViewHolderToday = (MovieViewHolder) holder;
        ResultsItem data = model.get(position);
        forecastItemViewHolderToday.bind(data,context,itemClick);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.rating)
        SimpleRatingBar ratingBar;
        @BindView(R.id.tv)
        TextView textView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            //ratingBar.setNumStars(10);
            ratingBar.setMaxStarSize(10);
        }

        void bind(final ResultsItem response, Context context, final ItemClick itemClick) {
            GlideClient.downloadImage(context, BaseConfig.BASE2_URL+response.getPosterPath(),img);
            Float rating = Float.valueOf((float) response.getVoteAverage());
            ratingBar.setRating(rating);
            ratingBar.setClickable(false);
            textView.setText(String.valueOf(rating)+"/10");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.onItemClick(response.getId());
                }
            });
          //  ratingBar.setBackgroundColor(context.getColor(R.color.colorPrimary));
        }

    }

}
