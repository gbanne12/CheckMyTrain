package bannerga.com.checkmytrain.view.activity.journey;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.facebook.stetho.Stetho;
import com.google.android.material.textfield.TextInputEditText;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.notification.NotificationJob;
import bannerga.com.checkmytrain.view.activity.notification.NotificationActivity;
import bannerga.com.checkmytrain.view.autocompletetextview.StationTextWatcher;
import bannerga.com.checkmytrain.view.fragment.TimePickerFragment;

public class JourneyActivity extends AppCompatActivity {

    private NotificationJob notificationJob = new NotificationJob(this);
    private AutoCompleteTextView arrivalStationText;
    private AutoCompleteTextView departureStationText;
    private TextInputEditText timeText;
    private int hourOfDay;
    private int minute;
    private int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
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
                || arrivalStationText.getText().toString().equals("")
                || departureStationText.getText().toString().equals("");

        if (!isMissingUserInput) {
            notificationJob.scheduleJob(
                    departureStationText.getText().toString(),
                    arrivalStationText.getText().toString(),
                    hourOfDay,
                    minute);
        } else {
            Toast.makeText(this, "Enter station details", Toast.LENGTH_LONG).show();
        }
    }

    private void onCancelClick(View v) {
        // notificationJob.cancelJob(this, jobId);
    }

    private void onPendingJobsClick(View v) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

}