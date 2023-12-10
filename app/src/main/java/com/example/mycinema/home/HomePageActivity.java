package com.example.mycinema.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycinema.R;
import com.example.mycinema.all_movie.AllMoviesActivity;
import com.example.mycinema.booking_history.BookingHistoryActivity;
import com.example.mycinema.controller.DataMovieController;
import com.example.mycinema.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private RecyclerView carouselRecyclerView;
    private RecyclerView listMoviesRecyclerView;
    private MovieListAdapter listMovieAdapter;
    private List<String> imageUrlList;
    private List<Movie> movieDataList;
    private CarouselAdapter carouselAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setupCarousel();
        setupListMovies();
        setupViewAllMovies();
        loadMovieData();
    }

    private void initializeViews() {
        carouselRecyclerView = findViewById(R.id.home_carousel);
        listMoviesRecyclerView = findViewById(R.id.home_list_movies);
        ImageView bookingHistoryBtn = findViewById(R.id.home_menu);
        bookingHistoryBtn.setOnClickListener(view ->{
                Intent intent = new Intent(HomePageActivity.this, BookingHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
        });
    }

    private void setupCarousel() {
        imageUrlList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        carouselRecyclerView.setLayoutManager(layoutManager);

        int horizontalOffset = getResources().getDimensionPixelSize(R.dimen.carousel_item_offset);
        float minScale = 0.8f;
        carouselRecyclerView.addItemDecoration(new CarouselItemDecoration(horizontalOffset, minScale));

        carouselAdapter = new CarouselAdapter(this, imageUrlList);
        carouselRecyclerView.setAdapter(carouselAdapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(carouselRecyclerView);
    }

    private void setupListMovies() {
        movieDataList = new ArrayList<>();

        LinearLayoutManager listMovieLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listMoviesRecyclerView.setLayoutManager(listMovieLayoutManager);

        listMovieAdapter = new MovieListAdapter(this, movieDataList);
        listMoviesRecyclerView.setAdapter(listMovieAdapter);
    }

    private void setupViewAllMovies() {
        TextView viewAllMoviesTextView = findViewById(R.id.home_view_all);
        viewAllMoviesTextView.setOnClickListener(view -> switchToViewAllActivity());
    }

    private void loadMovieData() {
        DataMovieController dataMovieController = new DataMovieController(this);

        dataMovieController.getMovieList(10, new DataMovieController.OnMovieDataListReceivedListener() {
            @Override
            public void onMovieDataReceived(List<Movie> receivedMovieData) {
                handleMovieData(receivedMovieData);
            }

            @Override
            public void onMovieDataError(String errorMessage) {
                // Handle the error
            }
        });
    }

    private void handleMovieData(List<Movie> receivedMovieData) {
        movieDataList.clear();
        movieDataList.addAll(receivedMovieData);

        int count =0;
        for (Movie mv : receivedMovieData) {
            if(count++ > 5)
                break;
            imageUrlList.add(mv.getImgUrl());
        }

        listMovieAdapter.notifyDataSetChanged();
        carouselAdapter.notifyDataSetChanged();
    }

    private void switchToViewAllActivity() {
        startActivity(new Intent(HomePageActivity.this, AllMoviesActivity.class));
    }
}
