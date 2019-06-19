package bannerga.com.checkmytrain.view.activity.journey;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.room.Room;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Station;
import bannerga.com.checkmytrain.data.StationDAO;

public class FindStationAsyncTask extends AsyncTask<String, Void, ArrayAdapter<String>> {

    private String input;
    private WeakReference<Context> contextRef;
    private WeakReference<AutoCompleteTextView> departureStationRef;


    public FindStationAsyncTask(String input, AutoCompleteTextView departureStationTextView, Context context) {
        this.input = input;
        departureStationRef = new WeakReference<>(departureStationTextView);
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected ArrayAdapter<String> doInBackground(String... strings) {
        Context context = contextRef.get();
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "checkmytrain.db")
                .fallbackToDestructiveMigration()
                .build();

        StationDAO stationDAO = db.stationDao();
        List<Station> stations = stationDAO.findByPartialName("%" + input + "%");
        List<String> names = new ArrayList<>();
        for (Station station : stations) {
            String name = station.getName();
            names.add(name);
        }
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, names);
    }

    @Override
    protected void onPostExecute(ArrayAdapter<String> adapter) {
        AutoCompleteTextView departureStationTextView = departureStationRef.get();
        departureStationTextView.setAdapter(adapter);
    }
}
