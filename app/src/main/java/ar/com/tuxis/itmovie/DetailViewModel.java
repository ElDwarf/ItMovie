package ar.com.tuxis.itmovie;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import ar.com.tuxis.itmovie.database.Review.ReviewEntry;
import ar.com.tuxis.itmovie.database.Trailer.AppDatabase;
import ar.com.tuxis.itmovie.database.Trailer.TrailerEntry;

public class DetailViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<TrailerEntry>> trailer;
    private LiveData<List<ReviewEntry>> review;

    public DetailViewModel(Application application) {
        super(application);
    }
    public void initicaliceViewModel(int movieId){
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        ar.com.tuxis.itmovie.database.Review.AppDatabase database1;
        database1 = ar.com.tuxis.itmovie.database.Review.AppDatabase.getInstance(this.getApplication());
        trailer = database.trailerDao().loadTrailerByMovieId(movieId);
        review = database1.reviewDao().loadReviewByMovieId(movieId);

    }

    public LiveData<List<TrailerEntry>> getTrailer() {
        return trailer;
    }

    public LiveData<List<ReviewEntry>> getReviews() {
        return review;
    }
}
