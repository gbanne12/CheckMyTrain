package bannerga.com.checkmytrain.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.controllers.ConfigurationController;
import bannerga.com.checkmytrain.service.NotificationReceiver;

public class MainActivity extends AppCompatActivity {

    private JobScheduler mScheduler;
    private static final int JOB_ID = 0;

    private TextInputEditText departureStationEditText;
    private TextInputEditText arrivalStationEditText;
    private Button submitButton;
    private Button cancelButton;
    private int hourOfDay;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_configuration);
        departureStationEditText = findViewById(R.id.departure_station_input);
        arrivalStationEditText = findViewById(R.id.arrival_station_input);
        submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitClick(view);
            }
        });

        cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClick(view);
            }
        });


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

            // FIXME use async task instead of main thread
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            // break
            mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            ComponentName service = new ComponentName(getPackageName(), NotificationJobService.class.getName());
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, service);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            JobInfo myJobInfo = builder.build();
            mScheduler.schedule(myJobInfo);
            Toast.makeText(this, "Job Scheduled, job will run when " +
                    "the constraints are met.", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(this, "Enter station details", Toast.LENGTH_LONG).show();
        }
    }

    public void onCancelClick(View view) {
        if (mScheduler!=null){
            mScheduler.cancelAll();
            mScheduler = null;
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show();
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

    private class GetTrainInfoTask extends AsyncTask<String, Void, Map> {

        //FIXME  returning empty hashmap for no reason
        @Override
        protected Map doInBackground(String... strings) {
            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("departure_station_name", departureStationEditText.getText().toString());
            intent.putExtra("arrival_station_name", arrivalStationEditText.getText().toString());
            intent.putExtra("hourOfDay", hourOfDay);
            intent.putExtra("minute", minute);
            ConfigurationController controller = new ConfigurationController();
            controller.scheduleAlarm(MainActivity.this, intent);
            return new HashMap();
        }

        @Override
        protected void onPostExecute(Map result) {
            Toast.makeText(MainActivity.this, "Notification added", Toast.LENGTH_SHORT).show();
        }
    }
}
