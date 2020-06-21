package codered.codered;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestDetailActivity extends AppCompatActivity {

    private static final String TAG = "RequestDetailActivity";
    private TextView timeText, messageText, productText, distanceText, codeText;
    private ImageView icon;
    private String rId;
    private Button goButton;

    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_detail);
         timeText = findViewById(R.id.read_time);
        messageText = findViewById(R.id.read_message);
         productText = findViewById(R.id.read_product);
         distanceText = findViewById(R.id.read_location);
        codeText = findViewById(R.id.read_code);
        goButton = findViewById(R.id.accept_request);
        icon = findViewById(R.id.read_icon);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            rId = extras.getString("RID");
        }

        loadInfo();

    }

    private void loadInfo(){
        fireRef.child("requests").child(rId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Request r = dataSnapshot.getValue(Request.class);
                messageText.setText(r.getMessage());
                productText.setText(Request.products[r.getProduct()]);
                icon.setImageResource(Request.productIcons[r.getProduct()]);

                long time = (long) r.getMeetTime();
                String message = "";
                Format format;
                Date recordedTime = new Date(time);
                if (recordedTime.before(new Date())){
                    message = "ASAP";
                } else {
                    format = new SimpleDateFormat("h:mm a");
                    message = format.format(recordedTime);
                }
                timeText.setText(message);
                String distance = RequestFragment.findDistance(RequestFragment.location, r.getLat(), r.getLng())+ " m away";
                distanceText.setText(distance);
                codeText.setText(r.getCode());

                goButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // opens google maps for navigation
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + r.getLat() + ", " + r.getLng()+"&mode=w");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // sets status as answered
        fireRef.child("requests").child(rId).child("status").setValue(1);

    }
}
