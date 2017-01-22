package tie.hackathon.travelguide;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.processbutton.FlatButton;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Util.Constants;
import adapters.NestedListView;
import foto.PhotoIntentActivity;
import tie.hackathon.travelguide.tables.FriendDb;
import tie.hackathon.travelguide.tables.PodrozDB;

public class MyTripInfo extends AppCompatActivity {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private String id;
    private String title;
    private String start;
    private String end;
    private String city;
    private String friendid;
    private String img;
    private final String mainfolder = "/storage/emulated/0/Pictures/";
    private String nameyet;
    private Intent intent;
    private MaterialDialog dialog;
    private ImageView iv;
    private TextView tite;
    private TextView date;
    private TextView dateEnd;
    private TextView description;
    private FlatButton add;
    private TwoWayView twoway;
    private NestedListView lv;
    private EditText frendname;
    private List<String> fname;
    private List<File> imagesuri;
    private List<File> mediaimages;
    private Handler mHandler;
    private ZarzadcaBazy zarzadcaBazy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        zarzadcaBazy = new ZarzadcaBazy(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip_info);
        intent = getIntent();
        id = intent.getStringExtra("_id");
        img = intent.getStringExtra("_image");

        mediaimages = new ArrayList<>();
        imagesuri = new ArrayList<>();
        fname = new ArrayList<>();

        twoway = (TwoWayView) findViewById(R.id.lv);
        iv = (ImageView) findViewById(R.id.image);
        tite = (TextView) findViewById(R.id.head);
        date = (TextView) findViewById(R.id.time);
        dateEnd = (TextView) findViewById(R.id.timeEnd);
        description = (TextView) findViewById(R.id.description);
        lv = (NestedListView) findViewById(R.id.friendlist);
        add = (FlatButton) findViewById(R.id.newfrriend);
        frendname = (EditText) findViewById(R.id.fname);

        Picasso.with(this).load(img).into(iv);

        mHandler = new Handler(Looper.getMainLooper());

        File sdDir = new File(mainfolder);
        File[] sdDirFiles = sdDir.listFiles();
        if (sdDirFiles != null) {
            for (File singleFile : sdDirFiles) {
                if (!singleFile.isDirectory())
                    mediaimages.add(singleFile);
            }
        }
        mediaimages.add(null);

        Imagesadapter ad = new Imagesadapter(this, mediaimages);
        twoway.setAdapter(ad);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addfriend();
            }
        });

        mytrip();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void mytrip() {

        dialog = new MaterialDialog.Builder(MyTripInfo.this)
                .title(R.string.app_name)
                .content("Fetching trips...")
                .progress(true, 0)
                .show();
        PodrozDB podrozDB = zarzadcaBazy.getPodroz(Integer.valueOf(id));
        title = podrozDB.getNazwa();
        start = podrozDB.getDataStart();
        end = podrozDB.getDataKoniec();
        city = podrozDB.getMiasto();

        tite.setText(city);
        tite = (TextView) findViewById(R.id.tname);
        tite.setText(title);
        final Calendar calStart = Calendar.getInstance();
        final Calendar calEnd = Calendar.getInstance();
        calStart.setTimeInMillis(Long.valueOf(start) * 1000);
        calEnd.setTimeInMillis(Long.valueOf(end) * 1000);

        date.setText("Started on : " + new SimpleDateFormat("dd-MM-yyyy").format(calStart.getTime()));
        dateEnd.setText("Ended on: " + new SimpleDateFormat("dd-MM-yyyy").format(calEnd.getTime()));

        description.setText(podrozDB.getOpis());
        for(FriendDb f : zarzadcaBazy.getAllFriendsByPodrozId(Integer.valueOf(id))){
            fname.add(f.getName());
        }

        Friendnameadapter dataAdapter = new Friendnameadapter(MyTripInfo.this, fname);
        lv.setAdapter(dataAdapter);

        dialog.dismiss();

//        // to fetch city names
//        String uri = Constants.apilink + "trip/get-one.php?trip=" + id;
//        Log.e("executing", uri + " ");
//
//
//        //Set up client
//        OkHttpClient client = new OkHttpClient();
//        //Execute request
//        Request request = new Request.Builder()
//                .url(uri)
//                .build();
//        //Setup callback
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("Request Failed", "Message : " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//
//                final String res = response.body().string();
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONObject ob;
//                        try {
//                            ob = new JSONObject(res);
////                            title = ob.getString("title");
//                            title = "to je jakis tytyul ";
//                            start = ob.getString("start_time");
//                            end = ob.getString("end_time");
//                            city = ob.getString("city");
//
//                            tite.setText(city);
//                            tite = (TextView) findViewById(R.id.tname);
//                            tite.setText(title);
//                            final Calendar cal = Calendar.getInstance();
//                            cal.setTimeInMillis(Long.parseLong(start) * 1000);
//                            final String timeString =
//                                    new SimpleDateFormat("dd-MMM").format(cal.getTime());
//                            date.setText("Started on : " + timeString);
//
//                            JSONArray arrr = ob.getJSONArray("users");
//                            for (int i = 0; i < arrr.length(); i++) {
//                                fname.add(arrr.getJSONObject(i).getString("name"));
//
//                                Log.e("fvdvdf", "adding " + arrr.getJSONObject(i).getString("name"));
//                            }
//
//                            Log.e("vdsv", fname.size() + " ");
//
//                            Friendnameadapter dataAdapter = new Friendnameadapter(MyTripInfo.this, fname);
//                            lv.setAdapter(dataAdapter);
//
//
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                        dialog.dismiss();
//                    }
//                });
//
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {

            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            for (int i = 0; i < image_uris.size(); i++) {
                Log.e("cdscsd", image_uris.get(i).getPath());
            }
            Toast.makeText(MyTripInfo.this, "Images added", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


    private void addfriend() {

        dialog = new MaterialDialog.Builder(MyTripInfo.this)
                .title(R.string.app_name)
                .content("Please wait...")
                .progress(true, 0)
                .show();

        zarzadcaBazy.dodajFriend(frendname.getText().toString(), Integer.valueOf(id));

        Toast.makeText(MyTripInfo.this, "Friend added", Toast.LENGTH_LONG).show();
//                        finish();
        dialog.dismiss();

    }

    public class Imagesadapter extends ArrayAdapter<File> {
        private final Activity context;
        private final List<File> name;


        Imagesadapter(Activity context, List<File> name) {
            super(context, R.layout.trip_listitem, name);
            this.context = context;
            this.name = name;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (view == null) {
                view = mInflater.inflate(R.layout.image_listitem, null);
                holder = new ViewHolder();
                holder.iv = (ImageView) view.findViewById(R.id.iv);

                view.setTag(holder);
            } else
                holder = (ViewHolder) view.getTag();
            if (position == name.size() - 1) {
                holder.iv.setImageResource(R.drawable.add_image);
                holder.iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(MyTripInfo.this, ImagePickerActivity.class);
//                        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
                        PhotoIntentActivity photoIntentActivity = new PhotoIntentActivity();
                        photoIntentActivity.dispatchTakePictureIntent(1);
                    }
                });
            } else {
                holder.iv.setImageDrawable(Drawable.createFromPath(name.get(position).getAbsolutePath()));
                holder.iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MyTripInfo.this, EventImage.class);
                        ArrayList<String> a = new ArrayList<>();
                        a.add(name.get(position).getAbsolutePath());

                        i.putExtra(Constants.EVENT_IMG, a);
                        i.putExtra(Constants.EVENT_NAME, "Image");
                        startActivity(i);
                    }
                });
            }
            return view;
        }

        private class ViewHolder {
            ImageView iv;
        }
    }

    public class Friendnameadapter extends ArrayAdapter<String> {
        private final Activity context;
        private final List<String> name;

        public Friendnameadapter(Activity context, List<String> name) {
            super(context, R.layout.friend_listitem, name);
            this.context = context;
            this.name = name;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (view == null) {
                view = mInflater.inflate(R.layout.friend_listitem, null);
                holder = new ViewHolder();
                holder.iv = (TextView) view.findViewById(R.id.name);
                view.setTag(holder);
            } else
                holder = (ViewHolder) view.getTag();
            holder.iv.setText(name.get(position) + " ");
            return view;
        }

        private class ViewHolder {
            TextView iv;
        }
    }

}
