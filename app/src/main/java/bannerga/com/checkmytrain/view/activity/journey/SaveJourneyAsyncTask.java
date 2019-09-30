package bannerga.com.checkmytrain.view.activity.journey;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Journey;
import bannerga.com.checkmytrain.data.JourneyDAO;

public class SaveJourneyAsyncTask extends AsyncTask<String, Void, String> {
    private AppDatabase db;
    private Journey journey;

    public SaveJourneyAsyncTask(Journey journey, Context context) {
        this.journey = journey;
        db = Room.databaseBuilder(context, AppDatabase.class, "checkmytrain.db")
                .fallbackToDestructiveMigration()
                .build();
    }


    @Override
    protected String doInBackground(String... strings) {
        JourneyDAO dao = db.dao();
        dao.insertAll(journey);
        return "pass";
    }
}
