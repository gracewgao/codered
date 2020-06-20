package codered.codered;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RequestFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private View view;

    private List<Request> requests = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private final int REQUEST_ACCESS_FINE_LOCATION=1;
    private FusedLocationProviderClient fusedLocationClient;
    private MainActivity main;

    public static Location location;

    // Firebase
    DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request, null);

        main = (MainActivity) getActivity();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // recycler view set up
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(main);
        recyclerView.setLayoutManager(layoutManager);

        // gets user's current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(main);
        if (ActivityCompat.checkSelfPermission(main, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(main, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location l) {
                            if (l != null) {
                                location = l;
                                mAdapter = new ItemAdapter(requests, location, main);
                                recyclerView.setAdapter(mAdapter);
                                getRequests();
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(main, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }

        return view;
    }

    private void getRequests() {
        swipeRefreshLayout.setRefreshing(true);
        DatabaseReference requestRef = fireRef.child("requests");
        requestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // clears the list to fetch new data
                requests.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Request req = itemSnapshot.getValue(Request.class);
                    int d = RequestFragment.findDistance(location, req.getLat(), req.getLng());
                    int wait = req.secAgo((long)req.getTimestamp());
                    // only displays if the request is pending, close enough, and recent enough
                    if (req.getStatus()==0 && d<1000 && wait <= (30*60)) {
                        requests.add(req);
                    }
                }

                // sorts data based on location
                Collections.sort(requests);

                // refreshes recycler view
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError);
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    public static int findDistance(Location l, double targetLat, double targetLng){
        Location target = new Location("target");
        target.setLatitude(targetLat);
        target.setLongitude(targetLng);
        int d = (int)Math.floor(l.distanceTo(target));
        return d;
    }

    @Override
    public void onRefresh() {
        getRequests();
    }

}
