package ar.com.tuxis.itmovie;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

import ar.com.tuxis.itmovie.database.Movie.AppDatabase;
import ar.com.tuxis.itmovie.database.Movie.MovieEntry;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<MovieEntry>> movie;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        movie = database.movieDao().loadAllMovie();
    }

    public LiveData<List<MovieEntry>> getMovies() {
        return movie;
    }

    public LiveData<List<MovieEntry>> getMoviesPopular(){
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        movie = database.movieDao().loadAllMovieOrdPopularity();
        return movie;
    }

    public LiveData<List<MovieEntry>> getMoviesRated(){
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        movie = database.movieDao().loadAllMovieOrdRated();
        return movie;
    }

    public LiveData<List<MovieEntry>> getMoviesFavorite(){
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        movie = database.movieDao().loadAllMovieFavorites();
        return movie;
    }
}
