package bannerga.com.checkmytrain.controllers;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.widget.Toast;

import androidx.room.Room;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;
import bannerga.com.checkmytrain.notification.NotificationService;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class JourneyController {
    private Context context;
    private int jobId;

    public JourneyController(Context context) {
        this.context = context;
    }
    private JobScheduler scheduler;

    public void scheduleJob(Context context, String departureStation, String arrivalStation, int hourOfDay, int minute) {
        ZonedDateTime now = ZonedDateTime.now();
        long nowInMillis = now.toInstant().toEpochMilli();
        ZonedDateTime notificationTime = ZonedDateTime.now().with(LocalTime.of(hourOfDay, minute));
        long notificationTimeInMillis = notificationTime.toInstant().toEpochMilli();

        long offset;
        boolean isRequestAheadOfCurrentTime = notificationTimeInMillis > nowInMillis;
        if (isRequestAheadOfCurrentTime) {
            offset = notificationTimeInMillis - nowInMillis;
        } else {
            // Time has passed for today, set the job for tomorrow
            ZonedDateTime tomorrow = notificationTime.plusDays(1);
            long tomorrowInMillis = tomorrow.toInstant().toEpochMilli();
            offset = tomorrowInMillis - nowInMillis;
        }

        scheduleJob(context, departureStation, arrivalStation, offset);
        new WriteJobAsyncTask(departureStation, arrivalStation, hourOfDay + ":" + minute).execute();
    }

    public int scheduleJob(Context context, String departureStation, String arrivalStation, long offset) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("departureStation", departureStation);
        bundle.putString("arrivalStation", arrivalStation);

        scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName service = new ComponentName(context.getPackageName(), NotificationService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(jobId, service);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setMinimumLatency(offset)
                .setExtras(bundle);
        JobInfo myJobInfo = builder.build();
        scheduler.schedule(myJobInfo);
        return myJobInfo.getId();
    }

    public void cancelJob(Context context) {
        if (scheduler != null) {
            scheduler.cancelAll();
            scheduler = null;
            Toast.makeText(context, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public class WriteJobAsyncTask extends AsyncTask<String, String, String> {
        private String departureStation;
        private String arrivalStation;
        private String time;

        public WriteJobAsyncTask(String departureStation, String arrivalStation, String time) {
            this.departureStation = departureStation;
            this.arrivalStation = arrivalStation;
            this.time = time;
        }

        @Override
        protected String doInBackground(String... strings) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class,
                    "checkmytrain.db")
                    .fallbackToDestructiveMigration()
                    .build();
            Journey journey = new Journey();
            journey.setDestination(arrivalStation);
            journey.setOrigin(departureStation);
            journey.setTime(time);
            JourneyDAO dao = db.dao();
            dao.insertAll(journey);
            jobId = dao.getLast().getId();
            return "pass";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

}
