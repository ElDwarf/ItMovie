package ar.com.tuxis.itmovie;

import android.app.Fragment;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ar.com.tuxis.itmovie.Movie.MovieReviewTask;
import ar.com.tuxis.itmovie.Movie.MovieTrailerTask;
import ar.com.tuxis.itmovie.Movie.ReviewListAdapter;
import ar.com.tuxis.itmovie.Movie.TrailerListAdapter;
import ar.com.tuxis.itmovie.database.Movie.AppDatabase;
import ar.com.tuxis.itmovie.database.Movie.MovieEntry;
import ar.com.tuxis.itmovie.database.Review.ReviewEntry;
import ar.com.tuxis.itmovie.database.Trailer.TrailerEntry;

public class DetailFragment extends android.support.v4.app.Fragment {

    private AppDatabase mDbMovie;
    private ar.com.tuxis.itmovie.database.Review.AppDatabase mDbReviews;
    private ar.com.tuxis.itmovie.database.Trailer.AppDatabase mDbTrailer;
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

    private MovieEntry mMovieEntry;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    private TextView movieTitleTextView;
    private TextView movieDetailDescription;
    private ImageView imageView;
    private ImageButton imageFavorite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDbMovie = AppDatabase.getInstance(getContext());
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        int mMovieId = intent.getIntExtra("MyClass", 0);
        try {
            getTrailer(mMovieId);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            getReview(mMovieId);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mDbTrailer = ar.com.tuxis.itmovie.database.Trailer.AppDatabase.getInstance(getContext());
        mDbReviews = ar.com.tuxis.itmovie.database.Review.AppDatabase.getInstance(getContext());
        final LiveData<MovieEntry> obj = mDbMovie.movieDao().loadMovieById(mMovieId);
        DetailMovieViewModelFactory factory = new DetailMovieViewModelFactory(mDbMovie, mMovieId);
        final DetailMovieViewModel viewModel
                = ViewModelProviders.of(this, factory).get(DetailMovieViewModel.class);

        viewModel.getMovie().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                viewModel.getMovie().removeObserver(this);
                mMovieEntry = movieEntry;
                populateUI(movieEntry);
            }
        });
        setupViewModel(mMovieId);

        movieTitleTextView = (TextView) rootView.findViewById(R.id.movieDetailTitle);
        movieDetailDescription = (TextView) rootView.findViewById(R.id.movieDetailDescription);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imageFavorite = (ImageButton) rootView.findViewById(R.id.btnFavorite);

        imageFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (mMovieEntry.getFavorite()) {
                mMovieEntry.setFavorite(false);
                imageFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            } else {
                mMovieEntry.setFavorite(true);
                imageFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            }
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDbMovie.movieDao().updateMovie(mMovieEntry);
                }
            });
            }
        });

        mTrailerListAdapter = new TrailerListAdapter(getActivity());

        trailerListView = (ListView) rootView.findViewById(R.id.trailer_container);
        trailerListView.setAdapter(mTrailerListAdapter);
        mDbTrailer = ar.com.tuxis.itmovie.database.Trailer.AppDatabase.getInstance(getContext());

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                TrailerEntry trailer = mTrailerListAdapter.getItem(position);
                onTrailerOpen(trailer);
            }
        });
        mReviewListAdapter = new ReviewListAdapter(
                getActivity(), // The current context (this activity)
                new ArrayList<ReviewEntry>()
        );
        reviewListView = (ListView) rootView.findViewById(R.id.reviews_container);
        reviewListView.setAdapter(mReviewListAdapter);
        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                ReviewEntry trailer = mReviewListAdapter.getItem(position);
            }
        });

        return rootView;
    }


    private void populateUI(MovieEntry movie) {
        if (movie == null) {
            return;
        }
        movieTitleTextView.setText(movie.getTitle());
        movieDetailDescription.setText(movie.getOverview());
        if (movie.getFavorite()) {
            imageFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            imageFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }

        String image_url = "http://image.tmdb.org/t/p/w780" + movie.getBackdrop_path();

        Picasso.with(getContext()).load(image_url).into(imageView);
    }


    public void getTrailer(int movieId) throws MalformedURLException {
        MovieTrailerTask movieDetailTaskTask = new MovieTrailerTask(
            getContext(),
            getResources().getString(R.string.MyMovieApiKey),
            movieId,
            ""
        );
        movieDetailTaskTask.execute();
    }

    public void getReview(int movieId) throws MalformedURLException {
        MovieReviewTask movieReviewTask = new MovieReviewTask(
            getContext(),
            getResources().getString(R.string.MyMovieApiKey),
            movieId,
            ""
        );
        movieReviewTask.execute();
    }

    private void setupViewModel(int mMovieId) {
        DetailViewModel viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        viewModel.initicaliceViewModel(mMovieId);
        viewModel.getTrailer().observe(this, new Observer<List<TrailerEntry>>() {
            @Override
            public void onChanged(@Nullable List<TrailerEntry> trailerEntry) {
                mTrailerListAdapter.setData(trailerEntry);
            }
        });
        viewModel.getReviews().observe(this, new Observer<List<ReviewEntry>>() {
            @Override
            public void onChanged(@Nullable List<ReviewEntry> reviewEntries) {
                mReviewListAdapter.setData(reviewEntries);
            }
        });
    }

    public void onTrailerOpen(TrailerEntry movieTrailer) {
        openUrl(movieTrailer.getTrailerUrl());
    }


    /*




    }






    */
}
