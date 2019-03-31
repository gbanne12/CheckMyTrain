package bannerga.com.checkmytrain.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("Notification Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("MyTestService", "Service running");
    }
}

