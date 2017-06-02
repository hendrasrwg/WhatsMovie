package com.example.me.whatsmovie;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.me.whatsmovie.adapter.MovieAdapter;
import com.example.me.whatsmovie.helper.ItemClick;
import com.example.me.whatsmovie.model.PopulerResponse;
import com.example.me.whatsmovie.model.ResultsItem;
import com.example.me.whatsmovie.network.ApiInterface;
import com.example.me.whatsmovie.network.Network;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,ItemClick{

    @BindView(R.id.rv)
    RecyclerView rv;
    MenuItem menuItem;
    SearchView searchView;
    Menu menu;
    ProgressDialog progressBar;
    private ArrayList<ResultsItem> listSearch = new ArrayList<>();
    MovieAdapter adapter;
    String query="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            rv.setLayoutManager(new GridLayoutManager(this,2));
        }else {
            rv.setLayoutManager(new GridLayoutManager(this,3));
        }
        rv.setItemAnimator(new DefaultItemAnimator());
        if (savedInstanceState != null && savedInstanceState.containsKey("list") && savedInstanceState.containsKey("key")){
            query= savedInstanceState.getString("key");
            listSearch = savedInstanceState.getParcelableArrayList("list");
            adapter = new MovieAdapter(listSearch,SearchActivity.this);
            adapter.setItemClick(SearchActivity.this);
            rv.setAdapter(adapter);
        }
        progressBar = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusearch, menu);
        this.menu = menu;
        menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        MenuItemCompat.expandActionView(menuItem); /* auto focus */
        if (query.equals("")){

        }else {
            searchView.setQuery(query,false);
        }
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener(){
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return false;
            }
        });

        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        SearchData(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void SearchData(String query){
        progressBar = new ProgressDialog(SearchActivity.this);
        progressBar.setMessage("Search....");
        progressBar.show();
        listSearch.clear();
        ApiInterface apiInterface = Network.getClient().create(ApiInterface.class);
        apiInterface.getListSearch(BuildConfig.MOVIEAPI,query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PopulerResponse>() {
                    @Override
                    public void onCompleted() {
                        progressBar.hide();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(SearchActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(PopulerResponse response) {
                        for (ResultsItem listItem : response.getResults()){
                            listSearch.add(listItem);
                        }
                        adapter = new MovieAdapter(listSearch,SearchActivity.this);
                        adapter.setItemClick(SearchActivity.this);
                        rv.setAdapter(adapter);
                    }
                });
    }

    @Override
    public void onItemClick(int id) {
        Intent intent = new Intent(SearchActivity.this,DetailActivity.class);
        intent.putExtra("id",String.valueOf(id));
        Toast.makeText(this, "id "+id+" clicked", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    public void onTrailerClick(String key) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if  (listSearch.size() != 0){
            outState.putParcelableArrayList("list",listSearch);
            outState.putString("key",searchView.getQuery().toString());
        }
        super.onSaveInstanceState(outState);
    }
}
