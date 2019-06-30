package bannerga.com.checkmytrain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import bannerga.com.checkmytrain.json.Timetable;

public class TimetableTest {
    private static JSONArray dummyJson;

    @Test
    public void getHuxleyResponseTest() throws Exception {
        Timetable timetable = new Timetable();
        JSONArray response = timetable.getAllJourneys("MUI");
        Assert.assertNotNull(response);
    }

    @Test
    public void canGetNextDepartureTime() throws Exception {
        dummyJson = getDummyJsonArray("on-time.json");
        Map info = new Timetable().getNextTrain(dummyJson, "Glasgow Central");
        Assert.assertEquals("15:11", info.get("time").toString());
    }

    @Test
    public void canConfirmTrainIsOnTime() throws Exception {
        dummyJson = getDummyJsonArray("on-time.json");
        Map journeyDetails = new Timetable().getNextTrain(dummyJson, "Glasgow Central");
        Assert.assertEquals("On time", journeyDetails.get("delayed").toString());
    }

    @Test
    public void canGetTimeOfDelayedTrain() throws Exception {
        dummyJson = getDummyJsonArray("delayed.json");
        Map journeyDetails = new Timetable().getNextTrain(dummyJson, "Glasgow Central");
        Assert.assertEquals("08:35", journeyDetails.get("delayed").toString());
    }

    @Test
    public void canConfirmTrainIsCancelled() throws Exception {
        dummyJson = getDummyJsonArray("on-time.json");
        Map info = new Timetable().getNextTrain(dummyJson, "Glasgow Central");
        String cancelled = info.get("cancelled").toString();
        Assert.assertTrue(cancelled.equals("true") || cancelled.equals("false"));
    }

    @Test
    public void canConfirmTrainIsOnTimeALLEndpoint() throws Exception {
        dummyJson = getDummyJsonArray("all-endpoint.json");
        Map journeyDetails = new Timetable().getNextTrain(dummyJson, "Cathcart");
        Assert.assertEquals("On time", journeyDetails.get("delayed").toString());
        Assert.assertEquals("false", journeyDetails.get("cancelled").toString());
        Assert.assertEquals("13:36", journeyDetails.get("time").toString());
    }

    private static JSONArray getDummyJsonArray(String filename) throws IOException, JSONException {
        StringBuilder responseString = new StringBuilder();
        File json = new File("src/test/res/" + filename);
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
}
