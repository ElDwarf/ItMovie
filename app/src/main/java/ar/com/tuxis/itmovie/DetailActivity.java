package ar.com.tuxis.itmovie;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ar.com.tuxis.itmovie.Movie.Movie;
import ar.com.tuxis.itmovie.Movie.TrailerListAdapter;
import ar.com.tuxis.itmovie.Movie.Trailer;

/**
 * Created by pdalmasso on 4/9/16.
 */
public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
    */
    public class DetailFragment extends Fragment {

        private void openUrl(String url) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

        public TrailerListAdapter mTrailerListAdapter;

        static final String DETAIL_MOVIE = "DETAIL_MOVIE";

        private final String LOG_TAG = DetailFragment.class.getSimpleName();
        private Movie objectsMovie;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        TextView movieTitleTextView;
        TextView movieDetailDescription;
        ImageView imageView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getIntent();

            Movie obj = (Movie) intent.getSerializableExtra("MyClass");
            objectsMovie = obj;
            movieTitleTextView = (TextView) rootView.findViewById(R.id.movieDetailTitle);
            movieTitleTextView.setText(obj.getTitle());
            movieDetailDescription = (TextView) rootView.findViewById(R.id.movieDetailDescription);
            movieDetailDescription.setText(obj.getOverview());
            imageView = (ImageView) rootView.findViewById(R.id.imageView);

            String image_url = "http://image.tmdb.org/t/p/w780" + obj.getBackdrop_path();

            Picasso.with(getContext()).load(image_url).into(imageView);


            mTrailerListAdapter = new TrailerListAdapter(
                    getActivity(), // The current context (this activity)
                    new ArrayList<Trailer>()
            );
            ListView trailerListView = (ListView) rootView.findViewById(R.id.trailer_container);
            trailerListView.setAdapter(mTrailerListAdapter);

            trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                    Trailer trailer = mTrailerListAdapter.getItem(position);
                    onTrailerOpen(trailer);
                }
            });

            return rootView;
        }

        public void onTrailerOpen(Trailer movieTrailer) {
            openUrl(movieTrailer.getTrailerUrl());
        }

        public void getTrailer(){
            MovieDetailTask movieDetailTaskTask = new MovieDetailTask();
            movieDetailTaskTask.execute();
        }

        @Override
        public void onStart(){
            super.onStart();
            getTrailer();
        }

        public class MovieDetailTask extends AsyncTask<String, Void, Trailer[]> {

            private final String LOG_TAG = DetailFragment.MovieDetailTask.class.getSimpleName();

            private Trailer[] getTrailerDataFromJson(String JsonStr)
                    throws JSONException {
                // These are the names of the JSON objects that need to be extracted.
                final String OWM_RESULTS = "results";
                JSONObject trailerJson = new JSONObject(JsonStr);
                JSONArray TrailerArray = trailerJson.getJSONArray(OWM_RESULTS);
                Trailer[] result = new Trailer[TrailerArray.length()];
                for(int i = 0; i < TrailerArray.length(); i++) {
                    JSONObject movieJSON = TrailerArray.getJSONObject(i);
                    Trailer movie_temp = new Trailer(movieJSON);
                    result[i] = movie_temp;
                }
                return result;
            }

            @Override
            protected Trailer[] doInBackground(String... params) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                String movieVideosJsonStr = null;
                try {
                    String BASE_URL = "http://api.themoviedb.org/3/movie/";
                    String URL_VIDEOS = BASE_URL + String.valueOf(objectsMovie.getId()) + "/videos";
                    final String APPID_PARAM = "api_key";
                    Uri builtUri = Uri.parse(URL_VIDEOS).buildUpon()
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
                    return getTrailerDataFromJson(movieVideosJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Trailer[] strings) {
                if (strings != null){
                    try{
                        mTrailerListAdapter.clear();
                    }catch(Exception e){

                    }

                    for (Trailer TrailerStr : strings){
                        mTrailerListAdapter.add(TrailerStr);
                    }
                }
            }
        }
    }
}
