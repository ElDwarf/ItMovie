package ar.com.tuxis.itmovie.database.Trailer;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import ar.com.tuxis.itmovie.database.Review.ReviewDao;
import ar.com.tuxis.itmovie.database.Review.ReviewEntry;

@Database(entities = {TrailerEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = ar.com.tuxis.itmovie.database.Trailer.AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "trailerlist";
    private static ar.com.tuxis.itmovie.database.Trailer.AppDatabase sInstance;

    public static ar.com.tuxis.itmovie.database.Trailer.AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        ar.com.tuxis.itmovie.database.Trailer.AppDatabase.class, ar.com.tuxis.itmovie.database.Trailer.AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract TrailerDao trailerDao();
}
