package bannerga.com.checkmytrain.notification


import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.Assert.assertTrue
import org.junit.Test


class NotificationJobTest {

    @Test
    fun scheduleJobWithHoursAndMinutes() {
        val job = MockNotificationJob(getInstrumentation().targetContext)
        job.setOffsetInMillis(1)
        job.scheduleJob("Exhibition Centre", "Glasgow Central", 0, 0)
        val id = job.jobId
        assertTrue(id > 0)    // job id is -1 if not properly set.
    }
}
