package ar.com.tuxis.itmovie.Movie;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by pdalmasso on 14/11/17.
 */
public class Trailer implements Serializable {

    private static final String VIDEO_TRAILER_URL = "https://www.youtube.com/watch?v=%s";
    private String id;
    private String key;
    private String name;
    private String site;

    public Trailer() {
    }

    public Trailer(JSONObject trailer_item) throws JSONException{
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

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTrailerUrl() {
        return String.format(VIDEO_TRAILER_URL, this.key);
    }
}