package bannerga.com.checkmytrain;


import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import bannerga.com.checkmytrain.controllers.ConfigurationController;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ConfigurationControllerTest {

    @Test
    public void canGetTimeInformation() {

    }

    @Test
    public void canGetStationInformation() {

    }

    @Test
    public void canGetDeparture() {

    }

    @Test
    public void canSetUserConfiguration() {

    }

    @Test
    public void canGetNetworkRailResponse() throws Exception {
        ConfigurationController controller = new ConfigurationController();
        JSONObject json = controller.getJSONResponse();
        Assert.assertFalse(json.toString().isEmpty() || json.toString().equals(""));
    }

    @Test
    public void canParseTrainTime() throws Exception {
        ConfigurationController controller = new ConfigurationController();
        JSONObject json=  controller.getJSONResponse();
        Map trainInfo = controller.getTrainInformation(json, "Glasgow Central");
        System.out.println(trainInfo.toString());
    }


}