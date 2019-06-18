package bannerga.com.checkmytrain.view.activity.cards;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class CardActivity extends AppCompatActivity {

    private TextView departureStationText;
    private TextView arrivalStationText;
    private TextView timeText;
    private ImageView deleteIcon;

    private static void onFloatingButtonClick(View view) {
        Snackbar.make(view, "Replace", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(CardActivity::onFloatingButtonClick);
        new DisplaySavedJourneysAsyncTask().execute();
    }

    public class DisplaySavedJourneysAsyncTask extends AsyncTask<String, Void, List<Journey>> {
        private AppDatabase db;

        @Override
        protected List<Journey> doInBackground(String... strings) {
            db = Room.databaseBuilder(CardActivity.this, AppDatabase.class, "checkmytrain.db")
                    .fallbackToDestructiveMigration()
                    .build();
            JourneyDAO dao = db.dao();
            return dao.getAll();
        }

        @Override
        protected void onPostExecute(List<Journey> journeys) {
            for (Journey journey : journeys) {
                runOnUiThread(() -> {
                    LinearLayout container = findViewById(R.id.container);
                    LinearLayout parent = new LinearLayout(CardActivity.this);
                    View cardView = getLayoutInflater().inflate(R.layout.card, parent);
                    container.addView(cardView);
                    departureStationText = cardView.findViewById(R.id.departureStationText);
                    departureStationText.setText(journey.getOrigin());
                    arrivalStationText = cardView.findViewById(R.id.arrivalStationText);
                    arrivalStationText.setText(journey.getDestination());
                    timeText = cardView.findViewById(R.id.timeText);
                    String time = journey.getHour() + " : " + journey.getMinute();
                    timeText.setText(time);
                    deleteIcon = cardView.findViewById(R.id.remove);
                    deleteIcon.setOnClickListener(new OnRemoveClickListener(CardActivity.this, journey, cardView));
                });
            }
        }
    }


}
