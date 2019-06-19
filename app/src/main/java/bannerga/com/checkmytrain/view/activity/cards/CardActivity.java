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

import java.util.List;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;

public class CardActivity extends AppCompatActivity {

    private TextView noJourneysMessage;
    private TextView departureStationText;
    private TextView arrivalStationText;
    private TextView timeText;
    private ImageView deleteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        noJourneysMessage = findViewById(R.id.no_journeys_message);
        noJourneysMessage.setText(R.string.message_loading);
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
                    int hour = journey.getHour();
                    int minute = journey.getMinute();
                    String time;
                    if (minute < 10) {
                        time = hour + ":0" + minute;
                    } else {
                        time = hour + ":" + minute;
                    }
                    timeText.setText(time);
                    deleteIcon = cardView.findViewById(R.id.remove);
                    deleteIcon.setOnClickListener(new OnRemoveClickListener(CardActivity.this, journey, cardView));
                });
            }
            if (journeys.size() == 0) {
                noJourneysMessage.setText(R.string.message_empty);
            } else {
                noJourneysMessage.setVisibility(View.GONE);
            }
        }
    }


}
