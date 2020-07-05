package codered.codered;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    final static String TAG = MainActivity.class.getSimpleName();

    // Phone storage
    private SharedPreferences pref;

    // views
    private Button requestButton;
    public static String REQS = "CODERED_REQS";
    private ArrayList<String> reqs;

    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        createNotificationChannel();
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new RequestFragment());

        requestButton = findViewById(R.id.request_button);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RequestActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

    }

    // saves array into local storage
    public static void saveArrayList(ArrayList<String> list, String key, Context c){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    // retrieves array from local storage
    public static ArrayList<String> getArrayList(String key, Context c){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // ensures fragment is loaded when returning to activity
    @Override
    protected void onResume() {
        super.onResume();
        loadFragment(new RequestFragment());
    }

    // set up listener for when data changed --> sends relevant notifications to user
    public void setUpNotifs(){
        DatabaseReference requestRef = fireRef.child("requests");
        requestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                reqs = getArrayList(REQS, MainActivity.this);

                // clears the list to fetch new data
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Request req = itemSnapshot.getValue(Request.class);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        int d = RequestFragment.findDistance(RequestFragment.location, req.getLat(), req.getLng());
                        int wait = Request.secAgo((long) req.getTimestamp());
                        // sends a notification to alert user of a nearby request posted
                        if (reqs != null){
                            boolean mine = reqs.contains(req.getId());
                            // if pending request, within 200m and 30 seconds
                            if (req.getStatus() == 0 && d < 200 && wait <= 30 && !mine) {
                                String title = "Do you have a " + (Request.products[req.getProduct()]).toLowerCase() + " ?";
                                String message = "Help out a sister in need! (" + d + " m away)";
                                addNotification(title, message, req.getProduct());
                            }
                        }
                    }
                    // if the request is the user's own pending request that has been answered
                    if (reqs != null && reqs.contains(req.getId()) && req.getStatus()==1){
                        String title = "Help is on the way!";
                        String message = "Your code word is " + req.getCode();
                        // removes from pending list stored in phone
                        reqs.remove(req.getId());
                        saveArrayList(reqs, REQS, MainActivity.this);
                        // shows notification that help is coming
                        addNotification(title, message, req.getProduct());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError);
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.navigation_home:
                fragment = new RequestFragment();
                break;
            case R.id.navigation_idk:
                // TODO - add screen here
                break;
            case R.id.navigation_learn:
                fragment = new LearnFragment();
                break;
            case R.id.navigation_profile:
                LauncherActivity.signOut(this);
                break;
        }
        return loadFragment(fragment);
    }

    private void addNotification(String title, String message, int product) {

        // builds notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CODERED")
                .setSmallIcon(R.drawable.reqbutton)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), Request.productIcons[product]))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        // creates the intent needed to show the notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // add as notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(
                Context.NOTIFICATION_SERVICE
        );
        notificationManager.notify(0,builder.build());

    }
    private void createNotificationChannel() {
        // create the NotificationChannel, but only on API 26+ because the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "codeREDchannel";
            String description = "Channel for codeRED notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CODERED", name, importance);
            channel.setDescription(description);
            // register the channel with the system; you can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
