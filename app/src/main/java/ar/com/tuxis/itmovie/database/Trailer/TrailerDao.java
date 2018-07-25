package ar.com.tuxis.itmovie.database.Trailer;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ar.com.tuxis.itmovie.database.Trailer.TrailerEntry;

@Dao
public interface TrailerDao {

    @Query("SELECT * FROM trailer")
    LiveData<List<TrailerEntry>> loadAllTrailer();

    @Insert
    void insertTrailer(TrailerEntry trailerEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTrailer(TrailerEntry trailerEntry);

    @Delete
    void deleteTrailer(TrailerEntry taskEntry);

    @Query("SELECT * FROM trailer WHERE id_movie = :id")
    LiveData<TrailerEntry> loadTrailerByMovieId(String id);
}
