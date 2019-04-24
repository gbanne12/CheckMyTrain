package bannerga.com.checkmytrain.view.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.controllers.ConfigurationController;
import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Station;
import bannerga.com.checkmytrain.data.StationDAO;

public class JourneyActivity extends AppCompatActivity {

    private TextInputEditText departureStationEditText;
    private TextInputEditText arrivalStationEditText;
    private TextInputEditText timeEditText;
    private int hourOfDay;
    private int minute;
    ConfigurationController controller = new ConfigurationController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
        departureStationEditText = findViewById(R.id.departure_station_input);
        departureStationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 2) {
                    new AsyncStationSearchJob(departureStationEditText.getText().toString()).execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        arrivalStationEditText = findViewById(R.id.arrival_station_input);
        timeEditText = findViewById(R.id.time_input);
        timeEditText.setOnClickListener(this::showTimePickerDialog);
        Button submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this::onSubmitClick);
        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(this::onCancelClick);
        Button pendingJobsButton = findViewById(R.id.button_pending_jobs);
        pendingJobsButton.setOnClickListener(this::onPendingJobsClick);
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
        TextInputEditText timeEditText = findViewById(R.id.time_input);
        String time = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
        if (minute < 10) {
            time = new StringBuilder().append(hourOfDay).append(":0").append(minute).toString();
        }
        timeEditText.setText(time);
    }

    public void onSubmitClick(View view) {
        boolean isMissingUserInput = timeEditText.getText().toString().equals("")
                || departureStationEditText.getText().toString().equals("")
                || arrivalStationEditText.getText().toString().equals("");

        if (!isMissingUserInput) {
            String departureStation = departureStationEditText.getText().toString();
            String arrivalStation = arrivalStationEditText.getText().toString();
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

    public class AsyncStationSearchJob extends AsyncTask<String, Void, String> {

        private String input;

        public AsyncStationSearchJob(String input) {
            this.input = input;
        }

        @Override
        protected String doInBackground(String... strings) {
            AppDatabase db =
                    Room.databaseBuilder(JourneyActivity.this, AppDatabase.class, "journey.db").build();

            StationDAO stationDAO = db.stationDao();
            List<Station> stations = stationDAO.findByPartialName("%" + input + "%");
            List<String> names = new ArrayList<>();
            for (Station station : stations) {
                String name = station.getName();
                names.add(name);
            }
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(JourneyActivity.this,
                            android.R.layout.simple_list_item_1, names);

            ListView list = findViewById(R.id.singleRow);
            runOnUiThread(() -> list.setAdapter(adapter));
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}