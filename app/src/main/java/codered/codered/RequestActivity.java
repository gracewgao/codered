package codered.codered;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class RequestActivity extends AppCompatActivity {

    private Button submitButton, locationButton;
    private RadioButton currentLocationButton, chooseLocationButton, currentTimeButton, chooseTimeButton;
    private EditText messageEditText;
    private Spinner productSpinner;

    private FusedLocationProviderClient fusedLocationClient;
    private double lat, lng;
    private Object meetTime;

    private final int REQUEST_ACCESS_FINE_LOCATION=1;

    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // to choose another location
        chooseLocationButton = findViewById(R.id.radio_location_choose);
        chooseLocationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO: change later
                if (ActivityCompat.checkSelfPermission(RequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(RequestActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        lat = location.getLatitude();
                                        lng = location.getLongitude();
                                    }
                                }
                            });
                } else {
                    ActivityCompat.requestPermissions(RequestActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                }
            }
        });

        // to use current location
        currentLocationButton = findViewById(R.id.radio_location_current);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(RequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(RequestActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        lat = location.getLatitude();
                                        lng = location.getLongitude();
                                    }
                                }
                            });
                } else {
                    ActivityCompat.requestPermissions(RequestActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                }
            }
        });

        // to use current time
        chooseTimeButton = findViewById(R.id.radio_time_choose);
        chooseTimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                meetTime = ServerValue.TIMESTAMP;
            }
        });

        // to choose another time
        chooseTimeButton = findViewById(R.id.radio_time_choose);
        chooseTimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO: change later
                meetTime = ServerValue.TIMESTAMP;
            }
        });


        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRequest();
            }
        });

        messageEditText = findViewById(R.id.message_text);
        productSpinner = findViewById(R.id.products_spinner);


    }

    private void submitRequest() {

        // gets the generated id from firebase
        DatabaseReference requestRef = fireRef.child("requests");
        String rId = requestRef.push().getKey();
        // get whatever data is currently selected on the screen
        String message = messageEditText.getText().toString();
        int product = productSpinner.getSelectedItemPosition();
        String code = Request.generateCode();

        // creates new request object
        Request r = new Request(rId, product, message, code, lat, lng, meetTime);
        requestRef.child(rId).setValue(r)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // successfully saved
                        Toast.makeText(getApplicationContext(),"Your request has been sent!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed to save
                        Toast.makeText(getApplicationContext(),"Uh-oh! something went wrong, please try again.",Toast.LENGTH_SHORT).show();
                    }
                });;
    }

}
