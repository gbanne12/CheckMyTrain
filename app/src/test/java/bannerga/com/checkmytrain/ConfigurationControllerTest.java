package bannerga.com.checkmytrain;


import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import bannerga.com.checkmytrain.controllers.ConfigurationController;

public class ConfigurationControllerTest {

    @Test
    public void canRetrieveTimeInformationFromDb() {

    }

    @Test
    public void canRetrieveStationInformationFromDb() {

    }

    @Test
    public void canSetUserConfiguration() {

    }

    @Test
    public void canGetNetworkRailResponse() throws Exception {
        ConfigurationController controller = new ConfigurationController();
        JSONObject json = controller.getJSONResponse("MUI");
        Assert.assertFalse(json.toString().isEmpty() || json.toString().equals(""));
    }

    @Test
    public void canParseTrainTime() throws Exception {
        ConfigurationController controller = new ConfigurationController();
        JSONObject json = controller.getJSONResponse("MUI");
        Map trainInfo = controller.getTrainInformation(json, "Glasgow Central");

        String time = trainInfo.get("time").toString();
        String twentyFourHourFormat = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Assert.assertTrue(time.matches(twentyFourHourFormat));
    }

    @Test
    public void canParseDelayedInformation() throws Exception {
        ConfigurationController controller = new ConfigurationController();
        JSONObject json = controller.getJSONResponse("MUI");
        Map trainInfo = controller.getTrainInformation(json, "Glasgow Central");

        String isDelayed = trainInfo.get("delayed").toString();
        Assert.assertTrue(isDelayed.equals("On time") || isDelayed.equals("Delayed"));
    }

    @Test
    public void canParseCancelledInformation() throws Exception {
        ConfigurationController controller = new ConfigurationController();
        JSONObject json = controller.getJSONResponse("MUI");
        Map trainInfo = controller.getTrainInformation(json, "Glasgow Central");

        String cancelled = trainInfo.get("cancelled").toString();
        Assert.assertTrue(cancelled.equals("true") || cancelled.equals("false"));
    }




}