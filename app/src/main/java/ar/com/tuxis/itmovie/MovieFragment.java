package ar.com.tuxis.itmovie;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.net.MalformedURLException;
import java.util.List;

import ar.com.tuxis.itmovie.Movie.FetchMovieTask;
import ar.com.tuxis.itmovie.Movie.MovieGridAdapter;
import ar.com.tuxis.itmovie.database.Movie.AppDatabase;
import ar.com.tuxis.itmovie.database.Movie.MovieEntry;

/**
 * Created by pdalmasso on 15/8/16.
 */
public class MovieFragment extends Fragment {

    /*private SQLiteDatabase mDb;*/

    private AppDatabase mDb;

    public MovieGridAdapter mMovieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        SharedPreferences settings = getContext().getSharedPreferences("MyPrefs", 0);
        String order_parameter = settings.getString("order_parameter", "popular");
        setupViewModel(order_parameter);
        setHasOptionsMenu(true);
        try {
            getApiMovieMovie("popular");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void getApiMovieMovie(String parameter) throws MalformedURLException {
        FetchMovieTask movieTask = new FetchMovieTask(
            getContext(),
            getResources().getString(R.string.MyMovieApiKey),
            parameter
        );
        movieTask.execute(parameter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    public interface Callback {
        void onItemSelected(MovieEntry movie);
    }

    private void setupViewModel(String parameter){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if (parameter == "popular"){
            viewModel.getMoviesPopular().observe(this, new Observer<List<MovieEntry>>() {
                @Override
                public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                    mMovieAdapter.setData(movieEntries);
                }
            });
        }else if (parameter == "rate"){
            viewModel.getMoviesRated().observe(this, new Observer<List<MovieEntry>>() {
                @Override
                public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                    mMovieAdapter.setData(movieEntries);
                }
            });
        }else if (parameter == "favorites"){
            viewModel.getMoviesFavorite().observe(this, new Observer<List<MovieEntry>>() {
                @Override
                public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                    mMovieAdapter.setData(movieEntries);
                }
            });
        }else{
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                mMovieAdapter.setData(movieEntries);
            }
        });
    }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDb = AppDatabase.getInstance(getActivity());

        mMovieAdapter = new MovieGridAdapter(getActivity());


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.Gridview_movie);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                MovieEntry movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("MyClass", movie.getId());
                startActivity(intent);
            }
        });
        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences settings = getContext().getSharedPreferences("MyPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        int id = item.getItemId();
        String order_parameter = "popular";
        if (id == R.id.order_most_popular) {
            order_parameter = "popular";
        }else if (id == R.id.order_highest_rated) {
            order_parameter = "rate";
        }else if (id == R.id.filter_Fav_only){
            order_parameter = "favorites";
        }else if (id == R.id.view_all){
            order_parameter = "popular";
        }
        setupViewModel(order_parameter);
        editor.putString("order_parameter", order_parameter);
        editor.commit();
        return super.onOptionsItemSelected(item);
    }
}
