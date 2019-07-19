package bannerga.com.checkmytrain

import android.content.Context

import bannerga.com.checkmytrain.notification.NotificationJob

class MockNotificationJob(context: Context) : NotificationJob(context) {

    private var offset: Long = 1

    override fun getOffsetInMillis(hourOfDay: Int, minute: Int): Long {
        return offset
    }

    fun setOffsetInMillis(offset: Long) {
        this.offset = offset
    }

}
