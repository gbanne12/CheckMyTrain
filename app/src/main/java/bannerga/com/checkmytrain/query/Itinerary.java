package bannerga.com.checkmytrain.query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class Itinerary {

    public JSONArray get(String origin) throws Exception {
        String huxleyAddress = "http://huxley.apphb.com/all/" + origin +
                "?accessToken=3dfc0955-c0b0-4cb0-a8ca-9ddcf9d850cf&expand=true";
        URL url = new URL(huxleyAddress);
        URLConnection connection = url.openConnection();

        StringBuilder responseString = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = br.readLine()) != null) {
            responseString.append(line);
        }

        JSONObject json = new JSONObject(responseString.toString());
        return (JSONArray) json.get("trainServices");
    }

    public Map getNext(String originStation, String destinationStation) throws Exception {
        JSONArray journeys = get(originStation);
        Map nextJourney = new HashMap();

            for (int i = 0; i < journeys.length(); i++) {
                JSONObject currentJourney = ((JSONObject) journeys.get(i));
                Boolean hasCorrectDestination =
                        currentJourney.get("destination").toString().contains(destinationStation);
                if (hasCorrectDestination) {
                    nextJourney.put("cancelled", currentJourney.getBoolean("isCancelled"));
                    nextJourney.put("time", currentJourney.get("std").toString());
                    nextJourney.put("delayed", currentJourney.get("etd").toString());
                    break;
                }
            }
        return nextJourney;
    }


}
