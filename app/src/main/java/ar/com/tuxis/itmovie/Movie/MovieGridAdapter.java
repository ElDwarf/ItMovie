package ar.com.tuxis.itmovie.Movie;

import android.content.Context;
import android.system.ErrnoException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ar.com.tuxis.itmovie.R;
import ar.com.tuxis.itmovie.database.Movie.MovieEntry;

/**
 * Created by pdalmasso on 4/9/16.
 */
public class MovieGridAdapter extends BaseAdapter {

    private MovieEntry movie = new MovieEntry();
    private List<MovieEntry> mMovieEntries;
    private Context context;
    private final LayoutInflater layoutInflater;

    public MovieGridAdapter(Context p_context) {
        layoutInflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = p_context;
    }

    public MovieGridAdapter(Context p_context, List<MovieEntry> p_objects) {
        layoutInflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = p_context;
        this.mMovieEntries = p_objects;
    }

    public void clear() {
        synchronized (this.movie) {
            this.mMovieEntries.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<MovieEntry> data) {
        mMovieEntries = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mMovieEntries != null){
            return mMovieEntries.size();
        }else{
            return 0;
        }
    }

    @Override
    public MovieEntry getItem(int position) {
        return mMovieEntries.get(position);
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

        final MovieEntry movie = getItem(position);

        String image_url = "http://image.tmdb.org/t/p/w342" + movie.getPoster_path();

        viewHolder = (ViewHolder) view.getTag();
        Picasso.with(context).load(image_url).into(viewHolder.imageView);
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
