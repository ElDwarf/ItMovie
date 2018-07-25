package ar.com.tuxis.itmovie.database.Movie;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DataConvert {
    @TypeConverter
    public static Boolean toBoolean(int value) {
        if (value == 1){
            return true;
        }else{
            return false;
        }
    }

    @TypeConverter
    public static int toInt(Boolean value){
        if(value){
            return 1;
        }else{
            return 0;
        }
    }


}
