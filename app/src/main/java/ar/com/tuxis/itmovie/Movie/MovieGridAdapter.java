package ar.com.tuxis.itmovie.Movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ar.com.tuxis.itmovie.R;
import com.squareup.picasso.Picasso;

/**
 * Created by pdalmasso on 4/9/16.
 */
public class MovieGridAdapter extends BaseAdapter {

    private Movie movie = new Movie();
    private List<Movie> movieObjects;
    private Context context;
    private final LayoutInflater layoutInflater;

    public MovieGridAdapter(Context p_context, List<Movie> p_objects) {
        layoutInflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = p_context;
        this.movieObjects = p_objects;
    }

    public void add(Movie object) {
        try {
            object.save(context);
        } catch (Exception e) {
        }

        synchronized (this.movie) {
            this.movieObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (this.movie) {
            this.movieObjects.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<Movie> data) {
        clear();
        for (Movie movie : data) {
            add(movie);
        }
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public int getCount() {
        return movieObjects.size();
    }

    @Override
    public Movie getItem(int position) {
        return movieObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.grid_item_movie, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Movie movie = getItem(position);

        String image_url = "http://image.tmdb.org/t/p/w342" + movie.getPoster_path();

        viewHolder = (ViewHolder) view.getTag();
        Picasso.with(getContext()).load(image_url).into(viewHolder.imageView);
        viewHolder.titleView.setText(movie.getTitle());

        return view;
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView titleView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.grid_item_movie_image);
            titleView = (TextView) view.findViewById(R.id.grid_item_movie_textview);
        }
    }
}
