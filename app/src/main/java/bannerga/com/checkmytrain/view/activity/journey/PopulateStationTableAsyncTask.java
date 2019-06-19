package bannerga.com.checkmytrain.view.activity.journey;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Station;
import bannerga.com.checkmytrain.data.StationDAO;
import bannerga.com.checkmytrain.json.Timetable;

public class PopulateStationTableAsyncTask extends AsyncTask<String, Void, Boolean> {

    private WeakReference<Context> contextRef;

    public PopulateStationTableAsyncTask(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        Context context = contextRef.get();
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "checkmytrain.db")
                .fallbackToDestructiveMigration()
                .build();
        StationDAO stationDAO = db.stationDao();

        try {
            Timetable query = new Timetable();
            JSONArray stationArray = query.getStations();
            if (stationDAO.getAll().size() > 1) {
                stationDAO.wipeTable();
            }
            for (int i = 0; i < stationArray.length(); i++) {
                JSONObject stationDetails = (JSONObject) stationArray.get(i);
                String name = stationDetails.get("stationName").toString();
                String crs = stationDetails.get("crsCode").toString();
                Station station = new Station();
                station.setName(name);
                station.setCrs(crs);
                stationDAO.insertAll(station);
            }
        } catch (IOException | JSONException e) {
            Log.i("Database", "Error writing the stations to the database");
            e.printStackTrace();
        }
        Log.i("Database", "Finished writing all stations to the database");
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {

    }
}
