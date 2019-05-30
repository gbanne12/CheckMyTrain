package bannerga.com.checkmytrain.view.activity.journey;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;
import bannerga.com.checkmytrain.notification.NotificationJob;
import bannerga.com.checkmytrain.view.activity.notification.NotificationActivity;
import bannerga.com.checkmytrain.view.autocompletetextview.StationTextWatcher;
import bannerga.com.checkmytrain.view.fragment.TimePickerFragment;

public class JourneyActivity extends AppCompatActivity {

    private NotificationJob notificationJob = new NotificationJob();
    private AutoCompleteTextView arrivalStationText;
    private AutoCompleteTextView departureStationText;
    private TextInputEditText timeText;
    private int hourOfDay;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        Button submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this::onSubmitClick);
        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(this::onCancelClick);
        Button pendingJobsButton = findViewById(R.id.button_pending_jobs);
        pendingJobsButton.setOnClickListener(this::onPendingJobsClick);

        departureStationText = findViewById(R.id.departure_station_input);
        departureStationText.addTextChangedListener(new StationTextWatcher(departureStationText, this));
        arrivalStationText = findViewById(R.id.arrival_station_input);
        arrivalStationText.addTextChangedListener(new StationTextWatcher(arrivalStationText, this));
        timeText = findViewById(R.id.time_input);
        timeText.setOnClickListener(this::showTimePickerDialog);
        new PopulateStationTableAsyncTask(this).execute();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setTime(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        String time = hourOfDay + ":" + minute;
        if (minute < 10) {
            time = hourOfDay + ":0" + minute;
        }
        timeText.setText(time);
    }

    public void onSubmitClick(View view) {
        boolean isMissingUserInput = timeText.getText().toString().equals("")
                || arrivalStationText.getText().toString().equals("");

        if (!isMissingUserInput) {
            String departureStation = departureStationText.getText().toString();
            String arrivalStation = arrivalStationText.getText().toString();
            long offset = notificationJob.getOffsetInMillis(hourOfDay, minute);
            notificationJob.scheduleJob(this, departureStation, arrivalStation, offset);
            new SaveJourneyAsyncTask(departureStation, arrivalStation).execute();
        } else {
            Toast.makeText(this, "Enter station details", Toast.LENGTH_LONG).show();
        }
    }

    private void onCancelClick(View v) {
        notificationJob.cancelJob(this);
    }

    private void onPendingJobsClick(View v) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    private class SaveJourneyAsyncTask extends AsyncTask<String, Void, String> {

        private String departureStation;
        private String arrivalStation;
        private AppDatabase db;

        public SaveJourneyAsyncTask(String departureStation, String arrivalStation) {
            this.departureStation = departureStation;
            this.arrivalStation = arrivalStation;
            db = Room.databaseBuilder(JourneyActivity.this, AppDatabase.class, "checkmytrain.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        @Override
        protected String doInBackground(String... strings) {
            Journey journey = new Journey();
            journey.setDestination(arrivalStation);
            journey.setOrigin(departureStation);
            journey.setGuid(UUID.randomUUID().toString());
            String time = new SimpleDateFormat("HH-mm").format(new Date());
            journey.setTime(time);
            JourneyDAO dao = db.dao();
            dao.insertAll(journey);
            return "pass";
        }

        @Override
        protected void onPostExecute(String adapter) {

        }
    }
}