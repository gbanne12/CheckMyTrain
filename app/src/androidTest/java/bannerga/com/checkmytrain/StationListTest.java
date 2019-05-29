package bannerga.com.checkmytrain;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import bannerga.com.checkmytrain.data.AppDatabase;
import bannerga.com.checkmytrain.data.Station;
import bannerga.com.checkmytrain.data.StationDAO;

public class StationListTest {

    private static StationDAO dao;

    @Before
    public void setup() throws IOException, JSONException {
        AppDatabase db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).build();
        dao = db.stationDao();

        JSONArray stationArray = getStationJson();
        for (int i = 0; i < stationArray.length(); i++) {
            JSONObject stationDetails = (JSONObject) stationArray.get(i);
            String name = stationDetails.get("stationName").toString();
            String crs = stationDetails.get("crsCode").toString();
            Station station = new Station();
            station.setName(name);
            station.setCode(crs);
            dao.insertAll(station);
        }
    }

    @Test
    public void canGetTheCrsCodeForAStation() throws IOException, JSONException {
        Station station = dao.findByName("Muirend");
        Assert.assertEquals("MUI", station.getCode());
    }


    private static JSONArray getStationJson() throws IOException, JSONException {
        StringBuilder responseString = new StringBuilder();
        //File json = new File("src/test/res/example.json");
        //FileInputStream is = new FileInputStream(json);
        String huxleyAddress = "http://huxley.apphb.com/crs";
        URL url = new URL(huxleyAddress);
        URLConnection connection = url.openConnection();

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = br.readLine();
        while (line != null) {
            responseString.append(line);
            line = br.readLine();
        }
        return new JSONArray(responseString.toString());
    }
}
