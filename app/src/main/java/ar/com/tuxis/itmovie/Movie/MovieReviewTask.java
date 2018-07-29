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
import ar.com.tuxis.itmovie.database.Review.AppDatabase;
import ar.com.tuxis.itmovie.database.Review.ReviewEntry;

public class MovieReviewTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = MovieReviewTask.class.getSimpleName();

    private AppDatabase mDb;
    private URL url;

    private Context mContext;

    private int mMovieId;

    public MovieReviewTask(Context myContext, String apiKey,int movieId, String... params) throws MalformedURLException {
        mDb = AppDatabase.getInstance(myContext);
        mMovieId = movieId;
        String BASE_URL = "http://api.themoviedb.org/3/movie/";
        String URL_VIDEOS = BASE_URL + String.valueOf(mMovieId) + "/reviews";
        final String APPID_PARAM = "api_key";
        Uri builtUri = Uri.parse(URL_VIDEOS).buildUpon()
                .appendQueryParameter(APPID_PARAM, apiKey)
                .build();
        url = new URL(builtUri.toString());
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieVideosJsonStr = null;
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
            movieVideosJsonStr = buffer.toString();

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
            JSONObject trailerJson = new JSONObject(movieVideosJsonStr);
            final String OWM_RESULTS = "results";
            JSONArray ReviewArray = trailerJson.getJSONArray(OWM_RESULTS);
            for(int i = 0; i < ReviewArray.length(); i++) {
                JSONObject movieJSON = ReviewArray.getJSONObject(i);
                final ReviewEntry movie_temp = new ReviewEntry(movieJSON);
                movie_temp.setId_movie(mMovieId);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mDb.reviewDao().ExistById(movie_temp.getId()) == 0){
                            mDb.reviewDao().insertReview(movie_temp);
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
