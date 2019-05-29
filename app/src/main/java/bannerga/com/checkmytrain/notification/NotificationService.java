package bannerga.com.checkmytrain.notification;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.room.Room;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.StationDAO;
import bannerga.com.checkmytrain.json.RailQuery;
import bannerga.com.checkmytrain.view.notification.TrainNotification;

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

        public NotificationAsyncTask(String departureStation, String arrivalStation) {
            this.departureStation = departureStation;
            this.arrivalStation = arrivalStation;
        }

        //FIXME  returning empty hashmap for no reason
        @Override
        protected Map doInBackground(String... strings) {
            Map trainInfo = new HashMap();
            try {
                AppDatabase db = Room.databaseBuilder(NotificationService.this, AppDatabase.class, "checkmytrain.db")
                        .fallbackToDestructiveMigration()
                        .build();
                StationDAO stationDAO = db.stationDao();
                RailQuery railQuery = new RailQuery();
                JSONArray json = railQuery.getTimetableFor(stationDAO.findByName(departureStation).getCrs());
                trainInfo = railQuery.getNextDepartureFor(json, arrivalStation);

            } catch (Exception e) {
                e.printStackTrace();
            }
            TrainNotification notification = new TrainNotification();
            notification.issueNotification(NotificationService.this, trainInfo);
            return new HashMap();
        }

        @Override
        protected void onPostExecute(Map result) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(NotificationService.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("departure_station", departureStation);
            editor.apply();

            NotificationJob controller = new NotificationJob(NotificationService.this);
            controller.schedule(NotificationService.this,
                    departureStation, arrivalStation, TimeUnit.DAYS.toMillis(1));
        }
    }
}
