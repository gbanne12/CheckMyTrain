package bannerga.com.checkmytrain.view.activities;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Station;
import bannerga.com.checkmytrain.data.StationDAO;
import bannerga.com.checkmytrain.json.RailQuery;

public class PopulateStationTableAsyncTask extends AsyncTask<String, Void, Boolean> {

    private WeakReference<Context> contextRef;

    public PopulateStationTableAsyncTask(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        Context context = contextRef.get();
        AppDatabase db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        StationDAO stationDAO = db.stationDao();

        try {
            RailQuery query = new RailQuery();
            JSONArray stationArray = query.getStations();
            for (int i = 0; i < stationArray.length(); i++) {
                JSONObject stationDetails = (JSONObject) stationArray.get(i);
                String name = stationDetails.get("stationName").toString();
                String crs = stationDetails.get("crsCode").toString();
                Station station = new Station();
                station.setName(name);
                station.setCrs(crs);
                if (stationDAO.getAll().size() > 0) ;
                {
                    stationDAO.wipeTable();
                }
                stationDAO.insertAll(station);
            }

        } catch (IOException | JSONException e) {

        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {

    }
}
