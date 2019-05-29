package bannerga.com.checkmytrain.view.activity.notification;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;

public class NotificationActivity extends AppCompatActivity {

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
        fab.setOnClickListener(NotificationActivity::onFloatingButtonClick);
        new ExistingJourneysAsyncTask().execute();
    }

    public class ExistingJourneysAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            AppDatabase db = Room.databaseBuilder(NotificationActivity.this,
                    AppDatabase.class, "checkmytrain.db")
                    .fallbackToDestructiveMigration()
                    .build();
            JourneyDAO dao = db.dao();
            List<Journey> journeys = dao.getAll();

            for (Journey journey : journeys) {
                runOnUiThread(() -> {
                    LinearLayout container = findViewById(R.id.container);
                    LinearLayout parent = new LinearLayout(NotificationActivity.this);
                    View cardView = getLayoutInflater().inflate(R.layout.card, parent);
                    container.addView(cardView);
                    departureStationText = cardView.findViewById(R.id.departureStationText);
                    departureStationText.setText(journey.getOrigin());
                    arrivalStationText = cardView.findViewById(R.id.arrivalStationText);
                    arrivalStationText.setText(journey.getDestination());
                    timeText = cardView.findViewById(R.id.timeText);
                    timeText.setText(journey.getTime());
                });
            }
            return "pass";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }


}
