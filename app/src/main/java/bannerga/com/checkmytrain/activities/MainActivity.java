package bannerga.com.checkmytrain.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.controllers.ConfigurationController;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText stationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_configuration);
        stationEditText = findViewById(R.id.station_input);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSubmitClick(View view) throws Exception {
        if (!stationEditText.getText().toString().equals("")) {
            new getTrainInfoTask().execute();
        } else {
            Toast.makeText(this, "No station name entered", Toast.LENGTH_SHORT).show();
        }
    }

    private class getTrainInfoTask extends AsyncTask<String, Void, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            Map trainInfo = new HashMap();
            try {
                ConfigurationController controller = new ConfigurationController();
                JSONObject json = controller.getJSONResponse(stationEditText.getText().toString());
                trainInfo = controller.getTrainInformation(json, "Glasgow Central");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return trainInfo;
        }

        @Override
        protected void onPostExecute(Map result) {
            Toast.makeText(MainActivity.this, result.get("time").toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
