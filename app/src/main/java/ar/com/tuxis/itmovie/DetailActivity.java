package ar.com.tuxis.itmovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ar.com.tuxis.itmovie.Movie.Movie;

/**
 * Created by pdalmasso on 4/9/16.
 */
public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
    */
    public class DetailFragment extends Fragment {

        static final String DETAIL_MOVIE = "DETAIL_MOVIE";

        private final String LOG_TAG = DetailFragment.class.getSimpleName();
        private Movie objectsMovie;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        TextView movieTitleTextView;
        TextView movieDetailDescription;
        ImageView imageView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getIntent();

            Movie obj = (Movie) intent.getSerializableExtra("MyClass");
            movieTitleTextView = (TextView) rootView.findViewById(R.id.movieDetailTitle);
            movieTitleTextView.setText(obj.getTitle());
            movieDetailDescription = (TextView) rootView.findViewById(R.id.movieDetailDescription);
            movieDetailDescription.setText(obj.getOverview());
            imageView = (ImageView) rootView.findViewById(R.id.imageView);

            String image_url = "http://image.tmdb.org/t/p/w780" + obj.getBackdrop_path();

            Picasso.with(getContext()).load(image_url).into(imageView);
            return rootView;
        }
    }
}
