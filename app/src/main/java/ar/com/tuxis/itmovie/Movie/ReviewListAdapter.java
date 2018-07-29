package ar.com.tuxis.itmovie.Movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ar.com.tuxis.itmovie.R;
import ar.com.tuxis.itmovie.database.Review.ReviewEntry;
import ar.com.tuxis.itmovie.database.Trailer.TrailerEntry;

/**
 * Created by pdalmasso on 14/11/17.
 */
public class ReviewListAdapter extends BaseAdapter {

    private ReviewEntry review = new ReviewEntry();
    private List<ReviewEntry> mReviewEntries;
    private Context context;
    private final LayoutInflater layoutInflater;


    public ReviewListAdapter(Context p_context) {
        layoutInflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = p_context;
    }

    public ReviewListAdapter(Context p_context, List<ReviewEntry> p_objects) {
        layoutInflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = p_context;
        this.mReviewEntries = p_objects;
    }

    public void clear() {
        synchronized (this.review) {
            this.mReviewEntries.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<ReviewEntry> data) {
        mReviewEntries = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mReviewEntries != null){
            return mReviewEntries.size();
        }else{
            return 0;
        }
    }

    @Override
    public ReviewEntry getItem(int position) {
        return mReviewEntries.get(position);
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
            view = this.layoutInflater.inflate(R.layout.movie_detail_review, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final ReviewEntry review = getItem(position);

        String author = review.getAuthor();
        String content = review.getContent();

        viewHolder = (ViewHolder) view.getTag();
        viewHolder.author_titleView.setText(author);
        viewHolder.content_titleView.setText(content);


        return view;
    }

    public static class ViewHolder {
        public final TextView author_titleView;
        public final TextView content_titleView;

        public ViewHolder(View view) {
            author_titleView = (TextView) view.findViewById(R.id.movie_review_author_textview);
            content_titleView = (TextView) view.findViewById(R.id.movie_review_content_textview);
        }
    }
}
