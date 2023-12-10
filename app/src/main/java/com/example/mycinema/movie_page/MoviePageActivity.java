// MoviePageActivity.java
package com.example.mycinema.movie_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycinema.R;
import com.example.mycinema.auth.LoginActivity;
import com.example.mycinema.controller.DataMovieController;
import com.example.mycinema.model.Movie;
import com.example.mycinema.model.Theater;
import com.example.mycinema.seat_booking.SeatBookingActivity;
import com.example.mycinema.util.StringUtil;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MoviePageActivity extends AppCompatActivity {

    private RecyclerView listTheatersRecyclerView;
    private TheaterAdapter theaterAdapter;
    private List<Theater> theaterList;
    private List<String> calendarList;
    private Movie movie;
    private CalendarAdapter calendarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_page);

        initializeViews();
        getMovieDataFromIntent();
        initializeRecyclerViews();
        setupButtons();
    }

    private void initializeViews() {
        listTheatersRecyclerView = findViewById(R.id.mp_theater_recylcer);
    }

    private void getMovieDataFromIntent() {
        Intent intent = getIntent();
        String movieId = intent.getStringExtra("movieId");

        DataMovieController dataMovieController = new DataMovieController(this);
        dataMovieController.getMovieById(movieId, new DataMovieController.OnMovieDataReceivedListener() {
            @Override
            public void onMovieDataReceived(Movie movieData) {
                handleMovieData(movieData);
            }

            @Override
            public void onMovieDataError(String errorMessage) {
                // Handle the error
            }
        });
    }

    private void initializeRecyclerViews() {
        // Set layout manager for listTheatersRecyclerView
        listTheatersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Initialize adapters
        theaterList = new ArrayList<>();
        calendarList = new ArrayList<>();
        theaterAdapter = new TheaterAdapter(theaterList);
        listTheatersRecyclerView.setAdapter(theaterAdapter);

        // Initialize calendar adapter
        calendarAdapter = new CalendarAdapter(calendarList, movie, theaterAdapter);

        RecyclerView calendarRecyclerView = findViewById(R.id.mp_calendar);
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private void setupButtons() {
        ImageView backBtn = findViewById(R.id.mp_back_activity);
        ImageView nextBtn = findViewById(R.id.mp_next_activity);

        backBtn.setOnClickListener(v -> finish());

        nextBtn.setOnClickListener(v -> {
            String chosenTheater  = theaterAdapter.getChosenTheater();
            String chosenDate  = calendarAdapter.getChosenDate();
            String chosenTime  = theaterAdapter.getChosenTime();
            if(chosenTime == null || chosenDate == null || chosenTheater == null)
            {
                Toast.makeText(MoviePageActivity.this,"Hãy chọn một lịch",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            startSeatBookingActivity(movie,calendarAdapter.getChosenDate(),theaterAdapter.getChosenTheater(),theaterAdapter.getChosenTime());
        });
    }

    private void handleMovieData(Movie mv) {
        movie = mv;
        List<String> calendarData = new ArrayList<>(movie.getSchedules().keySet());
        calendarData = StringUtil.sortDates(calendarData);
        calendarList.clear();
        calendarList.addAll(calendarData);
        calendarAdapter.setMovie(movie);
        calendarAdapter.notifyDataSetChanged();

        ImageView img = findViewById(R.id.mp_image);
        Picasso.get().load(movie.getImgUrl()).into(img);

        TextView name = findViewById(R.id.mp_movie_name);
        name.setText(movie.getName());

        TextView duration = findViewById(R.id.mp_duration);
        duration.setText(movie.getDuration() + " mins");

        Button playVideoButton = findViewById(R.id.mp_trailer_btn);
        playVideoButton.setOnClickListener(v -> showYouTubeVideo());
    }

    private void startSeatBookingActivity(Movie mv, String date, String theater, String time) {
        Intent intent = new Intent(MoviePageActivity.this, SeatBookingActivity.class);
        intent.putExtra("movieId", mv.getId());
        intent.putExtra("theater", theater);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        startActivity(intent);
    }

    private void showYouTubeVideo() {
        if (movie != null) {
            String videoUrl = movie.getTrailerUrl();
            YouTubeDialogHelper.showYouTubeVideoDialog(this, videoUrl);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseResources();
    }

    private void releaseResources() {
        if (theaterAdapter != null) {
            theaterAdapter.releaseResources();
        }

        if (calendarAdapter != null) {
            calendarAdapter.releaseResources();
        }
    }
}
