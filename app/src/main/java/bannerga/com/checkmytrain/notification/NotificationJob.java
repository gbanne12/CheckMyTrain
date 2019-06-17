package bannerga.com.checkmytrain.notification;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.widget.Toast;

import androidx.room.Room;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/***
 * Job that will schedule {@link NotificationService} to query the journey information
 */
public class NotificationJob {

    private Context context;
    private JobScheduler jobScheduler;

    public NotificationJob(Context context) {
        this.context = context;
    }

    /***
     * Schedule the notification using the number of milliseconds
     * until the journey information is to be queried
     * @return the id of the scheduled job
     */
    public void scheduleJob(String departureStation, String arrivalStation, int hour, int minute) {
        long offset = getOffsetInMillis(hour, minute);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("departureStation", departureStation);
        bundle.putString("arrivalStation", arrivalStation);
        bundle.putInt("hour", hour);
        bundle.putInt("minute", minute);

        String timestamp = new SimpleDateFormat("MMddHHmmss").format(new Date());
        int jobId = Integer.parseInt(timestamp);

        String serviceName = NotificationService.class.getName();
        ComponentName service = new ComponentName(context.getPackageName(), serviceName);

        JobInfo.Builder builder = new JobInfo.Builder(jobId, service);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setMinimumLatency(offset)
                .setExtras(bundle);
        JobInfo notificationJob = builder.build();
        jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(notificationJob);

        new SaveJourneyAsyncTask(jobId, departureStation, arrivalStation, hour, minute).execute();
    }

    public void cancelJob(int jobId) {
        if (jobScheduler != null) {
            jobScheduler.cancelAll();
            jobScheduler = null;
            Toast.makeText(context, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        } else {
            jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(jobId);
        }
    }

    public long getOffsetInMillis(int hourOfDay, int minute) {
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
        return offset;
    }

    private class SaveJourneyAsyncTask extends AsyncTask<String, Void, String> {

        private String departureStation;
        private String arrivalStation;
        private int hour;
        private int minute;
        private int jobId;
        private AppDatabase db;

        public SaveJourneyAsyncTask(
                int jobId, String departureStation, String arrivalStation, int hour, int minute) {
            this.departureStation = departureStation;
            this.arrivalStation = arrivalStation;
            this.hour = hour;
            this.minute = minute;
            this.jobId = jobId;
            db = Room.databaseBuilder(context, AppDatabase.class, "checkmytrain.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        @Override
        protected String doInBackground(String... strings) {
            Journey journey = new Journey();
            journey.setJobId(jobId);
            journey.setOrigin(departureStation);
            journey.setDestination(arrivalStation);
            journey.setHour(hour);
            journey.setMinute(minute);
            JourneyDAO dao = db.dao();
            dao.insertAll(journey);
            return "pass";
        }

        @Override
        protected void onPostExecute(String adapter) {

        }
    }
}
