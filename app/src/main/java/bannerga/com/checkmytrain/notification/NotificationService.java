package bannerga.com.checkmytrain.notification;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.json.Timetable;
import bannerga.com.checkmytrain.view.notification.TrainNotification;

/***
 * Service that will query the journey details and issue a notification on the train's punctuality.
 */
public class NotificationService extends JobService {


    private String departureStation;
    private String arrivalStation;
    private int hour;
    private int minute;

    @Override
    public boolean onStartJob(JobParameters params) {
        departureStation = params.getExtras().getString("departureStation");
        arrivalStation = params.getExtras().getString("arrivalStation");
        hour = params.getExtras().getInt("hour");
        minute = params.getExtras().getInt("minute");
        new NotificationAsyncTask().execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // false will reschedule the job if it  fails to finish
        return false;
    }

    public class NotificationAsyncTask extends AsyncTask<String, Void, Map> {

        private Context context = NotificationService.this;
        private AppDatabase db;

        public NotificationAsyncTask() {
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
            NotificationJob job = new NotificationJob(context);
            job.scheduleJob(departureStation, arrivalStation, hour, minute);
        }

        private void issueNotifcation() {
            Map trainInfo = new HashMap();
            try {
                String departureStationCode = db.stationDao().findByName(departureStation).getCrs();
                Timetable timetable = new Timetable();
                JSONArray journeys = timetable.getAllJourneys(departureStationCode);
                trainInfo = timetable.getNextJourney(journeys, arrivalStation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TrainNotification notification = new TrainNotification();
            notification.issueNotification(context, trainInfo);
        }
    }
}
