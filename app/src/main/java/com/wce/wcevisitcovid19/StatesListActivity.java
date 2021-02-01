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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.adapters.DistrictsListAdapter;
import com.wce.wcevisitcovid19.adapters.StatesListAdapter;

import java.util.ArrayList;

public class StatesListActivity extends AppCompatActivity {


    private static final String TAG = "StateWiseListActivit";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("Statewise list");
    ListView stateListView ;
    ArrayList<String> statesList = new ArrayList<>();
    StatesListAdapter statesListAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_states_list);

        progressBar = findViewById(R.id.progressBar);

        stateListView = findViewById(R.id.states_list_view);
        statesListAdapter = new StatesListAdapter(this, statesList);
        stateListView.setAdapter(statesListAdapter);

        Intent intent = getIntent();
        String name = intent.getStringExtra("UserName");

        //fetching states
        DatabaseReference stateDatabaseReference = dbRef;
        stateDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String stateName = postSnapshot.getKey();

                    statesList.add(stateName);
                    statesListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        progressBar.setVisibility(View.GONE);

        stateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stateName = statesList.get(position);
                Intent intent = new Intent(StatesListActivity.this,StateUsersActivity.class);
                intent.putExtra("state",stateName);
                startActivity(intent);
            }
        });

        statesListAdapter.clear();
        statesList.clear();
    }
}