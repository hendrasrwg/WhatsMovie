package com.example.me.whatsmovie.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.me.whatsmovie.FavoriteActivity;
import com.example.me.whatsmovie.adapter.MovieAdapter;
import com.example.me.whatsmovie.model.DetailResponse;
import com.example.me.whatsmovie.model.PopulerResponse;
import com.example.me.whatsmovie.model.ResultsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Me on 5/23/2017.
 */

public class movieDBHelper extends SQLiteOpenHelper {

    private static final String TAG = movieDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "whatmovie.db";
    private static final int DATABASE_VERSION = 1;
    private PopulerResponse result;

    public movieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DATABASE_SQL = "CREATE TABLE " + movieContract.MovieEntry.TABLE_NAME + " ("
                + movieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + movieContract.MovieEntry.COLUMN_ID_MOVIE + " INTEGER, "
                + movieContract.MovieEntry.COLUMN_POSTER + " TEXT, "
                + movieContract.MovieEntry.COLUMN_VOTE + " DOUBLE);";

        db.execSQL(CREATE_DATABASE_SQL);
    }

    public PopulerResponse showData(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<ResultsItem> resultsItems = new ArrayList<>();
        result = new PopulerResponse();
        result.setResults(resultsItems);
        Cursor cursor = db.query(movieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        //cursor.moveToFirst();
        ResultsItem item = new ResultsItem();
        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()){
                do {
                    result.getResults().get(0).setId(cursor.getInt(cursor.getColumnIndex(movieContract.MovieEntry.COLUMN_ID_MOVIE)));
                    result.getResults().get(0).setPosterPath(cursor.getString(cursor.getColumnIndex(movieContract.MovieEntry.COLUMN_POSTER)));
                    result.getResults().get(0).setVoteAverage(cursor.getDouble(cursor.getColumnIndex(movieContract.MovieEntry.COLUMN_VOTE)));
                    result.getResults().add(item);
                }while (cursor.moveToNext());
            }
            /*for (int i = 0;i<cursor.getCount();i++){
                cursor.moveToPosition(i);
                item.setId(cursor.getInt(i));
                item.setPosterPath(cursor.getString(i));
                item.setVoteAverage(cursor.getDouble(i));
                list.add(item);
            }*/
        }else {

        }
        cursor.close();
        db.close();
        return result;
    }

    public void saveForecast(int id, String poster, double vote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(movieContract.MovieEntry.COLUMN_ID_MOVIE, id);
        cv.put(movieContract.MovieEntry.COLUMN_POSTER, poster);
        cv.put(movieContract.MovieEntry.COLUMN_VOTE, vote);

        long result = db.insert(movieContract.MovieEntry.TABLE_NAME, null, cv);
        Log.i(TAG, "saveMovie result -> " + result);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + movieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    public boolean isDataAlreadyExist(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        /*Cursor cursor = db.query(
                true,
                ForecastContract.ForecastEntry.TABLE_NAME,
                null,
                ForecastContract.ForecastEntry.COLUMN_CITY_NAME + " LIKE ?",
                new String[]{"%" + city + "%"},
                null,
                null,
                null,
                null);*/
        String sql="";
        if (id == 0){
            sql = "SELECT * FROM "
                    + movieContract.MovieEntry.TABLE_NAME
                    + ";";
        }else {
            sql = "SELECT * FROM "
                    + movieContract.MovieEntry.TABLE_NAME
                    + " WHERE "
                    + movieContract.MovieEntry.COLUMN_ID_MOVIE
                    + " LIKE '%" + id + "%';";
        }


        Cursor cursor = db.rawQuery(sql,null);

        int total = cursor.getCount();
        Log.d(TAG,"isDataAlreadyExist total -> "+total);
        cursor.close();
        db.close();
        return total > 0;
    }

    public void deleteForUpdate(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        final String sql = "DELETE FROM "
                + movieContract.MovieEntry.TABLE_NAME
                + " WHERE "
                + movieContract.MovieEntry.COLUMN_ID_MOVIE
                + " LIKE '%" + id + "%';";
        db.execSQL(sql);
        db.close();
    }


}
