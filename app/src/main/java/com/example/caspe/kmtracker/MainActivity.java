package com.example.caspe.kmtracker;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.caspe.kmtracker.adapters.RidesAdapter;
import com.example.caspe.kmtracker.model.Ride;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Instantiate firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    // Set reference on the right location
    DatabaseReference dbRef = database.getReference("KMTracker/Rides/");

    final ArrayList<Ride> lstRides = new ArrayList<>();
    ListView listViewRides;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declare elements from the view
        listViewRides = (ListView) findViewById(R.id.listview_rides);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);

        // Get all rides in the Firebase database
        getRides();

        listViewRides.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Ride clickedRide = (Ride) parent.getItemAtPosition(position);
                if (clickedRide.username == null) {
                    // Alert dialog to ask the user if he or she wants to claim the selected ride
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.alert_title_claim_ride)
                            .setMessage(R.string.alert_message_claim_ride)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    claimRide(clickedRide);
                                }
                            }).setNegativeButton(android.R.string.no, null).show();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRides();
            }
        });
    }

    private void getRides(){
        // Get all rides from database and re-draw the list view
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstRides.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    lstRides.add(snapshot.getValue(Ride.class));
                }
                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Something went wrong
            }
        });
    }

    private void updateListView(){
        // Sort the collection of rides on date Descending
        Collections.sort(lstRides, Collections.reverseOrder());
        // Create an adapter and set it on the listview
        RidesAdapter adapter = new RidesAdapter(getBaseContext(), lstRides);
        listViewRides.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void claimRide(final Ride rideToClaim) {
        // Update the ride record with username
        dbRef.orderByChild("date").equalTo(rideToClaim.date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";
                // Username is temporary testUser until firebase authentication is implemented
                rideToClaim.username = "TestUser";

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    key = snap.getKey();
                }
                if (!key.equals("")) {
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(key, rideToClaim);
                    // Perform update operation
                    dbRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                getRides();
                                Toast.makeText(MainActivity.this, R.string.toast_ride_claim_success, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.toast_ride_claim_failure, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, R.string.toast_ride_claim_failure, Toast.LENGTH_LONG).show();
            }
        });
    }
}
