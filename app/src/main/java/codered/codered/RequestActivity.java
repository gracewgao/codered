package codered.codered;

import android.Manifest;
import android.app.TimePickerDialog;
import android.app.NotificationChannel;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RequestActivity extends AppCompatActivity {

    private Button submitButton, locationButton;
    private RadioButton currentLocationButton, chooseLocationButton, currentTimeButton, chooseTimeButton;
    private EditText messageEditText;
    private Spinner productSpinner;
    private TimePickerDialog timePicker;
    private TextView codeTv;

    private FusedLocationProviderClient fusedLocationClient;
    private double lat, lng;
    private Object meetTime;
    private String code;

    private final int REQUEST_ACCESS_FINE_LOCATION=1;
    ImageButton myImageButton;
    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        myImageButton = (ImageButton) findViewById(R.id.my_image_button);
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoadNewActivity = new Intent (RequestActivity.this, MainActivity.class);
                startActivity(intentLoadNewActivity);
            }
        });
        // Finds you button from the xml layout file

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
        currentTimeButton = findViewById(R.id.radio_time_current);
        currentTimeButton.setOnClickListener(new View.OnClickListener(){
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
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                timePicker = new TimePickerDialog(RequestActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                chooseTimeButton.setText(sHour + ":" + sMinute);

                                Date d = new Date();
                                d.setHours(sHour);
                                d.setMinutes(sMinute);

                                // if time goes over midnight
                                if (d.before(new Date())){
                                   d.setTime(d.getTime()+(24*60*60*1000));
                                }
                                meetTime = d.getTime();
                            }
                        }, hour, minutes, true);
                timePicker.show();
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

        codeTv = findViewById(R.id.code_text);
        code = Request.generateCode();
        codeTv.setText(code);
    }


    private void submitRequest() {

        // gets the generated id from firebase
        DatabaseReference requestRef = fireRef.child("requests");
        String rId = requestRef.push().getKey();
        // get whatever data is currently selected on the screen
        String message = messageEditText.getText().toString();
        int product = productSpinner.getSelectedItemPosition();

        // creates new request object
        Request r = new Request(rId, product, message, code, lat, lng, meetTime);
        requestRef.child(rId).setValue(r)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // successfully saved
                        RadioButton currentLocation = findViewById(R.id.radio_location_current);
                        RadioButton otherLocation = findViewById(R.id.radio_location_choose);
                        RadioButton currentTime = findViewById(R.id.radio_time_current);
                        RadioButton otherTime = findViewById(R.id.radio_time_choose);
                        if (!currentLocation.isChecked() && !otherLocation.isChecked() && !currentTime.isChecked() && !otherTime.isChecked()) {
                            Toast.makeText(getApplicationContext(),"Please fill in location and time.",Toast.LENGTH_SHORT).show();
                        } else if (!currentLocation.isChecked() && !otherLocation.isChecked()) {
                            Toast.makeText(getApplicationContext(),"Please fill in location.",Toast.LENGTH_SHORT).show();
                        } else if (!currentTime.isChecked() && !otherTime.isChecked()){
                            Toast.makeText(getApplicationContext(),"Please fill in time.",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Your request has been sent!",Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed to save
                        Toast.makeText(getApplicationContext(),"Uh-oh! something went wrong, please try again.",Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
