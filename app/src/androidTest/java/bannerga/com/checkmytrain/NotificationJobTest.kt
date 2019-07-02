package bannerga.com.checkmytrain

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.Assert.assertTrue
import org.junit.Test


class NotificationJobTest {

    @Test
    fun scheduleJobWithHoursAndMinutes() {
        val job = MockNotificationJob(getInstrumentation().targetContext)
        job.scheduleJob("Muirend", "Glasgow Central", 0, 0)
        val id = job.jobId
        assertTrue(id > 0)
    }
}
