package tie.hackathon.travelguide;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.processbutton.FlatButton;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import Util.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Activity to add new trip
 */
public class AddNewTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String DATEPICKER_TAG1 = "datepicker1";
    private static final String DATEPICKER_TAG2 = "datepicker2";
    private AutoCompleteTextView cityname;
    private String startdate;
    private String enddate;
    private String tripname;
    private String miasto;
    private String opis;
    private FlatButton sdate;
    private FlatButton edate;
    private FlatButton ok;
    private EditText tname;
    private EditText description;
    private MaterialDialog dialog;
    private ZarzadcaBazy zarzadcaBazy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        zarzadcaBazy = new ZarzadcaBazy(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);
        cityname = (AutoCompleteTextView) findViewById(R.id.cityname);
        sdate = (FlatButton) findViewById(R.id.sdate);
        edate = (FlatButton) findViewById(R.id.edate);
        ok = (FlatButton) findViewById(R.id.ok);
        description = (EditText) findViewById(R.id.description);
        tname = (EditText) findViewById(R.id.tripname);

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog =
                DatePickerDialog.newInstance(this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        isVibrate());

        // Set Start date
        sdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(isVibrate());
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG1);
            }
        });

        // Set end date
        edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(isVibrate());
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG2);
            }
        });

        // Add a new trip
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripname = tname.getText().toString();
                miasto = cityname.getText().toString();
                opis = description.getText().toString();
                addTrip();
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if (Objects.equals(datePickerDialog.getTag(), DATEPICKER_TAG1)) {
            Calendar calendar = new GregorianCalendar(year, month, day);
            startdate = Long.toString(calendar.getTimeInMillis() / 1000);
        }
        if (Objects.equals(datePickerDialog.getTag(), DATEPICKER_TAG2)) {
            Calendar calendar = new GregorianCalendar(year, month, day);
            enddate = Long.toString(calendar.getTimeInMillis() / 1000);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
    }

    /**
     * Calls API to add  new trip
     */
    private void addTrip() {

        // Show a dialog box
        dialog = new MaterialDialog.Builder(AddNewTrip.this)
                .title(R.string.app_name)
                .content("Please wait...")
                .progress(true, 0)
                .show();

        zarzadcaBazy.dodajPodroz(tripname, miasto, startdate, enddate, opis);

                        Toast.makeText(AddNewTrip.this, "Trip added", Toast.LENGTH_LONG).show();
                        dialog.dismiss();

    }

    private boolean isVibrate() {
        return false;
    }

    private boolean isCloseOnSingleTapDay() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}
