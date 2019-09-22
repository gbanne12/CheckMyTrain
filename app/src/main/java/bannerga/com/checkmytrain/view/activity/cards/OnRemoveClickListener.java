package bannerga.com.checkmytrain.view.activity.cards;

import android.app.job.JobScheduler;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.room.Room;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class OnRemoveClickListener implements View.OnClickListener {

    private Context context;
    private Journey journey;
    private View card;
    private final String CLASS_NAME = getClass().getSimpleName();

    public OnRemoveClickListener(Context context, Journey journey, View card) {
        this.context = context;
        this.journey = journey;
        this.card = card;
    }

    @Override
    public void onClick(View view) {
        new RemoveJourneyAsyncTask().execute();
        card.setVisibility(View.GONE);
    }

    public class RemoveJourneyAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            int jobId = journey.getId();

            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "checkmytrain.db")
                    .fallbackToDestructiveMigration()
                    .build();
            JourneyDAO dao = db.dao();
            dao.delete(journey);
            Log.i(CLASS_NAME, "Job " + jobId + " removed from " + " database");

            JobScheduler jobScheduler =
                    (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(jobId);
            Log.i(CLASS_NAME, "Job " + jobId + "removed from " + "scheduler");
            return true;
        }
    }
}
