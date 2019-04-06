package bannerga.com.checkmytrain.activities;

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

public class MainActivity extends AppCompatActivity {

    private TextInputEditText departureStationEditText;
    private TextInputEditText arrivalStationEditText;
    private Button submitButton;
    private Button cancelButton;
    private int hourOfDay;
    private int minute;
    ConfigurationController controller = new ConfigurationController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_configuration);
        departureStationEditText = findViewById(R.id.departure_station_input);
        arrivalStationEditText = findViewById(R.id.arrival_station_input);
        submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this::onSubmitClick);
        cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener((View v) -> controller.cancelJob(this));
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

    public void onSubmitClick(View view) {
        if (!departureStationEditText.getText().toString().equals("")
                || !arrivalStationEditText.getText().toString().equals("")) {
            controller.scheduleJob(this);
        } else {
            Toast.makeText(this, "Enter station details", Toast.LENGTH_LONG).show();
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setTime(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        TextInputEditText timeEditText = findViewById(R.id.time_input);
        timeEditText.setText(Integer.toString(hourOfDay) + ":" + Integer.toString(minute));
    }
}
