package com.wce.wcevisitcovid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.adapters.SymptomsListAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class SymptomsActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("Daily_assessment");
    ArrayList<String> symptomsList = new ArrayList<>();
    SymptomsListAdapter symptomsListAdapter;
    Button logoutBtn;
    ListView symptomListView;
    ProgressBar progressBar ;
    private static final String TAG = "SymptomsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        progressBar = findViewById(R.id.progressBar);
        symptomsListAdapter = new SymptomsListAdapter(this,symptomsList);
        symptomListView = findViewById(R.id.symptomsListView);
        symptomListView.setAdapter(symptomsListAdapter);

        logoutBtn = findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginStatus = new MainActivity().checkUserLoginStatus(getApplicationContext());
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("WCEVISITCOVID19", 0);
                SharedPreferences.Editor editor = preferences.edit();
                if (loginStatus) {
                    editor.clear();
                    editor.apply();
                    Toast.makeText(SymptomsActivity.this, "You have been signed out successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SymptomsActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(SymptomsActivity.this, "Please Sign in first!", Toast.LENGTH_SHORT).show();
            }
        });

       Intent intent = getIntent();
       final String year = intent.getStringExtra("year");
       String month = intent.getStringExtra("month");
       String date = intent.getStringExtra("date");

        final String finalmonth = String.valueOf(month);
        final String finaldate  = String.valueOf(date);

        Log.i(TAG, "onCreate: "+year+" "+month+" "+date);

        DatabaseReference todaysAssessmentDatabaseReference = dbRef.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(date)).child("Symptoms");
        todaysAssessmentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String symptom = postSnapshot.getKey();
                    symptomsList.add(symptom);
                    Log.i(TAG, "onDataChange: Symptom: "+symptom);
                    symptomsListAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });


        symptomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SymptomsActivity.this, SymptomaticUsersListActivity.class);
                intent.putExtra("symptom",symptomsList.get(position));
                intent.putExtra("year",String.valueOf(year));
                intent.putExtra("month",finalmonth);
                intent.putExtra("date",finaldate);
                startActivity(intent);
            }
        });

        symptomsList.clear();

    }
}