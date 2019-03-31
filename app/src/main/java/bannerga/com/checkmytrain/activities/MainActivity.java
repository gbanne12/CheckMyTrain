package bannerga.com.checkmytrain.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.controllers.ConfigurationController;
import bannerga.com.checkmytrain.service.NotificationReceiver;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText stationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_configuration);
        stationEditText = findViewById(R.id.station_input);
        scheduleAlarm();
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
        if (!stationEditText.getText().toString().equals("")) {
            new getTrainInfoTask().execute();
        } else {
            Toast.makeText(this, "No station name entered", Toast.LENGTH_SHORT).show();
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setTime(String time) {
        TextInputEditText timeEditText = findViewById(R.id.time_input);
        timeEditText.setText(time);
    }

    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        // Create a PendingIntent to be triggered when the alarm goes off. Intent will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent notificationIntent = PendingIntent.getBroadcast(
                this,
                NotificationReceiver.REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis();
        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 54);


        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, notificationIntent);
    }

    private class getTrainInfoTask extends AsyncTask<String, Void, Map> {
        @Override
        protected Map doInBackground(String... strings) {
            Map trainInfo = new HashMap();
            try {
                ConfigurationController controller = new ConfigurationController();
                JSONObject json = controller.getJSONResponse(stationEditText.getText().toString());
                trainInfo = controller.getTrainInformation(json, "Glasgow Central");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return trainInfo;
        }

        @Override
        protected void onPostExecute(Map result) {
            Toast.makeText(MainActivity.this, result.get("time").toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
