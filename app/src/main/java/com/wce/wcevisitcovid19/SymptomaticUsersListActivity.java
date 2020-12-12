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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.adapters.UserListAdapter;

import java.util.ArrayList;

public class SymptomaticUsersListActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("Daily_assessment");
    ListView usersListView;
    ArrayList<String> usersList = new ArrayList<>();
    ArrayList<String> userTypeList = new ArrayList<>();
    ArrayList<String> userIdList = new ArrayList<>();
    private static final String TAG = "SymptomaticStudentsList";
    UserListAdapter userListAdapter;
    ProgressBar progressBar ;
    TextView countTextView;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptomatic_users_list);

        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        String symptom = intent.getStringExtra("symptom");
        String year = String.valueOf(intent.getStringExtra("year"));
        String month = String.valueOf(intent.getStringExtra("month"));
        String date = String.valueOf(intent.getStringExtra("date"));

        getSupportActionBar().setTitle(symptom);

        usersListView = findViewById(R.id.symptomatic_users_list_view);
        userListAdapter = new UserListAdapter(this, usersList,userTypeList);
        usersListView.setAdapter(userListAdapter);
        countTextView = findViewById(R.id.symptomatic_users_count_text_view);

        //fetching students
        DatabaseReference todaysAssessmentDatabaseReference = dbRef.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(date)).child("Symptoms").child(symptom).child("Students");
        todaysAssessmentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String studentPRN = postSnapshot.getKey();
                    String status = postSnapshot.getValue(String.class);


                    if("yes".equalsIgnoreCase(status)) {
                        DatabaseReference studentDatabaseReference = database.getReference("Students").child(studentPRN);
                        studentDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String studentName = snapshot.child("Name").getValue(String.class);
                                userIdList.add(studentPRN);
                                usersList.add(studentName);
                                userTypeList.add("Students");
                                count = usersList.size();
                                countTextView.setText(String.valueOf(count));
                                Log.i(TAG, "onDataChange: Student having symptom: " + studentName);
                                userListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });


        //fetching faculties
        DatabaseReference todaysAssessmentDatabaseReference1 = dbRef.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(date)).child("Symptoms").child(symptom).child("Faculty");
        todaysAssessmentDatabaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String empId = postSnapshot.getKey();
//                    String facultyName = postSnapshot.child("");
                    String status = postSnapshot.getValue(String.class);

                    if("yes".equalsIgnoreCase(status)) {
                        DatabaseReference facultyDatabaseReference = database.getReference("Faculty").child(empId);
                        facultyDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String facultyName = snapshot.child("Name").getValue(String.class);
                                userIdList.add(empId);
                                usersList.add(facultyName);
                                userTypeList.add("Faculty");
                                count = usersList.size();
                                countTextView.setText(String.valueOf(count));
                                Log.i(TAG, "onDataChange: Faculty having symptom: " + facultyName);
                                userListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });


        //fetching non-teaching staff
        DatabaseReference todaysAssessmentDatabaseReference2 = dbRef.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(date)).child("Symptoms").child(symptom).child("Non_teaching");
        todaysAssessmentDatabaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String empId = postSnapshot.getKey();
                    String status = postSnapshot.getValue(String.class);

                    if("yes".equalsIgnoreCase(status)) {
                        DatabaseReference nonTeachingDatabaseReference = database.getReference("Non_teaching").child(empId);
                        nonTeachingDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String nonTeachingStaffName = snapshot.child("Name").getValue(String.class);
                                userIdList.add(empId);
                                usersList.add(nonTeachingStaffName);
                                userTypeList.add("Non_teaching");
                                count = usersList.size();
                                countTextView.setText(String.valueOf(count));
                                Log.i(TAG, "onDataChange: Non Teaching Staff having symptom: " + nonTeachingStaffName);

                                userListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });


        //fetching outsider
        DatabaseReference todaysAssessmentDatabaseReference3 = dbRef.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(date)).child("Symptoms").child(symptom).child("Outsiders");
        todaysAssessmentDatabaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String visitorId = postSnapshot.getKey();
                    String status = postSnapshot.getValue(String.class);

                    if("yes".equalsIgnoreCase(status)) {
                        DatabaseReference outsiderDatabaseReference = database.getReference("Outsiders").child(visitorId);
                        outsiderDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String outsiderName = snapshot.child("Name").getValue(String.class);
                                userIdList.add(visitorId);
                                usersList.add(outsiderName);
                                userTypeList.add("Outsiders");
                                count = usersList.size();
                                countTextView.setText(String.valueOf(count));
                                Log.i(TAG, "onDataChange: Outsider having symptom: " + outsiderName);
                                userListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        progressBar.setVisibility(View.GONE);

        userTypeList.clear();
        userIdList.clear();
        usersList.clear();

        countTextView.setText(String.valueOf(count));

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = usersList.get(position);
                String userType = userTypeList.get(position);
                String userId = userIdList.get(position);
                Intent intent = new Intent(SymptomaticUsersListActivity.this,UserDetailsActivity.class);
                intent.putExtra("userId",userId);
                intent.putExtra("userName",userName);
                intent.putExtra("userType",userType);
                startActivity(intent);
            }
        });

    }
}