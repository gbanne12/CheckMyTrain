package bannerga.com.checkmytrain

import android.content.Context

import bannerga.com.checkmytrain.notification.NotificationJob

class MockNotificationJob(context: Context) : NotificationJob(context) {

    override fun getOffsetInMillis(hourOfDay: Int, minute: Int): Long {
        return 1
    }

}
