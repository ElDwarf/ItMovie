package ar.com.tuxis.itmovie.database.Trailer;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "trailer")
public class TrailerEntry {

    @PrimaryKey()
    private String id;
    private int id_movie;
    private String key;
    private String name;
    private String site;

    private static final String VIDEO_TRAILER_URL = "https://www.youtube.com/watch?v=%s";

    public TrailerEntry(String id, int id_movie, String key, String name, String site) {
        this.id = id;
        this.id_movie = id_movie;
        this.key = key;
        this.name = name;
        this.site = site;
    }

    @Ignore
    public TrailerEntry(JSONObject trailer_item) throws JSONException {
        String OWM_ID = "id";
        String OWM_KEY = "key";
        String OWM_NAME = "name";
        String OWM_SITE = "site";

        this.id = trailer_item.getString(OWM_ID);
        this.key = trailer_item.getString(OWM_KEY);
        this.name = trailer_item.getString(OWM_NAME);
        this.site = trailer_item.getString(OWM_SITE);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getId_movie() {
        return id_movie;
    }

    public void setId_movie(int id_movie) {
        this.id_movie = id_movie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
