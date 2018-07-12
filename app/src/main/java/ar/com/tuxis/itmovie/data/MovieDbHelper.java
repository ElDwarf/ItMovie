package ar.com.tuxis.itmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ar.com.tuxis.itmovie.data.MovieContract.*;

/**
 * Created by pdalmasso on 15/11/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "movie.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;

    // Constructor
    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold Movie data
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_TITLE + " TEXT, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_RELEASE_DATA + " TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MovieEntry.COLUMN_POPULARITY + " TEXT, " +
                MovieEntry.COLUMN_VOTE_COUNT + " TEXT, " +
                MovieEntry.COLUMN_VIDEO + " TEXT, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " INTEGER, " +
                MovieEntry.COLUMN_FAVORITE + " BOOLEAN DEFAULT 0" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}