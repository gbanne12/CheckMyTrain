package bannerga.com.checkmytrain.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import bannerga.com.checkmytrain.notification.JourneyStatus;

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

    public JourneyStatus getNextTrain(JSONArray timetable, String destination) throws Exception {
        JourneyStatus notification = new JourneyStatus();
        for (int i = 0; i < timetable.length(); i++) {   // for each journey in the timetable
            JSONObject route = (JSONObject) timetable.get(i);
            boolean isFinalDestination = route.get("destination").toString().contains(destination);
            if (isFinalDestination) {
                notification.setCancelled(route.getBoolean("isCancelled"));
                notification.setTime(route.get("std").toString());
                notification.setDelayed(route.get("etd").toString());
                break;
            } else {
                notification = getInfoForSubsequentCallingPoint(route, destination);
                if (notification.getTime() != null) {
                    break;
                }
            }
        }
        return notification;
    }

    private JourneyStatus getInfoForSubsequentCallingPoint(JSONObject route, String destination) throws JSONException {
        JourneyStatus notification = new JourneyStatus();
        // For each subsequent calling point on the journey
        JSONArray subsequentCallingPoints = route.getJSONArray("subsequentCallingPoints");
        for (int j = 0; j < subsequentCallingPoints.length(); j++) {

            JSONArray callingPoints = ((JSONObject) subsequentCallingPoints.get(j)).getJSONArray("callingPoint");

            for (int k = 0; k < callingPoints.length(); k++) {
                JSONObject callingPoint = ((JSONObject) callingPoints.get(k));
                System.out.println("Found station: " + callingPoint.get("locationName").toString());
                boolean isCallingPoint = callingPoint.get("locationName").toString().contains(destination);

                if (isCallingPoint) {
                    notification.setCancelled(callingPoint.getBoolean("isCancelled"));
                    notification.setTime(callingPoint.get("st").toString());
                    notification.setDelayed(callingPoint.get("et").toString());
                    return notification;
                }
            }
        }
        return notification;
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
