package com.example.me.whatsmovie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.me.whatsmovie.adapter.MovieAdapter;
import com.example.me.whatsmovie.adapter.TrailerAdapter;
import com.example.me.whatsmovie.config.BaseConfig;
import com.example.me.whatsmovie.database.movieDBHelper;
import com.example.me.whatsmovie.helper.GlideClient;
import com.example.me.whatsmovie.helper.ItemClick;
import com.example.me.whatsmovie.model.DetailResponse;
import com.example.me.whatsmovie.model.ResultsItem;
import com.example.me.whatsmovie.modelTrailer.TrailerResponse;
import com.example.me.whatsmovie.network.ApiInterface;
import com.example.me.whatsmovie.network.Network;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity implements ItemClick{

    @BindView(R.id.toolbar_layout)CollapsingToolbarLayout coordinatorLayout;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.imgDetail)ImageView imgBackdrop;
    @BindView(R.id.tvSynopsis)TextView tvSynopsis;
    @BindView(R.id.tvRelease)TextView tvRelease;
    @BindView(R.id.tvRunTime)TextView tvRuntime;
    @BindView(R.id.tvLanguage)TextView tvLanguage;
    @BindView(R.id.tvRating)TextView tvRating;
    @BindView(R.id.tvGenre)TextView tvGenre;
    @BindView(R.id.rv)RecyclerView rv;
    @BindView(R.id.app_bar)AppBarLayout appBarLayout;
    @BindView(R.id.imgPoster2)ImageView image;
    @BindView(R.id.fabComent)FloatingActionButton floatingActionButton;
    @BindView(R.id.tvJudul)TextView tvJudul;
    ProgressDialog progressBar;
    String id,judul,poster;
    private Gson gson = new Gson();
    double averagevote;
    int idMovie=0;
    boolean collapse = true;
    private movieDBHelper dbHelper;
    DetailResponse responses = null;
    TrailerAdapter adapter;
    private ArrayList<com.example.me.whatsmovie.modelTrailer.ResultsItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        Intent intent = this.getIntent();
        id = intent.getExtras().getString("id");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new TrailerAdapter(list);
        rv.setAdapter(adapter);
        adapter.setItemClick(DetailActivity.this);
        if (savedInstanceState !=null && savedInstanceState.containsKey("listtrailer") && savedInstanceState.containsKey("list")){
            String title = savedInstanceState.getString("title");
            list = savedInstanceState.getParcelableArrayList("listtrailer");
            String jsondata = savedInstanceState.getString("list");
            getSupportActionBar().setTitle(title);
            responses = gson.fromJson(jsondata, DetailResponse.class);
            bind(responses);
            adapter = new TrailerAdapter(list);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else {
            loadDetail();
        }
        dbHelper = new movieDBHelper(this);
    }

    private void loadDetail(){
        progressBar = new ProgressDialog(DetailActivity.this);
        progressBar.setMessage("Get Detail....");
        progressBar.show();
        ApiInterface apiInterface = Network.getClient().create(ApiInterface.class);
        apiInterface.getJsonDetail(id,BuildConfig.MOVIEAPI)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DetailResponse>() {
                    @Override
                    public void onCompleted() {
                        progressBar.hide();
                        loadList();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(DetailResponse response) {
                        bind(response);
                        responses = response;
                    }
                });
    }

    private void bind(DetailResponse response){
        GlideClient.downloadImage(DetailActivity.this, BaseConfig.BASE_URL_BACKDROP+response.getBackdropPath(),imgBackdrop);
        GlideClient.downloadImage(DetailActivity.this, BaseConfig.BASE2_URL+response.getPosterPath(),image);
        tvSynopsis.setText(response.getOverview());
        tvRelease.setText(response.getReleaseDate());
        tvLanguage.setText(response.getSpokenLanguages().get(0).getName());
        int runtime = response.getRuntime();
        String hour = String.valueOf(runtime/60)+" hour";
        String minute = String.valueOf(runtime%60);
        String minute2="";
        if (minute.equals("0")){
            minute2="";
        }else {
            minute2=minute+" minute";
        }
        tvRuntime.setText(hour+" "+minute2);
        tvRating.setText(String.valueOf(response.getVoteAverage()));

        if  (response.getGenres().size() != 0){
            for (int i =0;i<response.getGenres().size();i++){
                if (i==0){
                    tvGenre.setText("#"+response.getGenres().get(i).getName());
                }else {
                    tvGenre.append(", #"+response.getGenres().get(i).getName());
                }
            }
        }
       judul = response.getTitle();
        idMovie = response.getId();
        poster =response.getPosterPath();
        averagevote = response.getVoteAverage();
        coordinatorLayout.setTitle(" ");
        tvJudul.setText(response.getTitle());
    }


    private void loadList(){
        ApiInterface apiInterface = Network.getClient().create(ApiInterface.class);
        apiInterface.getListTrailer(id,BuildConfig.MOVIEAPI)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TrailerResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(TrailerResponse trailerResponse) {
                        for (com.example.me.whatsmovie.modelTrailer.ResultsItem listItem : trailerResponse.getResults()){
                            list.add(listItem);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(int id) {

    }

    @OnClick(R.id.fabComent)
    public void showReview(){
        Intent intent = new Intent(DetailActivity.this,ReviewsActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void onTrailerClick(String key) {
        Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BaseConfig.BASE_URL_YOUTUBE+key)));
    }


    private void saveForecastToDB(int id, String poster, double vote) {
        dbHelper.saveForecast(id, poster, vote);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.beforefavorite:
                saveForecastToDB(idMovie, poster, averagevote);
                Toast.makeText(this, "this movie add to favorite", Toast.LENGTH_SHORT).show();
                supportInvalidateOptionsMenu();
                //onPrepareOptionsMenu(menu);
                break;
            case R.id.afterfavorite:
                dbHelper.deleteForUpdate(idMovie);
                Toast.makeText(this, "remove from favorite", Toast.LENGTH_SHORT).show();
                supportInvalidateOptionsMenu();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.share:
                onShareItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        idMovie = Integer.parseInt(id);
        if (!dbHelper.isDataAlreadyExist(idMovie)) {
            getMenuInflater().inflate(R.menu.menufav2, menu);
        }else {
            getMenuInflater().inflate(R.menu.menufav1, menu);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (list.size() != 0){
            outState.putString("list",gson.toJson(responses));
            outState.putParcelableArrayList("listtrailer",list);
            outState.putString("title",getSupportActionBar().getTitle().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void supportInvalidateOptionsMenu() {
        super.supportInvalidateOptionsMenu();
    }

    public void onShareItem() {
        ImageView imgflag = (ImageView) findViewById(R.id.imgPoster2);
        imgflag.buildDrawingCache();
        Bitmap bm=imgflag.getDrawingCache();

        OutputStream fOut = null;
        Uri outputFileUri;

        try {
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "FoodMana" + File.separator);
            root.mkdirs();
            File sdImageMainDirectory = new File(root, "myPicName.jpg");
            outputFileUri = Uri.fromFile(sdImageMainDirectory);
            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }

        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }

        try {
            PackageManager pm=getPackageManager();
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/png");
            waIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            waIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory()
                    + File.separator + "FoodMana" + File.separator+"lookThisMovie.jpg"));
            startActivity(Intent.createChooser(waIntent, "Share with"));
        }catch (Exception x) {
            Toast.makeText(this, x.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
