package ar.com.tuxis.itmovie;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
import ar.com.tuxis.itmovie.Movie.Review;
import ar.com.tuxis.itmovie.Movie.ReviewListAdapter;
import ar.com.tuxis.itmovie.Movie.Trailer;
import ar.com.tuxis.itmovie.Movie.TrailerListAdapter;

public class DetailFragment extends android.support.v4.app.Fragment {

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public TrailerListAdapter mTrailerListAdapter;
    public ReviewListAdapter mReviewListAdapter;
    public ListView trailerListView;
    public ListView reviewListView;

    static final String DETAIL_MOVIE = "DETAIL_MOVIE";

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private Movie objectsMovie;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    TextView movieTitleTextView;
    TextView movieDetailDescription;
    ImageView imageView;
    ImageButton imageFavorite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        final Movie obj = (Movie) intent.getSerializableExtra("MyClass");
        objectsMovie = obj;
        movieTitleTextView = (TextView) rootView.findViewById(R.id.movieDetailTitle);
        movieTitleTextView.setText(obj.getTitle());
        movieDetailDescription = (TextView) rootView.findViewById(R.id.movieDetailDescription);
        movieDetailDescription.setText(obj.getOverview());
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imageFavorite = (ImageButton) rootView.findViewById(R.id.btnFavorite);
        if (obj.is_favirte()){
            imageFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        }else{
            imageFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
        imageFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                obj.toggleFavorite();
                obj.save(rootView.getContext());
                if (obj.is_favirte()){
                    imageFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                }else{
                    imageFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                }
            }
        });


        String image_url = "http://image.tmdb.org/t/p/w780" + obj.getBackdrop_path();

        Picasso.with(getContext()).load(image_url).into(imageView);


        mTrailerListAdapter = new TrailerListAdapter(
                getActivity(), // The current context (this activity)
                new ArrayList<Trailer>()
        );
        trailerListView = (ListView) rootView.findViewById(R.id.trailer_container);
        trailerListView.setAdapter(mTrailerListAdapter);

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Trailer trailer = mTrailerListAdapter.getItem(position);
                onTrailerOpen(trailer);
            }
        });

        mReviewListAdapter = new ReviewListAdapter(
                getActivity(), // The current context (this activity)
                new ArrayList<Review>()
        );
        reviewListView = (ListView) rootView.findViewById(R.id.reviews_container);
        reviewListView.setAdapter(mReviewListAdapter);
        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Review trailer = mReviewListAdapter.getItem(position);
            }
        });
        return rootView;
    }

    public void onTrailerOpen(Trailer movieTrailer) {
        Log.e("<PRUEBAS>: ", "Prueba 002");
        openUrl(movieTrailer.getTrailerUrl());
    }

    public void getTrailer(){
        MovieTrailerTask movieDetailTaskTask = new MovieTrailerTask();
        movieDetailTaskTask.execute();
    }

    public void getReview(){
        MovieReviewTask movieReviewTask = new MovieReviewTask();
        movieReviewTask.execute();
    }

    @Override
    public void onStart(){
        Log.e("<PRUEBAS>: ", "Prueba 003");
        super.onStart();
        getTrailer();
        getReview();
    }

    public class MovieTrailerTask extends AsyncTask<String, Void, Trailer[]> {

        private final String LOG_TAG = DetailFragment.MovieTrailerTask.class.getSimpleName();

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
                    int list_cell_size=200;
                    ViewGroup.LayoutParams list;
                    list = (ViewGroup.LayoutParams) trailerListView.getLayoutParams();
                    list.height = list_cell_size * mTrailerListAdapter.getCount();
                    trailerListView.setLayoutParams(list);
                }
            }
        }
    }

    public class MovieReviewTask extends AsyncTask<String, Void, Review[]> {

        private final String LOG_TAG = DetailFragment.MovieReviewTask.class.getSimpleName();

        private Review[] getReviewDataFromJson(String JsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            JSONObject trailerJson = new JSONObject(JsonStr);
            JSONArray ReviewArray = trailerJson.getJSONArray(OWM_RESULTS);
            Review[] result = new Review[ReviewArray.length()];
            for(int i = 0; i < ReviewArray.length(); i++) {
                JSONObject movieJSON = ReviewArray.getJSONObject(i);
                Review movie_temp = new Review(movieJSON);
                if (movie_temp.getAuthor() != null && movie_temp.getContent() != null){
                    result[i] = movie_temp;
                }
            }
            return result;
        }

        @Override
        protected Review[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieVideosJsonStr = null;
            try {
                String BASE_URL = "http://api.themoviedb.org/3/movie/";
                String URL_VIDEOS = BASE_URL + String.valueOf(objectsMovie.getId()) + "/reviews";
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
                return getReviewDataFromJson(movieVideosJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Review[] strings) {
            if (strings != null){
                try{
                    mReviewListAdapter.clear();
                }catch(Exception e){

                }

                for (Review ReviewStr : strings){
                    int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    mReviewListAdapter.add(ReviewStr);

                    int grossElementHeight = 0;
                    for(int x = 0; x < mReviewListAdapter.getCount(); x++){
                        View childView = mReviewListAdapter.getView(x, null, reviewListView);
                        childView.measure(UNBOUNDED, UNBOUNDED);
                        grossElementHeight += childView.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams list;
                    list = (ViewGroup.LayoutParams) reviewListView.getLayoutParams();
                    list.height = grossElementHeight;
                    reviewListView.setLayoutParams(list);
                }
            }
        }
    }
}
