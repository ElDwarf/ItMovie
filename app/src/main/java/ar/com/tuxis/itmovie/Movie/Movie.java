package ar.com.tuxis.itmovie.Movie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ar.com.tuxis.itmovie.data.MovieContract;
import ar.com.tuxis.itmovie.data.MovieDbHelper;

/**
 * Created by pdalmasso on 31/8/16.
 */
public class Movie implements Serializable {

    private int id;
    private String title;
    private String poster_path;
    private Boolean adult;
    private String overview;
    private String release_date;
    private String original_title;
    private String backdrop_path;
    private String popularity;
    private String vote_count;
    private String video;
    private int vote_average;
    private Boolean favorite;

    public Movie() {
    }

    public Movie(JSONObject movie_item) throws JSONException{
        String OWM_POSTER_PATH = "poster_path";
        String OWM_ADULT = "adult";
        String OWM_OVERVIEW = "overview";
        String OWM_RELEASE_DATE = "release_date";
        String OWM_ID = "id";
        String OWM_ORIGINAL_TITLE = "original_title";
        String OWM_TITLE = "title";
        String OWM_BACKDROP_PATH = "backdrop_path";
        String OWM_POPULARITY = "popularity";
        String OWM_VOTE_COUNT = "vote_count";
        String OWM_VIDEO = "video";
        String OWM_VOTE_AVERAGE = "vote_average";

        this.id = movie_item.getInt(OWM_ID);
        this.title = movie_item.getString(OWM_TITLE);
        this.poster_path = movie_item.getString(OWM_POSTER_PATH);
        this.adult = movie_item.getBoolean(OWM_ADULT);
        this.overview = movie_item.getString(OWM_OVERVIEW);
        this.release_date = movie_item.getString(OWM_RELEASE_DATE);
        this.original_title = movie_item.getString(OWM_ORIGINAL_TITLE);
        this.backdrop_path = movie_item.getString(OWM_BACKDROP_PATH);
        this.popularity = movie_item.getString(OWM_POPULARITY);
        this.vote_count = movie_item.getString(OWM_VOTE_COUNT);
        this.video = movie_item.getString(OWM_VIDEO);
        this.vote_average = movie_item.getInt(OWM_VOTE_AVERAGE);
        this.favorite = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getVote_average() {
        return vote_average;
    }

    public void setVote_average(int vote_average) {
        this.vote_average = vote_average;
    }

    public void setFavorite() {
        this.favorite = true;
    }

    public void unSetFavorite() {
        this.favorite = false;
    }

    public void toggleFavorite() {
        if( this.is_favirte()){
            this.favorite = false;
        }else{
            this.favorite = true;
        }
    }

    public Boolean is_favirte(){ return this.favorite; }

    protected Movie(Parcel in) {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

    public long save(Context context) {
        SQLiteDatabase mDb;
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        mDb = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_ID, this.id);
        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, this.title);
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, this.poster_path);
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, this.overview);
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATA, this.release_date);
        cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, this.original_title);
        cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, this.backdrop_path);
        cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, this.popularity);
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, this.vote_count);
        cv.put(MovieContract.MovieEntry.COLUMN_VIDEO, this.video);
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, this.vote_average);
        cv.put(MovieContract.MovieEntry.COLUMN_FAVORITE, this.favorite);
        try{
            long id_movie;
            id_movie = mDb.update(
                    MovieContract.MovieEntry.TABLE_NAME,
                    cv,
                    "id=" + this.id,
                    null
            );
            if (id_movie == 0){
                id_movie = mDb.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
            }
            mDb.close();
            return id_movie;
        }catch (Exception e){
            return 0;
        }

    }

    public void loadDataFormId(Context context, int id_movie){
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase mDb;
        mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                "id = " + Integer.toString(id_movie),
                null,
                null,
                null,
                null
        );
        while(cursor.moveToNext()) {
            this.id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
            this.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            this.poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            this.overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            this.release_date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATA));
            this.original_title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
            this.backdrop_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH));
            this.popularity = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY));
            this.vote_count = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT));
            this.video = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VIDEO));
            this.vote_average = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
            this.favorite = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)) > 0;
        }
    }

    public void loadDataFormCursor(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID));
        this.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        this.poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        this.overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
        this.release_date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATA));
        this.original_title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
        this.backdrop_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH));
        this.popularity = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY));
        this.vote_count = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT));
        this.video = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VIDEO));
        this.vote_average = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
        this.favorite = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)) > 0;
    }
}