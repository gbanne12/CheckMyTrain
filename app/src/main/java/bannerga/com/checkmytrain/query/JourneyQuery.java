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

public class JourneyQuery {

    private String originStation;
    private String destinationStation;
    private String time;
    private JSONObject json;

    public JourneyQuery(String originStation, String destinationStation) {
        this.originStation = originStation;
        this.destinationStation = destinationStation;
    }

    public void getJSONResponse() throws Exception {
        String huxleyAddress = "http://huxley.apphb.com/all/" + originStation +
                "?accessToken=3dfc0955-c0b0-4cb0-a8ca-9ddcf9d850cf&expand=true";
        URL url = new URL(huxleyAddress);
        URLConnection connection = url.openConnection();

        StringBuilder responseString = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }


        json = new JSONObject(responseString.toString());
    }

    public Map getNextJourney() throws JSONException {
        Map journeyInfo = new HashMap();
        try {
            JSONArray journeys = (JSONArray) json.get("trainServices");

            for (int i = 0; i < journeys.length(); i++) {
                JSONObject currentJourney = ((JSONObject) journeys.get(i));
                Boolean hasCorrectDestination = currentJourney.get("destination").toString().contains(destinationStation);
                if (hasCorrectDestination) {
                    journeyInfo.put("cancelled", currentJourney.getBoolean("isCancelled"));
                    journeyInfo.put("time", currentJourney.get("std").toString());
                    journeyInfo.put("delayed", currentJourney.get("etd").toString());
                    break;
                }
            }
        } catch (JSONException e) {
            System.out.println(e.getStackTrace());
        }
        return journeyInfo;

    }


}
