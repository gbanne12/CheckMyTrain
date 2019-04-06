package bannerga.com.checkmytrain.activities;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bannerga.com.checkmytrain.controllers.ConfigurationController;
import bannerga.com.checkmytrain.notification.TrainNotification;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("Notification Service", "Adding notification");
        Map trainInfo = new HashMap();
        try {
            ConfigurationController controller = new ConfigurationController();
           // JSONObject json = controller.getJSONResponse(intent.getStringExtra("departure_station_name"));
           // trainInfo = controller.getTrainInformation(json, intent.getStringExtra("arrival_station_name"));
            JSONObject json = controller.getJSONResponse("MUI");
            trainInfo = controller.getTrainInformation(json, "Glasgow Central");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Failed to get train route JSON", Toast.LENGTH_LONG).show();
        }
        TrainNotification notification = new TrainNotification();
        notification.issueNotification(this, trainInfo);
        return false;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        // false will reschedule the job if it  fails to finish
        return false;
    }
}
