package bannerga.com.checkmytrain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import bannerga.com.checkmytrain.query.Itinerary;

public class ItineraryTest {

    private static JSONArray dummyJson;

    @BeforeClass
    public static void getDummyJSON() throws IOException, JSONException {
        dummyJson = getDummyJsonArray();
    }

    private static JSONArray getDummyJsonArray() throws IOException, JSONException {
        StringBuilder responseString = new StringBuilder();
        File json = new File("src/test/res/example.json");
        FileInputStream is = new FileInputStream(json);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = br.readLine();
        while (line != null) {
            responseString.append(line);
            line = br.readLine();
        }
        JSONObject jsonOb = new JSONObject(responseString.toString());
        return (JSONArray) jsonOb.get("trainServices");
    }

    @Test
    public void getHuxleyResponseTest() throws Exception {
        Itinerary itinerary = new Itinerary();
        JSONArray response = itinerary.getTimetableFor("MUI");
        Assert.assertNotNull(response);
    }

    @Test
    public void getParseNextTrainTime() throws Exception {
        Itinerary itinerary = new Itinerary();
        Map info = itinerary.getNextDepartureFor(dummyJson, "Glasgow Central");
        Assert.assertEquals("15:11", info.get("time").toString());
    }

    @Test
    public void canParseDelayedInformation() throws Exception {
        Itinerary itinerary = new Itinerary();
        Map info = itinerary.getNextDepartureFor(dummyJson, "Glasgow Central");
        String isDelayed = info.get("delayed").toString();
        Assert.assertTrue(isDelayed.equals("On time") || isDelayed.equals("Delayed"));
    }

    @Test
    public void canParseCancelledInformation() throws Exception {
        Itinerary itinerary = new Itinerary();
        Map info = itinerary.getNextDepartureFor(dummyJson, "Glasgow Central");
        String cancelled = info.get("cancelled").toString();
        Assert.assertTrue(cancelled.equals("true") || cancelled.equals("false"));
    }
}
