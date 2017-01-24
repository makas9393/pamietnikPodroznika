package tie.hackathon.travelguide;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import java.util.Date;
import java.util.List;

import adapters.NestedListView;
import tie.hackathon.travelguide.tables.FriendDb;
import tie.hackathon.travelguide.tables.PodrozDB;

public class MyTripInfo extends AppCompatActivity {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private String id;
    private String title;
    private String start;
    private String end;
    private String city;
    private String img;
    private final String mainfolder = "/storage/emulated/0/Pictures/";
    private Intent intent;
    private MaterialDialog dialog;
    private ImageView iv;
    private TextView tite;
    private TextView date;
    private TextView dateEnd;
    private TextView description;
    private FlatButton add;
    private NestedListView lv;
    private EditText frendname;
    private List<String> fname;
    private List<File> mediaimages;
    private ZarzadcaBazy zarzadcaBazy;
    private Button takePictureButton;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        zarzadcaBazy = new ZarzadcaBazy(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip_info);
        intent = getIntent();
        id = intent.getStringExtra("_id");
        img = intent.getStringExtra("_image");

        mediaimages = new ArrayList<>();
        fname = new ArrayList<>();

        iv = (ImageView) findViewById(R.id.image);
        tite = (TextView) findViewById(R.id.head);
        date = (TextView) findViewById(R.id.time);
        dateEnd = (TextView) findViewById(R.id.timeEnd);
        description = (TextView) findViewById(R.id.description);
        lv = (NestedListView) findViewById(R.id.friendlist);
        add = (FlatButton) findViewById(R.id.newfrriend);
        frendname = (EditText) findViewById(R.id.fname);

        Picasso.with(this).load(img).into(iv);

        File sdDir = new File(mainfolder);
        File[] sdDirFiles = sdDir.listFiles();
        if (sdDirFiles != null) {
            for (File singleFile : sdDirFiles) {
                if (!singleFile.isDirectory())
                    mediaimages.add(singleFile);
            }
        }
        mediaimages.add(null);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addfriend();
            }
        });

        mytrip();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        takePictureButton = (Button) findViewById(R.id.btn_take_picture);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                //Create intent
                Intent intent = new Intent(MyTripInfo.this, DetailsActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);
            }
        });
    }


    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
//        "123_asdasd.png".split("_")[0];
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        File files [] = imagesFolder.listFiles();
        Integer licznik = 1;
        if(files != null && files.length > 0){
            for(File f : imagesFolder.listFiles()){
                try {
                    Integer idFile = Integer.valueOf(f.getName().split("_")[0]);
                    if(idFile.equals(Integer.valueOf(id))){
                        imageItems.add(new ImageItem(BitmapFactory.decodeFile(f.getAbsolutePath()), licznik.toString()));
                        licznik++;
                    }
                }catch (Exception e){
                    //
                }

            }
        }
//        for (int i = 0; i < imgs.length(); i++) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
//            imageItems.add(new ImageItem(bitmap, "Image#" + i));
//        }
        return imageItems;
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }


    public void takePicture(View view) {
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

//folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        imagesFolder.mkdirs();

        File image = new File(imagesFolder, id + "_" + timeStamp + ".png");

        Uri uriSavedImage = Uri.fromFile(image);

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, 1);
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
        dialog.dismiss();

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
