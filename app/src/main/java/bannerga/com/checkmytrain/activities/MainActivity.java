package bannerga.com.checkmytrain.activities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.controllers.ConfigurationController;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;
import bannerga.com.checkmytrain.data.JourneyDatabase;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText departureStationEditText;
    private TextInputEditText arrivalStationEditText;
    private TextInputEditText timeEditText;
    private int hourOfDay;
    private int minute;
    ConfigurationController controller = new ConfigurationController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_configuration);
        departureStationEditText = findViewById(R.id.departure_station_input);
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
        new AsyncDatabaseTask(
                departureStationEditText.getText().toString(),
                arrivalStationEditText.getText().toString(),
                timeEditText.getText().toString())
                .execute();

        Intent intent = new Intent(this, PendingJobsActivity.class);
        startActivity(intent);
    }

    public class AsyncDatabaseTask extends AsyncTask<String, String, String> {

        private String departureStation;
        private String arrivalStation;
        private String time;

        public AsyncDatabaseTask(String departureStation, String arrivalStation, String time) {
            this.departureStation = departureStation;
            this.arrivalStation = arrivalStation;
            this.time = time;
        }

        @Override
        protected String doInBackground(String... strings) {
            JourneyDatabase db = Room.databaseBuilder(MainActivity.this, JourneyDatabase.class, "journeys.db")
                    .fallbackToDestructiveMigration()
                    .build();
            Journey journey = new Journey();
            journey.setDestination(arrivalStation);
            journey.setOrigin(departureStation);
            journey.setTime(time);
            JourneyDAO dao = db.dao();
            dao.insertAll(journey);
            return "pass";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

}