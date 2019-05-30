package bannerga.com.checkmytrain.notification;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.json.RailQuery;
import bannerga.com.checkmytrain.view.notification.TrainNotification;

/***
 * Service that will query the journey details and issue a notification on the train's punctuality.
 */
public class NotificationService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        String departureStation = params.getExtras().getString("departureStation");
        String arrivalStation = params.getExtras().getString("arrivalStation");
        new NotificationAsyncTask(departureStation, arrivalStation).execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // false will reschedule the job if it  fails to finish
        return false;
    }

    public class NotificationAsyncTask extends AsyncTask<String, Void, Map> {

        private String departureStation;
        private String arrivalStation;
        Context context = NotificationService.this;
        private AppDatabase db;

        public NotificationAsyncTask(String departureStation, String arrivalStation) {
            this.departureStation = departureStation;
            this.arrivalStation = arrivalStation;
            db = Room.databaseBuilder(context, AppDatabase.class, "checkmytrain.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        //FIXME  returning empty hashmap for no reason
        @Override
        protected Map doInBackground(String... strings) {
            issueNotifcation();
            return new HashMap();
        }

        @Override
        protected void onPostExecute(Map result) {
            NotificationJob job = new NotificationJob();
            job.scheduleJob(context, departureStation, arrivalStation, TimeUnit.DAYS.toMillis(1));
        }

        private void issueNotifcation() {
            Map trainInfo = new HashMap();
            try {
                String departureStationCode = db.stationDao().findByName(departureStation).getCrs();
                RailQuery railQuery = new RailQuery();
                JSONArray json = railQuery.getTimetableFor(departureStationCode);
                trainInfo = railQuery.getNextDepartureFor(json, arrivalStation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TrainNotification notification = new TrainNotification();
            notification.issueNotification(context, trainInfo);
        }
    }
}
