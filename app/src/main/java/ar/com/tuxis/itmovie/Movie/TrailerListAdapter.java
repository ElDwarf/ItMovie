package ar.com.tuxis.itmovie.Movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ar.com.tuxis.itmovie.R;

/**
 * Created by pdalmasso on 4/9/16.
 */
public class TrailerListAdapter extends BaseAdapter {

    private Trailer trailer = new Trailer();
    private List<Trailer> trailerObjects;
    private Context context;
    private final LayoutInflater layoutInflater;

    public TrailerListAdapter(Context p_context, List<Trailer> p_objects) {
        layoutInflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = p_context;
        this.trailerObjects = p_objects;
    }

    public void add(Trailer object) {
        synchronized (this.trailer) {
            this.trailerObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (this.trailer) {
            this.trailerObjects.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<Trailer> data) {
        clear();
        for (Trailer trailer : data) {
            add(trailer);
        }
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public int getCount() {
        return trailerObjects.size();
    }

    @Override
    public Trailer getItem(int position) {
        return trailerObjects.get(position);
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
            view = this.layoutInflater.inflate(R.layout.movie_detail_trailer, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Trailer trailer = getItem(position);

        String title = trailer.getName();

        viewHolder = (ViewHolder) view.getTag();
        viewHolder.titleView.setText(trailer.getName());

        return view;
    }

    public static class ViewHolder {
        public final TextView titleView;

        public ViewHolder(View view) {
            titleView = (TextView) view.findViewById(R.id.movie_detail_trailer_textview);
        }
    }
}
