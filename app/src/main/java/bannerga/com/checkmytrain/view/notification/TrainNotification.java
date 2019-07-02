package bannerga.com.checkmytrain.view.notification;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Random;
import java.util.UUID;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.notification.JourneyStatus;
import bannerga.com.checkmytrain.view.activity.journey.JourneyActivity;

public class TrainNotification {

    public void issueNotification(Context context, JourneyStatus journeyStatus) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String id = UUID.randomUUID().toString();
        Notification.Builder builder = new Notification.Builder(context, id)
                .setContentIntent(getPendingIntent(context))
                .setContentTitle("Status: " + journeyStatus.getDelayed())
                .setContentText("Departure time: " + journeyStatus.getTime())
                .setSmallIcon(R.drawable.ic_notification_icon)
               // .setColor(content.getColor())
                .setAutoCancel(true)
                .setOngoing(false);
        setAndroidOBehaviour(notificationManager, builder);
        Notification notification = builder.build();

        notificationManager.notify(getRandomDigit(), notification);
    }

    private void setAndroidOBehaviour(NotificationManager manager, Notification.Builder builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_02";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder.setColorized(true);
            builder.setChannelId(CHANNEL_ID);
        }
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent notificationIntent = new Intent(context, JourneyActivity.class);
        return PendingIntent.getActivity(context,
                getRandomDigit(),
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);
    }

    private int getRandomDigit() {
        Random rand = new Random();
        return Integer.parseInt(Integer.toString(rand.nextInt(9) + 1)
                + Integer.toString(rand.nextInt(9) + 1)
                + Integer.toString(rand.nextInt(9) + 1));
    }
}
