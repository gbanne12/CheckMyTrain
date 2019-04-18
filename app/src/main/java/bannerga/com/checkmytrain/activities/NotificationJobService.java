package bannerga.com.checkmytrain.activities;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bannerga.com.checkmytrain.controllers.ConfigurationController;
import bannerga.com.checkmytrain.notification.TrainNotification;
import bannerga.com.checkmytrain.query.Itinerary;

public class NotificationJobService extends JobService {

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
                Itinerary itinerary = new Itinerary();
                JSONArray json = itinerary.getTimetableFor(departureStation);
                trainInfo = itinerary.getNextDepartureFor(json, arrivalStation);

            } catch (Exception e) {
                e.printStackTrace();
            }
            TrainNotification notification = new TrainNotification();
            notification.issueNotification(NotificationJobService.this, trainInfo);
            return new HashMap();
        }

        @Override
        protected void onPostExecute(Map result) {
            ConfigurationController controller = new ConfigurationController();
            controller.scheduleJob(NotificationJobService.this,
                    departureStation, arrivalStation, TimeUnit.DAYS.toMillis(1));
        }
    }
}
