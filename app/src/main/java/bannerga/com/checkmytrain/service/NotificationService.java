package bannerga.com.checkmytrain.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bannerga.com.checkmytrain.controllers.ConfigurationController;
import bannerga.com.checkmytrain.notification.TrainNotification;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("Notification Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Notification Service", "Adding notification");
        Map trainInfo = new HashMap();
        try {
            ConfigurationController controller = new ConfigurationController();
            JSONObject json = controller.getJSONResponse(intent.getStringExtra("departure_station_name"));
            trainInfo = controller.getTrainInformation(json, intent.getStringExtra("arrival_station_name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
       // return trainInfo;
        TrainNotification notification = new TrainNotification();
        notification.issueNotification(this, trainInfo);
    }
}

