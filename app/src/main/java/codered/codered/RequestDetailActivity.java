package codered.codered;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestDetailActivity extends AppCompatActivity {

    private static final String TAG = "RequestDetailActivity";
    private TextView timeText, messageText, productText, distanceText, codeText;
    private String rId;

    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_detail);
        // timeText = findViewById(R.id.read_time);
        messageText = findViewById(R.id.read_message);
         productText = findViewById(R.id.read_product);
         distanceText = findViewById(R.id.read_location);
        codeText = findViewById(R.id.read_code);

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
                Request r = dataSnapshot.getValue(Request.class);
                messageText.setText(r.getMessage());
                productText.setText(Request.products[r.getProduct()]);
                String time = Request.secAgo((long)r.getTimestamp()) + " min ago";
//                timeText.setText(time);
                String distance = RequestFragment.findDistance(RequestFragment.location, r.getLat(), r.getLng())+ " m away";
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
