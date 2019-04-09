package bannerga.com.checkmytrain.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import bannerga.com.checkmytrain.R;
import bannerga.com.checkmytrain.controllers.ConfigurationController;

public class PendingJobsActivity extends AppCompatActivity {

    private TextView pendingJobText;

    private static void onFloatingButtonClick(View view) {
        Snackbar.make(view, "Replace", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_jobs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(PendingJobsActivity::onFloatingButtonClick);
        pendingJobText = findViewById(R.id.pending_jobs_info);
        //ConfigurationController controller = new ConfigurationController();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String text = sharedPref.getString("departure_station", "GB");
        Intent intent = getIntent();
        ConfigurationController controller = intent.getExtras().getParcelable("bannerga.com.checkmytrain.controllers.par");
        // String text = controller.getPendingJobs(this);
        pendingJobText.setText(text);
    }


}
