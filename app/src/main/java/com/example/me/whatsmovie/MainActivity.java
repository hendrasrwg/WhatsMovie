package com.example.me.whatsmovie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.me.whatsmovie.adapter.MovieAdapter;
import com.example.me.whatsmovie.dagger.MainApplication;
import com.example.me.whatsmovie.helper.ItemClick;
import com.example.me.whatsmovie.model.PopulerResponse;
import com.example.me.whatsmovie.model.ResultsItem;
import com.example.me.whatsmovie.network.ApiInterface;
import com.example.me.whatsmovie.network.Network;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements ItemClick, SharedPreferences.OnSharedPreferenceChangeListener,RecyclerView.OnScrollChangeListener{

    @BindView(R.id.rv)RecyclerView rv;
    @BindView(R.id.pb) ProgressBar pb;
    private ArrayList<ResultsItem> list = new ArrayList<>();
    private ArrayList<ResultsItem> listSearch = new ArrayList<>();
    ProgressDialog progressBar;
    String category,judul;
    MovieAdapter adapter;
    int page=1;
    int totalpage;

    @Inject
    ApiInterface service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MainApplication application = (MainApplication) getApplication();
        application.getmApplicationComponent().inject(this);
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new MovieAdapter(list,MainActivity.this);
        adapter.setItemClick(MainActivity.this);
        rv.setAdapter(adapter);
        progressBar = new ProgressDialog(MainActivity.this);
        pb.setVisibility(View.GONE);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            rv.setLayoutManager(new GridLayoutManager(this,2));
        }else {
            rv.setLayoutManager(new GridLayoutManager(this,3));
        }
        if  (savedInstanceState != null && savedInstanceState.containsKey("list") && savedInstanceState.containsKey("judul")){
            list = savedInstanceState.getParcelableArrayList("list");
            judul = savedInstanceState.getString("judul");
            category = savedInstanceState.getString("category");
            page = savedInstanceState.getInt("page");
            getSupportActionBar().setTitle(judul);
            adapter = new MovieAdapter(list,MainActivity.this);
            adapter.setItemClick(MainActivity.this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else {
            setupSharedPreference();
        }
        rv.setOnScrollChangeListener(this);
    }

    private void setupSharedPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadCategoryFromPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadCategoryFromPreferences(SharedPreferences sharedPreferences) {
        category= sharedPreferences.getString(getString(R.string.pref_category_key),
                getString(R.string.pref_populer));
        String label="";
        if (category.equals("popular")){
            label ="Populer";
        }else if (category.equals("top_rated")){
            label ="Top Rate";
        }else if (category.equals("latest")){
            label ="Latest";
        }else if (category.equals("now_playing")){
            label ="Now Playing";
        }else if (category.equals("upcoming")){
            label ="Upcoming";
        }
        getSupportActionBar().setTitle(label+" Movie");
        page=1;
        //loadDataPopuler();
        loaddagger();
    }

    private void loaddagger(){
        if (page == 1){
            progressBar.setMessage("Loading....");
            progressBar.show();
            progressBar.setCancelable(false);
            list.clear();
        }else {
            pb.setVisibility(View.VISIBLE);
        }
        if (page == totalpage){

        }
        else {
            service.getJsonPopuler(category, BuildConfig.MOVIEAPI, page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PopulerResponse>() {
                        @Override
                        public void onCompleted() {
                            if (page == 1){
                                progressBar.hide();
                            }else {
                                pb.setVisibility(View.GONE);
                            }
                            page++;
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.hide();
                        }

                        @Override
                        public void onNext(PopulerResponse response) {
                            for (ResultsItem listItem : response.getResults()) {
                                list.add(listItem);
                            }
                            totalpage = response.getTotalPages();
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }


    private void loadDataPopuler(){
        if (page == 1){
            progressBar.setMessage("Loading....");
            progressBar.show();
            progressBar.setCancelable(false);
            list.clear();
        }else {
            //progressBar.setMessage("Load More....");
            pb.setVisibility(View.VISIBLE);
        }
        final ApiInterface apiInterface = Network.getClient().create(ApiInterface.class);
        //Observable<List<PopulerResponse>> call = apiInterface.getJsonPopuler();
        if (page == totalpage){

        }else {
            apiInterface.getJsonPopuler(category, BuildConfig.MOVIEAPI, page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PopulerResponse>() {
                        @Override
                        public void onCompleted() {
                            if (page == 1){
                                progressBar.hide();
                            }else {
                                pb.setVisibility(View.GONE);
                            }
                            page++;
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.hide();
                        }

                        @Override
                        public void onNext(PopulerResponse response) {
                            for (ResultsItem listItem : response.getResults()) {
                                list.add(listItem);
                            }
                            totalpage = response.getTotalPages();
                        /*adapter = new MovieAdapter(list,MainActivity.this);
                        adapter.setItemClick(MainActivity.this);
                        rv.setAdapter(adapter);*/
                        adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    @Override
    public void onItemClick(int id) {
        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra("id",String.valueOf(id));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
                break;
            case R.id.viewFav:
                startActivity(new Intent(MainActivity.this,FavoriteActivity.class));
                break;
            case R.id.search:
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrailerClick(String key) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuhome, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (list.size() != 0){
            outState.putParcelableArrayList("list",list);
            outState.putString("judul",getSupportActionBar().getTitle().toString());
            outState.putString("category",category);
            outState.putInt("page",page);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_category_key))) {
            loadCategoryFromPreferences(sharedPreferences);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }


    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (isLastItemDisplaying(rv)) {
           loadDataPopuler();
        }
    }
}
