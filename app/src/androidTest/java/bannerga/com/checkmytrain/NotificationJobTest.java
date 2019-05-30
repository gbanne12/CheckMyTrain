package bannerga.com.checkmytrain;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import bannerga.com.checkmytrain.notification.NotificationJob;

public class NotificationJobTest {

    @Test
    public void scheduleJobWithHoursAndMinutes() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        NotificationJob job = new NotificationJob();
        long offset = job.getOffsetInMillis(9, 39);
        int id = job.scheduleJob(context, "Muirend", "Glasgow Central", offset);
        Assert.assertTrue(id > 0);
    }
}
