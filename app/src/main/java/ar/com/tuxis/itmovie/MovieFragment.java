package ar.com.tuxis.itmovie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ar.com.tuxis.itmovie.Movie.Movie;
import ar.com.tuxis.itmovie.Movie.MovieGridAdapter;
import ar.com.tuxis.itmovie.data.MovieContract;
import ar.com.tuxis.itmovie.data.MovieDbHelper;

import static java.security.AccessController.getContext;

/**
 * Created by pdalmasso on 15/8/16.
 */
public class MovieFragment extends Fragment {

    private SQLiteDatabase mDb;

    public MovieGridAdapter mMovieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }


    public void updateMovie(String parameter){
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute(parameter);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovie("popular");
    }

    public interface Callback {
        void onItemSelected(Movie movie);
    }

    private Cursor getAllMovie() {
        // COMPLETED (6) Inside, call query on mDb passing in the table name and projection String [] order by COLUMN_TIMESTAMP
        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
        mDb = dbHelper.getReadableDatabase();
        Cursor response = mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return response;
    }

    private Cursor getAnlyFavMovie() {
        // COMPLETED (6) Inside, call query on mDb passing in the table name and projection String [] order by COLUMN_TIMESTAMP
        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
        mDb = dbHelper.getReadableDatabase();
        Cursor response = mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                "favorite > 0",
                null,
                null,
                null,
                null
        );
        return response;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Cursor mCursor = getAllMovie();
        ArrayList<Movie> mArrayList = new ArrayList<Movie>();
        while(mCursor.moveToNext()) {
            Movie movie_temp = new Movie();
            movie_temp.loadDataFormCursor(mCursor);
            mArrayList.add(movie_temp); //add the item
        }
        mMovieAdapter = new MovieGridAdapter(
                getActivity(), // The current context (this activity)
                mArrayList
        );



        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.Gridview_movie);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Movie movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("MyClass", movie);
                startActivity(intent);
            }
        });
        mDb.close();
        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.order_most_popular) {
            updateMovie("popular");
            return true;
        }else if (id == R.id.order_highest_rated) {
            updateMovie("rate");
            return true;
        }else if (id == R.id.filter_Fav_only){
            Cursor mCursor = getAnlyFavMovie();
            ArrayList<Movie> mArrayList = new ArrayList<Movie>();
            while(mCursor.moveToNext()) {
                Movie movie_temp = new Movie();
                movie_temp.loadDataFormCursor(mCursor);
                mArrayList.add(movie_temp); //add the item
            }
            mMovieAdapter.clear();
            for (int x = 0; x < mArrayList.size(); x++){
                mMovieAdapter.add(mArrayList.get(x));
            }
        }else if (id == R.id.view_all){
            Cursor mCursor = getAllMovie();
            ArrayList<Movie> mArrayList = new ArrayList<Movie>();
            while(mCursor.moveToNext()) {
                Movie movie_temp = new Movie();
                movie_temp.loadDataFormCursor(mCursor);
                mArrayList.add(movie_temp); //add the item
            }
            mMovieAdapter.clear();
            for (int x = 0; x < mArrayList.size(); x++){
                mMovieAdapter.add(mArrayList.get(x));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private Movie[] getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(OWM_RESULTS);
            Movie[] result = new Movie[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movieJSON = movieArray.getJSONObject(i);
                Movie movie_temp = new Movie(movieJSON);
                result[i] = movie_temp;
            }
            return result;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            try {
                String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/";
                if (params.length > 0) {
                    if (params[0] == "popular") {
                        FORECAST_BASE_URL = FORECAST_BASE_URL + "popular?";
                    }else if (params[0] == "rate") {
                        FORECAST_BASE_URL += "top_rated?";
                    }
                }else{
                    FORECAST_BASE_URL = FORECAST_BASE_URL + "popular?";
                }
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, getResources().getString(R.string.MyMovieApiKey))
                        .build();
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] strings) {
            if (strings != null){
                try{
                    mMovieAdapter.clear();
                }catch(Exception e){

                }

                for (Movie dayForecastStr : strings){
                    dayForecastStr.loadDataFormId(getContext(), dayForecastStr.getId());
                    mMovieAdapter.add(dayForecastStr);
                }
            }
        }
    }
}
