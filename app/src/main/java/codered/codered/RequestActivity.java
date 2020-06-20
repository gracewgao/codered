package codered.codered;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestActivity extends AppCompatActivity {

    private Button submitButton;
    private EditText messageEditText;
    private Spinner productSpinner;

    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();
    /*private FusedLocationProviderClient fusedLocationClient;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        /*fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);*/

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

    private void submitRequest(){

        // gets the generated id from firebase
        DatabaseReference requestRef = fireRef.child("requests");
        String rId = requestRef.push().getKey();
        // get whatever data is currently selected on the screen
        String message = messageEditText.getText().toString();
        int product = productSpinner.getSelectedItemPosition();
        String code = Request.generateCode();
        // creates new request object
        Request r = new Request(rId, product, message, code);
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
    /*fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
            }
        }
    });*/
}
