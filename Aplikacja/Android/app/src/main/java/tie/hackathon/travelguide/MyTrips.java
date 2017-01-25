package tie.hackathon.travelguide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Util.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import tie.hackathon.travelguide.tables.PodrozDB;

public class MyTrips extends AppCompatActivity {

    private GridView g;
    private MaterialDialog dialog;
    private List<String> id;
    private List<String> name;
    private List<String> image;
    private List<String> start;
    private List<String> end;
    private List<String> tname;
    private String userid;
    private SharedPreferences s;
    private Handler mHandler;
    private ZarzadcaBazy zarzadcaBazy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        zarzadcaBazy = new ZarzadcaBazy(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        id = new ArrayList<>();
        name = new ArrayList<>();
        tname = new ArrayList<>();
        image = new ArrayList<>();
        start = new ArrayList<>();
        end = new ArrayList<>();

        s = PreferenceManager.getDefaultSharedPreferences(this);
        userid = s.getString(Constants.USER_ID, "1");
        g = (GridView) findViewById(R.id.gv);
        mHandler = new Handler(Looper.getMainLooper());

        id.add("yo");
        name.add("yo");
        tname.add("yo");
        image.add("yo");
        start.add("yo");
        end.add("yo");

        mytrip();

        setTitle("My Trips");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    private void mytrip() {

        dialog = new MaterialDialog.Builder(MyTrips.this)
                .title(R.string.app_name)
                .content("Fetching trips...")
                .progress(true, 0)
                .show();

        String uri = Constants.apilink + "trip/get-all.php?user=" + userid;
        Log.e("executing", uri + " ");


        for (PodrozDB podrozDB : zarzadcaBazy.getAllTrips()) {
            name.add(podrozDB.getNazwa());
            id.add(String.valueOf(podrozDB.getId()));
            start.add(podrozDB.getDataStart());
            end.add(podrozDB.getDataKoniec());
            image.add("http://www.helpunlimited.ca/wp-content/uploads/2015/05/road-trip_rainbow-1024x685.jpg");
        }


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                g.setAdapter(new MyTripsadapter(MyTrips.this, id, name, image, start, end));
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public class MyTripsadapter extends ArrayAdapter<String> {
        private final Activity context;
        private final List<String> ids, name, image, start, end;
        ImageView city;
        TextView cityname, date;

        public MyTripsadapter(Activity context, List<String> id, List<String> name, List<String> image, List<String> start, List<String> end) {
            super(context, R.layout.trip_listitem, id);
            this.context = context;
            ids = id;
            this.name = name;
            this.image = image;
            this.start = start;
            this.end = end;
        }

        @Override
        public View getView(final int position, View view2, ViewGroup parent) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = mInflater.inflate(R.layout.trip_listitem, null);
            city = (ImageView) view.findViewById(R.id.profile_image);
            cityname = (TextView) view.findViewById(R.id.tv);
            date = (TextView) view.findViewById(R.id.date);

            if (position == 0) {
                city.setImageResource(R.drawable.ic_add_circle_black_24dp);
                cityname.setText("Add New Trip");
                date.setText("");

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MyTrips.this, AddNewTrip.class);
                        context.startActivity(i);
                    }
                });

            } else {
                Picasso.with(MyTrips.this).load(image.get(position)).placeholder(R.drawable.add_list_item)
                        .into(city);
                cityname.setText(name.get(position));
                date.setText(start.get(position));
                Log.e("time", start.get(position) + " " + image.get(position));
                final Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(Long.parseLong(start.get(position)) * 1000);
                final String timeString =
                        new SimpleDateFormat("dd-MMM").format(cal.getTime());
                date.setText(timeString);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MyTrips.this, MyTripInfo.class);
                        i.putExtra("_id", id.get(position));
                        i.putExtra("_image", image.get(position));
                        startActivity(i);
                    }
                });
            }
            return view;
        }
    }
}
