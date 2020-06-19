package codered.codered;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestActivity extends AppCompatActivity {

    private Button submitButton;
    private EditText messageEditText;

    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRequest();
            }
        });

        messageEditText = findViewById(R.id.message_text);

    }

    private void submitRequest(){

        // gets the generated id from firebase
        DatabaseReference requestRef = fireRef.child("requests");
        String rId = requestRef.push().getKey();
        // get whatever message was in the textbox
        String message = messageEditText.getText().toString();
        Request r = new Request(rId, message);
        requestRef.child(rId).setValue(r);

        finish();
    }
}