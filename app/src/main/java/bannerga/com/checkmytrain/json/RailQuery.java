package bannerga.com.checkmytrain.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class RailQuery {

    public JSONArray getTimetableFor(String station) throws Exception {
        String huxleyAddress = "http://huxley.apphb.com/all/" + station +
                "?accessToken=3dfc0955-c0b0-4cb0-a8ca-9ddcf9d850cf&expand=true";
        URL url = new URL(huxleyAddress);
        URLConnection connection = url.openConnection();

        StringBuilder responseString = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = br.readLine();
        while (line != null) {
            responseString.append(line);
            line = br.readLine();
        }
        JSONObject json = new JSONObject(responseString.toString());
        return (JSONArray) json.get("trainServices");
    }

    public Map getNextDepartureFor(JSONArray journeys, String destination) throws Exception {
        Map map = new HashMap();
            for (int i = 0; i < journeys.length(); i++) {
                JSONObject currentJourney = ((JSONObject) journeys.get(i));
                boolean isDestination =
                        currentJourney.get("destination").toString().contains(destination);
                if (isDestination) {
                    map.put("cancelled", currentJourney.getBoolean("isCancelled"));
                    map.put("time", currentJourney.get("std").toString());
                    map.put("delayed", currentJourney.get("etd").toString());
                    break;
                }
            }
        return map;
    }

    public JSONArray getStations() throws IOException, JSONException {
        StringBuilder responseString = new StringBuilder();
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
