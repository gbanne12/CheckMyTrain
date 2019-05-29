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
        NotificationJob job = new NotificationJob(context);
        int id = job.schedule(context, "Muirend", "Glasgow Central", 1000);
        Assert.assertTrue(id > 0);
    }
}
