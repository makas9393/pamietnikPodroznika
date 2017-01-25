package tie.hackathon.travelguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Displays emergency contact numbers
 */
public class EmergencyFragment extends Fragment implements View.OnClickListener {

    private Button police;
    private Button fire;
    private Button ambulance;
    private Activity activity;

    public EmergencyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emergency, container, false);

        police = (Button) v.findViewById(R.id.police);
        fire = (Button) v.findViewById(R.id.fire);
        ambulance = (Button) v.findViewById(R.id.ambulance);

        police.setOnClickListener(this);
        fire.setOnClickListener(this);
        ambulance.setOnClickListener(this);
//        blood_bank.setOnClickListener(this);
//        bomb.setOnClickListener(this);
//        railways.setOnClickListener(this);

        return v;
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        this.activity = (Activity) activity;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        switch (v.getId()) {
            case R.id.police:
                intent.setData(Uri.parse("tel:100"));
                break;
            case R.id.fire:
                intent.setData(Uri.parse("tel:101"));
                break;
            case R.id.ambulance:
                intent.setData(Uri.parse("tel:102"));
                break;
        }
        startActivity(intent);
    }
}
