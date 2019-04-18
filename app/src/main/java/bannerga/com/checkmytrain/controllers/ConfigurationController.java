package bannerga.com.checkmytrain.controllers;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.widget.Toast;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import bannerga.com.checkmytrain.activities.NotificationJobService;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class ConfigurationController {

    private JobScheduler mScheduler;
    private static final int JOB_ID = 0;

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
    }

    public void scheduleJob(Context context, String departureStation, String arrivalStation, long offset) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("departureStation", departureStation);
        bundle.putString("arrivalStation", arrivalStation);

        mScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName service = new ComponentName(context.getPackageName(), NotificationJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, service);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setMinimumLatency(offset)
                .setExtras(bundle);
        JobInfo myJobInfo = builder.build();
        mScheduler.schedule(myJobInfo);
    }

    public void cancelJob(Context context) {
        if (mScheduler != null) {
            mScheduler.cancelAll();
            mScheduler = null;
            Toast.makeText(context, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }
    
}
