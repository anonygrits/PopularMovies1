package com.example.android.popularmovies1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import com.example.android.popularmovies1.Utilities.MovieJSONUtils;
import com.example.android.popularmovies1.Utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Movie> mMovies = new ArrayList<>();
    private final String POPULAR_MOVIES_URL="https://api.themoviedb.org/3/movie/popular?";
    private final String TOP_RATED_MOVIES_URL="https://api.themoviedb.org/3/movie/top_rated?";

    private static String API_KEY_TAG="api_key";

    // todo remove before commiting to github!
    private static String API = "";

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get movies list (default ordering is by popularity)
        new FetchMovieListTask().execute(POPULAR_MOVIES_URL);

        // get reference to RecyclerView in xml
        mRecyclerView = findViewById(R.id.recyclerview);

        // changes in content don't change child layout size (performance)
        mRecyclerView.setHasFixedSize(true);

        // set up grid layout manager
        int numberColumns = 2;  // todo move this somewhere later?
        mGridLayoutManager = new GridLayoutManager(this, numberColumns);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // set up adapter to display each item in grid
        mAdapter = new MyAdapter(mMovies);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class FetchMovieListTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        @Override
        protected ArrayList<Movie> doInBackground(String... urls) {
            if (urls.length == 0) return null;

            String movieListURL = urls[0];
            try {
                String moviesListJSON = NetworkUtils.getMovieList(movieListURL,API_KEY_TAG,API);
                ArrayList<Movie> moviesArrayList = MovieJSONUtils.getMovieArrayList(moviesListJSON);
                return moviesArrayList;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();  // todo figure out what to do if this happens
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            mMovies = movies;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_ordering, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.popular_movies) {
            new FetchMovieListTask().execute(POPULAR_MOVIES_URL);
            mAdapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.top_rated_movies) {
            new FetchMovieListTask().execute(TOP_RATED_MOVIES_URL);
            mAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
