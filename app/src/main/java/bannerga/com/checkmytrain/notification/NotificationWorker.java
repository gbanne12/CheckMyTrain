package bannerga.com.checkmytrain.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;

import bannerga.com.checkmytrain.json.Timetable;
import bannerga.com.checkmytrain.view.notification.TrainNotification;

public class NotificationWorker extends Worker {

    private Context context;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        JourneyStatus status = new JourneyStatus();
        try {
            Timetable timetable = new Timetable();
            JSONArray journeys = timetable.getAllJourneys(getInputData().getString("origin"));
            status = timetable.getNextJourney(journeys, getInputData().getString("destination"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        TrainNotification notification = new TrainNotification();
        notification.issueNotification(context, status);
        return Result.success();
    }
}
