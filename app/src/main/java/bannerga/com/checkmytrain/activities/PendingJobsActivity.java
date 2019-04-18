package bannerga.com.checkmytrain.activities;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;
import bannerga.com.checkmytrain.data.JourneyDatabase;

public class PendingJobsActivity extends AppCompatActivity {

    private TextView departureStationText;
    private TextView arrivalStationText;
    private TextView timeText;

    private static void onFloatingButtonClick(View view) {
        Snackbar.make(view, "Replace", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_jobs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(PendingJobsActivity::onFloatingButtonClick);
        departureStationText = findViewById(R.id.departureStationText);
        arrivalStationText = findViewById(R.id.arrivalStationText);
        timeText = findViewById(R.id.timeText);
        new AsyncReadDatabaseTask().execute();
    }

    public class AsyncReadDatabaseTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            JourneyDatabase db = Room.databaseBuilder(PendingJobsActivity.this, JourneyDatabase.class, "journeys.db")
                    .fallbackToDestructiveMigration()
                    .build();
            JourneyDAO dao = db.dao();
            List<Journey> journeys = dao.getAll();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    departureStationText.setText(journeys.get(0).getOrigin());
                    arrivalStationText.setText(journeys.get(0).getDestination());
                    timeText.setText(journeys.get(0).getTime());
                    // Stuff that updates the UI
                }
            });


            return "pass";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }


}
