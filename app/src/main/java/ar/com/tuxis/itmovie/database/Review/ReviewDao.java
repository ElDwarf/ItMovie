package ar.com.tuxis.itmovie.database.Review;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ReviewDao {

    @Query("SELECT * FROM review")
    LiveData<List<ReviewEntry>> loadAllReview();

    @Insert
    void insertReview(ReviewEntry reviewEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateReview(ReviewEntry movieEntry);

    @Delete
    void deleteReview(ReviewEntry taskEntry);

    @Query("SELECT * FROM review WHERE id_movie = :id")
    LiveData<ReviewEntry> loadReviewByMovieId(String id);
}
