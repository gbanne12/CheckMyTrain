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

import bannerga.com.checkmytrain.json.Timetable;
import bannerga.com.checkmytrain.notification.JourneyStatus;

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
        JourneyStatus info = new Timetable().getNextTrain(dummyJson, "Glasgow Central");
        Assert.assertEquals("15:11", info.getTime());
    }

    @Test
    public void canConfirmTrainIsOnTime() throws Exception {
        dummyJson = getDummyJsonArray("on-time.json");
        JourneyStatus journeyDetails = new Timetable().getNextTrain(dummyJson, "Glasgow Central");
        Assert.assertEquals("On time", journeyDetails.getDelayed());
    }

    @Test
    public void canGetTimeOfDelayedTrain() throws Exception {
        dummyJson = getDummyJsonArray("delayed.json");
        JourneyStatus journeyDetails = new Timetable().getNextTrain(dummyJson, "Glasgow Central");
        Assert.assertEquals("08:35", journeyDetails.getDelayed());
    }

    @Test
    public void canConfirmTrainIsCancelled() throws Exception {
        dummyJson = getDummyJsonArray("on-time.json");
        JourneyStatus info = new Timetable().getNextTrain(dummyJson, "Glasgow Central");
        Boolean cancelled = info.getCancelled();
        Assert.assertFalse(cancelled);
    }

    @Test
    public void canConfirmTrainIsOnTimeALLEndpoint() throws Exception {
        dummyJson = getDummyJsonArray("all-endpoint.json");
        JourneyStatus journeyDetails = new Timetable().getNextTrain(dummyJson, "Williamwood");
        Assert.assertEquals("On time", journeyDetails.getDelayed());
        Assert.assertFalse(journeyDetails.getCancelled());
        Assert.assertEquals("13:41", journeyDetails.getTime());
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
