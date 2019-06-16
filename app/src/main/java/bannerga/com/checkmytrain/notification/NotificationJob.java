package bannerga.com.checkmytrain.notification;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.widget.Toast;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/***
 * Job that will schedule {@link NotificationService} to query the journey information
 */
public class NotificationJob {

    private JobScheduler jobScheduler;

    /***
     * Schedule the notification using the number of milliseconds
     * until the journey information is to be queried
     *
     * @param context the launching activity or service
     * @param departureStation  the origin station for the journey
     * @param arrivalStation  the destination station for the journey
     * @param offset the number of milliseconds to wait before attempting to the notification job
     * @return the id of the scheduled job
     */
    public int scheduleJob(Context context, String jobId, String departureStation, String arrivalStation, long offset) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("departureStation", departureStation);
        bundle.putString("arrivalStation", arrivalStation);

        jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName notificationService = new ComponentName(context.getPackageName(), NotificationService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(Integer.parseInt(jobId), notificationService);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setMinimumLatency(offset)
                .setExtras(bundle);
        JobInfo notificationJob = builder.build();
        jobScheduler.schedule(notificationJob);
        return notificationJob.getId();
    }

    public void cancelJob(Context context, String jobId) {
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
}
