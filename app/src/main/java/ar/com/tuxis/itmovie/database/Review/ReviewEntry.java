package ar.com.tuxis.itmovie.database.Review;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "review")
public class ReviewEntry {

    @PrimaryKey()
    private String id;
    private int id_movie;
    private String author;
    private String content;
    private String url;

    public ReviewEntry(String id, int id_movie, String author, String content, String url) {
        this.id = id;
        this.id_movie = id_movie;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public ReviewEntry(JSONObject reviews_item) throws JSONException {
        String OWM_ID = "id";
        String OWM_AUTHOR = "author";
        String OWM_CONTENT = "content";
        String OWM_URL = "url";

        this.id = reviews_item.getString(OWM_ID);
        this.author = reviews_item.getString(OWM_AUTHOR);
        this.content = reviews_item.getString(OWM_CONTENT);
        this.url = reviews_item.getString(OWM_URL);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getId_movie() {
        return id_movie;
    }

    public void setId_movie(int id_movie) {
        this.id_movie = id_movie;
    }

    public String toString() {
        return author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
