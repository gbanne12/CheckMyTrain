package bannerga.com.checkmytrain.view.autocompletetextview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;

import bannerga.com.checkmytrain.data.station.FindStationAsyncTask;

public class StationTextWatcher implements TextWatcher {

    private AutoCompleteTextView textView;
    private Context context;

    public StationTextWatcher(AutoCompleteTextView textView, Context context) {
        this.textView = textView;
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = s.toString();
        new FindStationAsyncTask(input, textView, context).execute();
    }
}
