package bannerga.com.checkmytrain.view.activity.journey;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import java.lang.ref.WeakReference;
import java.util.List;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;

public class FindJourneyAsyncTask extends AsyncTask<String, Void, Journey> {

    private Journey journey;
    private WeakReference<Context> contextRef;

    public interface AsyncResponse {
        void processFinish(Journey journey);
    }

    public AsyncResponse delegate = null;

    public FindJourneyAsyncTask(Journey journey, Context context, AsyncResponse delegate) {
        this.journey = journey;
        this.delegate = delegate;
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Journey doInBackground(String... strings) {
        Context context = contextRef.get();
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "checkmytrain.db")
                .fallbackToDestructiveMigration()
                .build();

        JourneyDAO journeyDao = db.dao();
        List<Journey> savedJourney = journeyDao.getJourney(journey.getOrigin(),
                journey.getDestination(),
                journey.getHour(), journey.getMinute());
        return savedJourney.get(0);
    }

    @Override
    protected void onPostExecute(Journey journey) {
        delegate.processFinish(journey);
    }
}
