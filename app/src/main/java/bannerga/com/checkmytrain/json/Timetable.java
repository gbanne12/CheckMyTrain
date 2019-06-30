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

public class Timetable {

    private final String BASE_URL = "http://huxley.apphb.com";
    private final String ACCESS_TOKEN = "?accessToken=3dfc0955-c0b0-4cb0-a8ca-9ddcf9d850cf&expand=true";
    private final String JOURNEYS_ENDPOINT = "/all/";
    private final String STATION_ENDPOINT = "/crs";

    public JSONArray getAllJourneys(String station) throws Exception {
        URL timetableEndpoint = new URL(BASE_URL + JOURNEYS_ENDPOINT + station + ACCESS_TOKEN);
        JSONObject json = new JSONObject(getResponseAsString(timetableEndpoint).toString());
        return (JSONArray) json.get("trainServices");
    }

    public JSONArray getStations() throws IOException, JSONException {
        URL stationsEndpoint = new URL(BASE_URL + STATION_ENDPOINT);
        return new JSONArray(getResponseAsString(stationsEndpoint).toString());
    }

    public Map getNextTrain(JSONArray timetable, String destination) throws Exception {
        Map map = new HashMap();
        for (int i = 0; i < timetable.length(); i++) {
            JSONObject currentJourney = (JSONObject) timetable.get(i);
            boolean isDestination =
                    currentJourney.get("destination").toString().contains(destination);
            if (isDestination) {
                map.put("cancelled", currentJourney.getBoolean("isCancelled"));
                map.put("time", currentJourney.get("std").toString());
                map.put("delayed", currentJourney.get("etd").toString());
                break;


            } else {
                System.out.println("Searching route " + i + " of " + timetable.length());
                JSONArray subsequentCallingPoints =
                        currentJourney.getJSONArray("subsequentCallingPoints");
                for (int count = 0; count < subsequentCallingPoints.length(); count++) {

                    JSONObject currentCallingPoint = (JSONObject) subsequentCallingPoints.get(count);
                    JSONArray callingPoints = currentCallingPoint.getJSONArray("callingPoint");


                    for (int iterator = 0; iterator < callingPoints.length(); iterator++) {
                        System.out.println("Searching calling point " + count + " of " + subsequentCallingPoints.length());
                        JSONObject callingPoint = ((JSONObject) callingPoints.get(iterator));
                        System.out.println("Found calling point: " + callingPoint.get("locationName").toString());
                        boolean isCallingPoint =
                                callingPoint.get("locationName").toString().contains(destination);

                        if (isCallingPoint) {
                            map.put("cancelled", callingPoint.getBoolean("isCancelled"));
                            map.put("time", callingPoint.get("st").toString());
                            map.put("delayed", callingPoint.get("et").toString());
                            return map;
                        }

                    }
                }
            }
        }
        return map;
    }

    private StringBuilder getResponseAsString(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        StringBuilder responseString = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = br.readLine();
        while (line != null) {
            responseString.append(line);
            line = br.readLine();
        }
        return responseString;
    }
}
