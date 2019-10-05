package bannerga.com.checkmytrain.data.journey;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import bannerga.com.checkmytrain.data.model.AppDatabase;
import bannerga.com.checkmytrain.data.model.Journey;
import bannerga.com.checkmytrain.data.model.JourneyDAO;

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
