package bannerga.com.checkmytrain.notification;

import android.content.Context;
import android.util.Log;

import androidx.work.Configuration;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.TestDriver;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class NotificationWorkerTest {

    private Context context;

    @Before
    public void setup() {
        context = getInstrumentation().getTargetContext();
        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                // Use a SynchronousExecutor to make it easier to write tests
                .setExecutor(new SynchronousExecutor())
                .build();

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config);
    }

    @Test
    public void testWorkIsReQueued() throws ExecutionException, InterruptedException {
        Data input = new Data.Builder()
                .putString("origin", "Kings Park")
                .putString("destination", "Mount Florida")
                .build();

        // Create request
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                NotificationWorker.class, 24, TimeUnit.HOURS)
                .setInputData(input)
                .setInitialDelay(1, TimeUnit.MILLISECONDS)
                .build();

        // Enqueue and wait for result.
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueue(request).getResult().get();
        TestDriver testDriver = WorkManagerTestInitHelper.getTestDriver(context);
        testDriver.setInitialDelayMet(request.getId());


        WorkInfo workInfo = workManager.getWorkInfoById(request.getId()).get();
        Assert.assertThat(workInfo.getState(), Matchers.is(WorkInfo.State.ENQUEUED));
    }


}
