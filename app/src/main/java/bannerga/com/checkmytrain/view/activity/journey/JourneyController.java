package bannerga.com.checkmytrain.view.activity.journey;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import bannerga.com.checkmytrain.data.journey.SaveJourneyAsyncTask;
import bannerga.com.checkmytrain.data.model.Journey;
import bannerga.com.checkmytrain.notification.NotificationWorker;

public class JourneyController {

    String scheduleWork(Journey journey, Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

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

        Data.Builder data = new Data.Builder();
        data.putString("origin", journey.getOrigin());
        data.putString("destination", journey.getDestination());

        PeriodicWorkRequest notificationWorkRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS)
                        .setInputData(data.build())
                        .setConstraints(constraints)
                        .setInitialDelay(offset, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(context).enqueue(notificationWorkRequest);
        return notificationWorkRequest.getId().toString();
    }

    void saveJourney(Journey journey, Context context) {
        new SaveJourneyAsyncTask(journey, context).execute();
    }

}
