package bannerga.com.checkmytrain.view.activity.journey;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.facebook.stetho.Stetho;
import com.google.android.material.textfield.TextInputEditText;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.data.model.Journey;
import bannerga.com.checkmytrain.data.station.PopulateStationTableAsyncTask;
import bannerga.com.checkmytrain.input.StationTextWatcher;
import bannerga.com.checkmytrain.view.activity.cards.CardActivity;
import bannerga.com.checkmytrain.view.fragment.TimePickerFragment;

public class JourneyActivity extends AppCompatActivity {

    private AutoCompleteTextView arrivalStationText;
    private AutoCompleteTextView departureStationText;
    private TextInputEditText timeText;
    private int hourOfDay;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_journey);
        Toolbar toolbar = findViewById(R.id.journey_toolbar);
        setSupportActionBar(toolbar);

        Button submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this::onSubmitClick);

        departureStationText = findViewById(R.id.departure_station_input);
        departureStationText.addTextChangedListener(new StationTextWatcher(departureStationText, this));
        arrivalStationText = findViewById(R.id.arrival_station_input);
        arrivalStationText.addTextChangedListener(new StationTextWatcher(arrivalStationText, this));
        timeText = findViewById(R.id.time_input);
        timeText.setOnClickListener(this::showTimePickerDialog);
        new PopulateStationTableAsyncTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pending_jobs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_cards:
                Intent intent = new Intent(this, CardActivity.class);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "timePicker");
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
        String origin = departureStationText.getText().toString();
        String destination = arrivalStationText.getText().toString();
        String time = timeText.getText().toString();

        if (!origin.equals("") || !destination.equals("") || !time.equals("")) {
            Journey journey = new Journey();
            journey.setOrigin(origin);
            journey.setDestination(destination);
            journey.setHour(hourOfDay);
            journey.setMinute(minute);

            JourneyController controller = new JourneyController();
            String workId = controller.scheduleWork(journey, this);
            journey.setUuid(workId);
            controller.saveJourney(journey, this);
            Toast.makeText(this, "Journey added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Enter journey details", Toast.LENGTH_LONG).show();
        }
    }
}