package com.example.me.whatsmovie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.me.whatsmovie.adapter.MovieAdapter;
import com.example.me.whatsmovie.adapter.ReviewAdapter;
import com.example.me.whatsmovie.model.ResultsItem;
import com.example.me.whatsmovie.modelReviews.ReviewsResponse;
import com.example.me.whatsmovie.modelTrailer.TrailerResponse;
import com.example.me.whatsmovie.network.ApiInterface;
import com.example.me.whatsmovie.network.Network;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReviewsActivity extends AppCompatActivity {

    @BindView(R.id.rv)
    RecyclerView rv;
    private ArrayList<com.example.me.whatsmovie.modelReviews.ResultsItem> list = new ArrayList<>();
    String id;
    ProgressDialog progressDialog;
    ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ButterKnife.bind(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        getSupportActionBar().setTitle("Review Movie");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = this.getIntent();
        id = i.getExtras().getString("id");
        adapter = new ReviewAdapter(list);
        rv.setAdapter(adapter);
        if (savedInstanceState != null && savedInstanceState.containsKey("list")){
            list = savedInstanceState.getParcelableArrayList("list");
            adapter.notifyDataSetChanged();
        }else {
            loadReview();
        }
    }

    private void loadReview(){
        progressDialog = new ProgressDialog(ReviewsActivity.this);
        progressDialog.setMessage("Get Reviewss");
        progressDialog.show();
        ApiInterface apiInterface = Network.getClient().create(ApiInterface.class);
        apiInterface.getListReview(id,BuildConfig.MOVIEAPI)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReviewsResponse>() {
                    @Override
                    public void onCompleted() {
                        progressDialog.hide();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ReviewsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ReviewsResponse response) {
                        for (com.example.me.whatsmovie.modelReviews.ResultsItem listItem : response.getResults()){
                            list.add(listItem);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (list.size() != 0){
            outState.putParcelableArrayList("list",list);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
