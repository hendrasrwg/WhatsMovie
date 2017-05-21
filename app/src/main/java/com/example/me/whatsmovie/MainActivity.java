package com.example.me.whatsmovie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.example.me.whatsmovie.adapter.MovieAdapter;
import com.example.me.whatsmovie.helper.ItemClick;
import com.example.me.whatsmovie.model.PopulerResponse;
import com.example.me.whatsmovie.model.ResultsItem;
import com.example.me.whatsmovie.network.ApiInterface;
import com.example.me.whatsmovie.network.Network;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements ItemClick{

    @BindView(R.id.rv)RecyclerView rv;
    private List<ResultsItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());
        loadDataPopuler();
    }

    private void loadDataPopuler(){
        ApiInterface apiInterface = Network.getClient().create(ApiInterface.class);
        //Observable<List<PopulerResponse>> call = apiInterface.getJsonPopuler();
        apiInterface.getJsonPopuler()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PopulerResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(PopulerResponse response) {
                        for (ResultsItem listItem : response.getResults()){
                            list.add(listItem);
                        }
                        MovieAdapter adapter = new MovieAdapter(list,MainActivity.this);
                        rv.setAdapter(adapter);
                        adapter.setItemClick(MainActivity.this);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(int id) {
        Toast.makeText(this, "id "+id+" clicked", Toast.LENGTH_SHORT).show();
    }
}
