package com.example.me.whatsmovie.database;

import android.provider.BaseColumns;

/**
 * Created by Me on 5/23/2017.
 */

public class movieContract {

    public static final class MovieEntry implements BaseColumns {
        //table name
        public static final String TABLE_NAME = "favorite";

        //city detail
        public static final String COLUMN_ID_MOVIE = "ID_MOVIE";

        //forecast detail
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_VOTE = "vote";
    }

}
