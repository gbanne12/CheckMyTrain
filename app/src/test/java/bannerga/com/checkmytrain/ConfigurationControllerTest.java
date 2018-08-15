package bannerga.com.checkmytrain;


import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


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
        //String station = "MUI";
        URL huxley = new URL("http://huxley.apphb.com/all/MUI" +
                "?accessToken=3dfc0955-c0b0-4cb0-a8ca-9ddcf9d850cf&expand=true");
        URLConnection connection = huxley.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String inputLine = "";
        String json = "";
        while ((inputLine = in.readLine()) != null)
            json = json + inputLine;
        in.close();

        JSONObject jsonObj = new JSONObject(json);
    }

    @Test
    public void canParseTrainTime() {

    }


}