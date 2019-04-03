package bannerga.com.checkmytrain.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "bannerga.com.checkmytrain.service";

    // Triggered by the AlarmManager periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, NotificationService.class);
        i.putExtra("departure_station_name", intent.getStringExtra("departure_station_name"));
        i.putExtra("arrival_station_name", intent.getStringExtra("arrival_station_name"));
        context.startService(i);
    }
}
