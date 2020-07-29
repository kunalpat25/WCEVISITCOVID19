package com.wce.wcevisitcovid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.adapters.DistrictsListAdapter;

import java.util.ArrayList;

public class DistrictsListActivity extends AppCompatActivity {

    private static final String TAG = "DistrictWiseListActivit";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("District wise list");
    ListView districtListView ;
    ArrayList<String> districtsList = new ArrayList<>();
    DistrictsListAdapter districtListAdapter;
    ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_districts_list);

        progressBar = findViewById(R.id.progressBar);

        districtListView = findViewById(R.id.districts_list_view);
        districtListAdapter = new DistrictsListAdapter(this, districtsList);
        districtListView.setAdapter(districtListAdapter);

        //fetching states
        DatabaseReference districtDatabaseReference = dbRef;
        districtDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String districtName = postSnapshot.getKey();

                        districtsList.add(districtName);
                        Log.i(TAG, "onDataChange: District: " + districtName);
                        districtListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        progressBar.setVisibility(View.GONE);

        districtListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String districtName = districtsList.get(position);
                Intent intent = new Intent(DistrictsListActivity.this,DistrictUsersActivity.class);
                intent.putExtra("district",districtName);
                startActivity(intent);
            }
        });

        districtListAdapter.clear();
        districtsList.clear();
    }
}