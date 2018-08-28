package bannerga.com.checkmytrain.controllers;


import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationController {


    public JSONObject getJSONResponse() throws Exception {
        String huxleyAddress = "http://huxley.apphb.com/all/MUI" +
                "?accessToken=3dfc0955-c0b0-4cb0-a8ca-9ddcf9d850cf&expand=true";
        URL url = new URL(huxleyAddress);
        URLConnection connection = url.openConnection();

        StringBuilder responseString = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }

        return new JSONObject(responseString.toString());
    }

    public Map getTrainInformation(JSONObject json, String stationName) throws JSONException {
        Map map = new HashMap();
        if (json != null) {
            JSONArray jsonArray = (JSONArray) json.get("trainServices");

            for (int i = 0; i < jsonArray.length(); i++) {
                Boolean isDestination =  ((JSONObject) jsonArray.get(i)).get("destination").toString().contains(stationName);
                if (isDestination) {

                    map.put("cancelled", ((JSONObject) jsonArray.get(i)).getBoolean("isCancelled"));
                    map.put("time", ((JSONObject) jsonArray.get(i)).get("std").toString());
                    map.put("delayed",((JSONObject) jsonArray.get(i)).get("etd").toString());
                }
            }

        } else {
            System.out.println("JSON is null, aborting attempt to find time");
        }
        return map;

    }

}
