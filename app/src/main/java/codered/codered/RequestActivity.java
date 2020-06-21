package codered.codered;

import android.Manifest;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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

public class RequestActivity extends AppCompatActivity {

    private Button submitButton, locationButton;
    private RadioButton currentLocationButton, chooseLocationButton, currentTimeButton, chooseTimeButton;
    private EditText messageEditText;
    private Spinner productSpinner;
    private TextView codeTv;

    private FusedLocationProviderClient fusedLocationClient;
    private double lat, lng;
    private Object meetTime;
    private String code;

    private final int REQUEST_ACCESS_FINE_LOCATION=1;

    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        createNotificationChannel();
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
                        addNotification();
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
                });
    }
    private void addNotification() {
        // Builds your notification
        String message = "This is a notification.";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(RequestActivity.this, "kailey")
                .setSmallIcon(R.drawable.reqbutton)
                .setContentTitle("My notification")
                .setContentText(message)
                .setAutoCancel(true);


        // Creates the intent needed to show the notification
        Intent intent = new Intent(RequestActivity.this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(RequestActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        // Add as notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(
                Context.NOTIFICATION_SERVICE
        );
        notificationManager.notify(0,builder.build());
    }
   private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "studentChannel";
            String description = "channel for student notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("kailey", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
