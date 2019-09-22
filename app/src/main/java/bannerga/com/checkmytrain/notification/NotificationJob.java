package bannerga.com.checkmytrain.notification;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.room.Room;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;
import bannerga.com.checkmytrain.view.activity.journey.FindJourneyAsyncTask;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/***
 * Job that will schedule {@link NotificationService} to query the journey information
 */
public class NotificationJob implements FindJourneyAsyncTask.AsyncResponse {

    private Context context;
    private JobScheduler jobScheduler;
    private int jobId = -1;

    public NotificationJob(Context context) {
        this.context = context;
    }

    /***
     * Schedule the notification using the number of milliseconds
     * until the journey information is to be queried
     */
    public void scheduleJob(Journey journey) {
        new FindJourneyAsyncTask(journey, context, this).execute();
    }

    public void saveJob(Journey journey) {
        new SaveJourneyAsyncTask(journey).execute();
    }

    public int getJobId() {
        return jobId;
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

    @Override
    public void processFinish(Journey journey) {
        jobId = journey.getId();
        Log.i(getClass().getSimpleName(), "Job id set as: " + jobId);

        long offset = getOffsetInMillis(journey.getHour(), journey.getMinute());
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("departureStation", journey.getOrigin());
        bundle.putString("arrivalStation", journey.getDestination());
        bundle.putInt("hour", journey.getHour());
        bundle.putInt("minute", journey.getMinute());

        String serviceClass = NotificationService.class.getName();
        ComponentName jobService = new ComponentName(context.getPackageName(), serviceClass);

        JobInfo.Builder builder = new JobInfo.Builder(jobId, jobService);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setMinimumLatency(offset)
                .setExtras(bundle);
        JobInfo notificationJob = builder.build();
        jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(notificationJob);
        Log.i(getClass().getSimpleName(), "Scheduled job with id: " + jobId);
    }

    private class SaveJourneyAsyncTask extends AsyncTask<String, Void, String> {
        private AppDatabase db;
        private Journey journey;

        public SaveJourneyAsyncTask(Journey journey) {
            this.journey = journey;
            db = Room.databaseBuilder(context, AppDatabase.class, "checkmytrain.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        @Override
        protected String doInBackground(String... strings) {
            JourneyDAO dao = db.dao();
            dao.insertAll(journey);
            return "pass";
        }
    }
}
