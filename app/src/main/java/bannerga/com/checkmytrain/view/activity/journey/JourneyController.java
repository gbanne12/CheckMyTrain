package bannerga.com.checkmytrain.view.activity.journey;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.notification.NotificationService;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class JourneyController {

    public void scheduleJourney(Journey journey, Context context) {
        String timestamp = new SimpleDateFormat("MMddHHmmss").format(new Date());
        int jobId = Integer.parseInt(timestamp);
        journey.setId(jobId);
        Log.i(getClass().getSimpleName(), "Job id set as: " + jobId);

        ZonedDateTime now = ZonedDateTime.now();
        long nowInMillis = now.toInstant().toEpochMilli();
        ZonedDateTime notificationTime = ZonedDateTime.now().with(
                LocalTime.of(journey.getHour(), journey.getMinute()));
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

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("departureStation", journey.getOrigin());
        bundle.putString("arrivalStation", journey.getDestination());
        bundle.putInt("hour", journey.getHour());
        bundle.putInt("minute", journey.getMinute());
        bundle.putInt("jobid", journey.getJobId());

        String serviceClass = NotificationService.class.getName();
        ComponentName jobService = new ComponentName(context.getPackageName(), serviceClass);

        JobInfo.Builder builder = new JobInfo.Builder(jobId, jobService);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setMinimumLatency(offset)
                .setExtras(bundle);
        JobInfo notificationJob = builder.build();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(notificationJob);
        Log.i(getClass().getSimpleName(), "Scheduled job with id: " + jobId);
    }

    public void saveJourney(Journey journey, Context context) {
        new SaveJourneyAsyncTask(journey, context).execute();
    }

}
