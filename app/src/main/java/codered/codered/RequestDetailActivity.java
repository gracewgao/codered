package codered.codered;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static codered.codered.Request.products;
import static codered.codered.Request.states;

public class RequestDetailActivity extends AppCompatActivity {

    private static final String TAG = "RequestDetailActivity";
    private TextView timeText, messageText, productText, statusText, distanceText, codeText;
    private String rId;

    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();

    private FusedLocationProviderClient fusedLocationClient;
    private Location location;

    private final int REQUEST_ACCESS_FINE_LOCATION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_detail);
        // timeText = findViewById(R.id.read_time);
        messageText = findViewById(R.id.read_message);
        // productText = findViewById(R.id.read_product);
        // statusText = findViewById(R.id.read_status);
        // distanceText = findViewById(R.id.read_location);
        codeText = findViewById(R.id.read_code);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            rId = extras.getString("RID");
        }

        // gets user's current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location l) {
                            if (l != null) {
                                location = l;
                                loadInfo();
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    private void loadInfo(){
        fireRef.child("requests").child(rId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Request r = dataSnapshot.getValue(Request.class);
                messageText.setText(r.getMessage());
                productText.setText(Request.products[r.getProduct()]);
                statusText.setText(Request.states[r.getStatus()]);
                String time = Request.convertTime((long)r.getTimestamp());
                timeText.setText(time);
                String distance = RequestFragment.findDistance(location, r.getLat(), r.getLng())+ " m away";
                distanceText.setText(distance);
                codeText.setText(r.getCode());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}