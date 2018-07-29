package ar.com.tuxis.itmovie.Movie;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ar.com.tuxis.itmovie.AppExecutors;
import ar.com.tuxis.itmovie.database.Movie.AppDatabase;
import ar.com.tuxis.itmovie.database.Movie.MovieEntry;


public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private AppDatabase mDb;
    private URL url;

    private Context mContext;

    public FetchMovieTask(Context myContext, String apiKey, String... params) throws MalformedURLException {
        mDb = AppDatabase.getInstance(myContext);
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
                .appendQueryParameter(APPID_PARAM, apiKey)
                .build();
        url = new URL(builtUri.toString());
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;
        try {

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
            /*return getMovieDataFromJson(movieJsonStr);*/
            JSONObject moviesJson = new JSONObject(movieJsonStr);
            final String OWM_RESULTS = "results";
            JSONArray movieArray = moviesJson.getJSONArray(OWM_RESULTS);
            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movieJSON = movieArray.getJSONObject(i);
                final MovieEntry movie_temp = new MovieEntry(movieJSON);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mDb.movieDao().ExistById(movie_temp.getId()) == 0){
                            mDb.movieDao().insertMovie(movie_temp);
                        }
                    }
                });
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
