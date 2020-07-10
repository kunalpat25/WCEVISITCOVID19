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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.models.Faculty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("Daily_assessment");
    private static final String TAG = "AdminActivity";
    ArrayList<String> symptomsList = new ArrayList<>();
    SymptomsListAdapter symptomsListAdapter;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        final ListView symptomsListView = findViewById(R.id.symptomsListView);
        symptomsListAdapter = new SymptomsListAdapter(this,symptomsList);

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
                    Toast.makeText(AdminActivity.this, "You have been signed out successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(AdminActivity.this, "Please Sign in first!", Toast.LENGTH_SHORT).show();
            }
        });

        final int year = Calendar.getInstance().get(Calendar.YEAR);
        String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
        String date = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        if(month.length() == 1)
            month = "0"+ month;

        if(date.length() == 1)
            date ="0"+ date;

        final String finalmonth = String.valueOf(month);
        final String finaldate  = String.valueOf(date);

        Log.i(TAG, "onCreate: "+year+" "+month+" "+date);

        DatabaseReference todaysAssessmentDatabaseReference = dbRef.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(date));
        todaysAssessmentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String symptom = postSnapshot.getKey();
                    symptomsList.add(symptom);
                    Log.i(TAG, "onDataChange: Symptom: "+symptom);
                    symptomsListView.setAdapter(symptomsListAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        symptomsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(AdminActivity.this,SymptomaticStudentsListActivity.class);
                intent.putExtra("symptom",symptomsList.get(position));
                intent.putExtra("year",String.valueOf(year));
                intent.putExtra("month",finalmonth);
                intent.putExtra("date",finaldate);
                startActivity(intent);

            }
        });
    }
}