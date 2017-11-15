package ar.com.tuxis.itmovie.Movie;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by pdalmasso on 14/11/17.
 */
public class Review implements Serializable {

    private String id;
    private String author;
    private String content;
    private String url;

    public Review() {
    }

    public Review(JSONObject reviews_item) throws JSONException{
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