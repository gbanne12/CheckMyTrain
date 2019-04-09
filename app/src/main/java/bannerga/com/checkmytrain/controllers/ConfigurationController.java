package bannerga.com.checkmytrain.controllers;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bannerga.com.checkmytrain.service.NotificationJobService;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class ConfigurationController {

    private static final int JOB_ID = 0;
    private JobScheduler scheduler;

    public void scheduleJob(Context context, String departureStation, String arrivalStation, int hourOfDay, int minute) {
        ZonedDateTime now = ZonedDateTime.now();
        long nowInMillis = now.toInstant().toEpochMilli();
        ZonedDateTime notificationTime = ZonedDateTime.now().with(LocalTime.of(hourOfDay, minute));
        long notificationTimeInMillis = notificationTime.toInstant().toEpochMilli();

        long offset;
        boolean isRequestAheadOfCurrentTime = notificationTimeInMillis > nowInMillis;
        if (isRequestAheadOfCurrentTime) {
            offset = notificationTimeInMillis - nowInMillis;
        } else {
            // Time has passed for today, set the job for tomorrow
            ZonedDateTime tomorrow = notificationTime.plusDays(1);
            long tomorrowInMillis = tomorrow.toInstant().toEpochMilli();
            offset = tomorrowInMillis - nowInMillis;
        }
        scheduleJob(context, departureStation, arrivalStation, offset);
    }

    public void scheduleJob(Context context, String departureStation, String arrivalStation, long offset) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("departureStation", departureStation);
        bundle.putString("arrivalStation", arrivalStation);

        scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName service = new ComponentName(context.getPackageName(), NotificationJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, service);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setMinimumLatency(offset)
                .setExtras(bundle);
        JobInfo myJobInfo = builder.build();
        scheduler.schedule(myJobInfo);
    }

    public void cancelJob(Context context) {
        if (scheduler != null) {
            scheduler.cancelAll();
            scheduler = null;
            Toast.makeText(context, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void getPendingJobs(Context context) {
        if (scheduler != null) {
            List<JobInfo> jobs = scheduler.getAllPendingJobs();
            for (JobInfo job : jobs) {
                Toast.makeText(context, "Pending Jobs: " + job.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONObject getJSONResponse(String originStation) throws Exception {
        String huxleyAddress = "http://huxley.apphb.com/all/" + originStation +
                "?accessToken=3dfc0955-c0b0-4cb0-a8ca-9ddcf9d850cf&expand=true";
        URL url = new URL(huxleyAddress);
        URLConnection connection = url.openConnection();

        StringBuilder responseString = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }

        return new JSONObject(responseString.toString());
    }

    public Map getTrainInformation(JSONObject json, String stationName) throws JSONException {
        Map map = new HashMap();
        if (json != null) {
            JSONArray jsonArray = (JSONArray) json.get("trainServices");
            for (int i = 0; i < jsonArray.length(); i++) {
                Boolean isDestination = ((JSONObject) jsonArray.get(i)).get("destination").toString().contains(stationName);
                if (isDestination) {
                    map.put("cancelled", ((JSONObject) jsonArray.get(i)).getBoolean("isCancelled"));
                    map.put("time", ((JSONObject) jsonArray.get(i)).get("std").toString());
                    map.put("delayed", ((JSONObject) jsonArray.get(i)).get("etd").toString());
                    break;
                }
            }
        } else {
            System.out.println("JSON is null, aborting attempt to find time");
        }
        return map;

    }
}
