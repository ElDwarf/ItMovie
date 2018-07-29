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
import ar.com.tuxis.itmovie.database.Trailer.TrailerEntry;

/**
 * Created by pdalmasso on 14/11/17.
 */
public class TrailerListAdapter extends BaseAdapter {

    private TrailerEntry trailer = new TrailerEntry();
    private List<TrailerEntry> mTrailerEntries;
    private Context context;
    private final LayoutInflater layoutInflater;


    public TrailerListAdapter(Context p_context) {
        layoutInflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = p_context;
    }

    public TrailerListAdapter(Context p_context, List<TrailerEntry> p_objects) {
        layoutInflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = p_context;
        this.mTrailerEntries = p_objects;
    }

    public void clear() {
        synchronized (this.trailer) {
            this.mTrailerEntries.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<TrailerEntry> data) {
        mTrailerEntries = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mTrailerEntries != null){
            return mTrailerEntries.size();
        }else{
            return 0;
        }
    }

    @Override
    public TrailerEntry getItem(int position) {
        return mTrailerEntries.get(position);
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

        final TrailerEntry trailer = getItem(position);

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
