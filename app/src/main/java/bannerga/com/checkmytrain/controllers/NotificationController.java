package bannerga.com.checkmytrain.controllers;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bannerga.com.checkmytrain.json.RailQuery;
import bannerga.com.checkmytrain.view.notification.TrainNotification;

public class NotificationController extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        String departureStation = params.getExtras().getString("departureStation");
        String arrivalStation = params.getExtras().getString("arrivalStation");
        new AsyncNotificationJob(departureStation, arrivalStation).execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // false will reschedule the job if it  fails to finish
        return false;
    }

    public class AsyncNotificationJob extends AsyncTask<String, Void, Map> {

        private String departureStation;
        private String arrivalStation;

        public AsyncNotificationJob(String departureStation, String arrivalStation) {
            this.departureStation = departureStation;
            this.arrivalStation = arrivalStation;
        }

        //FIXME  returning empty hashmap for no reason
        @Override
        protected Map doInBackground(String... strings) {
            Map trainInfo = new HashMap();
            try {
                RailQuery railQuery = new RailQuery();
                JSONArray json = railQuery.getTimetableFor(departureStation);
                trainInfo = railQuery.getNextDepartureFor(json, arrivalStation);

            } catch (Exception e) {
                e.printStackTrace();
            }
            TrainNotification notification = new TrainNotification();
            notification.issueNotification(NotificationController.this, trainInfo);
            return new HashMap();
        }

        @Override
        protected void onPostExecute(Map result) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(NotificationController.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("departure_station", departureStation);
            editor.apply();

            ConfigurationController controller = new ConfigurationController(NotificationController.this);
            controller.scheduleJob(NotificationController.this,
                    departureStation, arrivalStation, TimeUnit.DAYS.toMillis(1));
        }
    }
}
