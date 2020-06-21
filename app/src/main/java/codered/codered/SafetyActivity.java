package codered.codered;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SafetyActivity extends AppCompatActivity {

    ImageButton myImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        myImageButton = (ImageButton) findViewById(R.id.my_image_button);
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoadNewActivity = new Intent (SafetyActivity.this, MainActivity.class);
                startActivity(intentLoadNewActivity);
            }
        });
    }
}

