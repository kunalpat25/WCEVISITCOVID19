package com.wce.wcevisitcovid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.models.Faculty;

import java.util.ArrayList;

public class SymptomaticStudentsListActivity extends AppCompatActivity {


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("Daily_assessment");
    TextView symptomTextView ;
    ListView studentsListView ;
    ArrayList<String> studentList = new ArrayList<>();
    private static final String TAG = "SymptomaticStudentsList";
    SymptomaticStudentListAdapter symptomaticStudentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptomatic_students_list);

        Intent intent = getIntent();
        String symptom = intent.getStringExtra("symptom");
        String year = String.valueOf(intent.getStringExtra("year"));
        String month = String.valueOf(intent.getStringExtra("month"));
        String date = String.valueOf(intent.getStringExtra("date"));

        symptomTextView = findViewById(R.id.symptomTextView);
        symptomTextView.setText(symptom);

        studentsListView = findViewById(R.id.students_list_view);
        symptomaticStudentListAdapter = new SymptomaticStudentListAdapter(this,studentList);

        DatabaseReference todaysAssessmentDatabaseReference = dbRef.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(date)).child(symptom).child("Student");
        todaysAssessmentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String studentName = postSnapshot.getKey();
                    String status = postSnapshot.getValue(String.class);

                    if(status.equals("Yes")) {
                        studentList.add(studentName);
                        Log.i(TAG, "onDataChange: Student having symptom: " + studentName);
                        studentsListView.setAdapter(symptomaticStudentListAdapter);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });
    }
}