package ar.com.tuxis.itmovie.database.Movie;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY popularity desc")
    LiveData<List<MovieEntry>> loadAllMovie();

    @Query("SELECT * FROM movie ORDER BY popularity desc")
    LiveData<List<MovieEntry>> loadAllMovieOrdPopularity();

    @Query("SELECT * FROM movie ORDER BY vote_count desc")
    LiveData<List<MovieEntry>> loadAllMovieOrdRated();

    @Query("SELECT * FROM movie WHERE favorite = 1")
    LiveData<List<MovieEntry>> loadAllMovieFavorites();

    @Insert
    void insertMovie(MovieEntry movieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieEntry movieEntry);

    @Delete
    void deleteTask(MovieEntry taskEntry);

    @Query("SELECT * FROM movie WHERE id = :id")
    LiveData<MovieEntry> loadMovieById(int id);

    @Query("SELECT count(*) FROM movie WHERE id = :id")
    int ExistById(int id);
}