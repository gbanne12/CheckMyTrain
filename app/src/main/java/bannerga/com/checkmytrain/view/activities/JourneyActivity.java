package bannerga.com.checkmytrain.view.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.controllers.ConfigurationController;

public class JourneyActivity extends AppCompatActivity {

    private ConfigurationController controller = new ConfigurationController(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            controller.scheduleJob(this, departureStation, arrivalStation, hourOfDay, minute);
        } else {
            Toast.makeText(this, "Enter station details", Toast.LENGTH_LONG).show();
        }
    }

    private void onCancelClick(View v) {
        controller.cancelJob(this);
    }

    private void onPendingJobsClick(View v) {
        Intent intent = new Intent(this, PendingJobsActivity.class);
        startActivity(intent);
    }
}