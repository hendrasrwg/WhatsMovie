package com.example.me.whatsmovie;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.me.whatsmovie.adapter.MovieAdapter;
import com.example.me.whatsmovie.database.movieContract;
import com.example.me.whatsmovie.database.movieDBHelper;
import com.example.me.whatsmovie.helper.ItemClick;
import com.example.me.whatsmovie.model.PopulerResponse;
import com.example.me.whatsmovie.model.ResultsItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteActivity extends AppCompatActivity implements ItemClick{

    @BindView(R.id.rv)RecyclerView rv;
    private ArrayList<ResultsItem> list = new ArrayList<>();
    private movieDBHelper dbHelper;
    protected Cursor cursor;
    private MovieAdapter adapter;
    private PopulerResponse populerResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);
        dbHelper = new movieDBHelper(this);
        getSupportActionBar().setTitle("Favorite Movie");
        adapter = new MovieAdapter(list,FavoriteActivity.this);
        adapter.setItemClick(FavoriteActivity.this);
        rv.setAdapter(adapter);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            rv.setLayoutManager(new GridLayoutManager(this,2));
        }else {
            rv.setLayoutManager(new GridLayoutManager(this,3));
        }
        rv.setItemAnimator(new DefaultItemAnimator());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState != null && savedInstanceState.containsKey("list")){
            list = savedInstanceState.getParcelableArrayList("list");
            adapter = new MovieAdapter(list,FavoriteActivity.this);
            adapter.setItemClick(FavoriteActivity.this);
            rv.setAdapter(adapter);
        }else {
            if (dbHelper.isDataAlreadyExist(0)) {
                onResume();
            }else{
                Toast.makeText(this, "you not have a favorite movie", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDataFromDB() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("select ID_MOVIE, poster, vote from favorite", null);
        //cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            list.clear();
            while (cursor.moveToNext()) {
                ResultsItem item = new ResultsItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(movieContract.MovieEntry.COLUMN_ID_MOVIE)));
                item.setPosterPath(cursor.getString(cursor.getColumnIndex(movieContract.MovieEntry.COLUMN_POSTER)));
                item.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(movieContract.MovieEntry.COLUMN_VOTE)));
                list.add(item);
            }
        }else {

        }
        adapter.notifyDataSetChanged();
        cursor.close();
        db.close();
    }

    @Override
    protected void onResume() {
        showDataFromDB();
        super.onResume();
    }

    @Override
    public void onItemClick(int id) {
        Intent intent = new Intent(FavoriteActivity.this,DetailActivity.class);
        intent.putExtra("id",String.valueOf(id));
        startActivity(intent);
    }

    @Override
    public void onTrailerClick(String key) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (list.size() != 0){
            outState.putParcelableArrayList("list",list);
        }
        super.onSaveInstanceState(outState);
    }
}
