package ar.com.tuxis.itmovie;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ar.com.tuxis.itmovie.database.Movie.AppDatabase;
import ar.com.tuxis.itmovie.database.Movie.MovieEntry;

public class DetailMovieViewModel extends ViewModel {

    private LiveData<MovieEntry> movie;

    public DetailMovieViewModel(AppDatabase database, int taskId) {
        movie = database.movieDao().loadMovieById(taskId);
    }

    public LiveData<MovieEntry> getMovie() {
        return movie;
    }
}