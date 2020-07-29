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
import com.wce.wcevisitcovid19.adapters.SimpleListAdapter;
import com.wce.wcevisitcovid19.adapters.UserListAdapter;

import java.util.ArrayList;

public class SymptomaticUsersListActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("Daily_assessment");
    TextView symptomTextView ;
    ListView usersListView;
    ArrayList<String> usersList = new ArrayList<>();
    ArrayList<String> userTypeList = new ArrayList<>();
    private static final String TAG = "SymptomaticStudentsList";
    UserListAdapter userListAdapter;
    ProgressBar progressBar ;

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

        symptomTextView = findViewById(R.id.symptomTextView);
        symptomTextView.setText(symptom);

        usersListView = findViewById(R.id.symptomatic_users_list_view);
        userListAdapter = new UserListAdapter(this, usersList,userTypeList);
        usersListView.setAdapter(userListAdapter);

        //fetching students
        DatabaseReference todaysAssessmentDatabaseReference = dbRef.child(String.valueOf(year)).child(String.valueOf(month)).child(String.valueOf(date)).child("Symptoms").child(symptom).child("Students");
        todaysAssessmentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String studentPRN = postSnapshot.getKey();
//                    String studentName = postSnapshot.child("Name").getValue(String.class);
                    String status = postSnapshot.getValue(String.class);


                    if(status.equals("Yes")) {
                        DatabaseReference studentDatabaseReference = database.getReference("Students").child(studentPRN);
                        studentDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for(DataSnapshot studentSnapshot: snapshot.getChildren())
//                                {
//                                    if(studentSnapshot.child("PRN").getValue(String.class).equals(studentPRN)) {
//                                        String studentName = studentSnapshot.child(studentPRN).child("Name").getValue(String.class);
//                                        usersList.add(studentName);
//                                        userTypeList.add("Students");
//                                        Log.i(TAG, "onDataChange: Student having symptom: " + studentName);
//                                        simpleListAdapter.notifyDataSetChanged();
//                                    }
//                                }
                                String studentName = snapshot.child("Name").getValue(String.class);
//                                String displayName = studentName + " (\\033[3mStudent\\033[0m)";
                                usersList.add(studentName);
                                userTypeList.add("Students");
                                Log.i(TAG, "onDataChange: Student having symptom: " + studentName);
                                userListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });
//                        usersList.add(studentName);
//                        userTypeList.add("Students");
//                        Log.i(TAG, "onDataChange: Student having symptom: " + studentName);
//                        simpleListAdapter.notifyDataSetChanged();
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
                    String facultyName = postSnapshot.getKey();
                    String status = postSnapshot.getValue(String.class);

                    if(status.equals("Yes")) {
//                        String displayName = facultyName + " (\\033[3mFaculty\\033[0m)";
                        usersList.add(facultyName);
                        userTypeList.add("Faculty");
                        Log.i(TAG, "onDataChange: Faculty having symptom: " + facultyName);
//                        simpleListAdapter.clear();
                        userListAdapter.notifyDataSetChanged();
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
                    String nonTeachingStaffName = postSnapshot.getKey();
                    String status = postSnapshot.getValue(String.class);

                    if(status.equals("Yes")) {
//                        String displayName = nonTeachingStaffName + " (\\033[3mNon-Teaching Staff\\033[0m)";
                        usersList.add(nonTeachingStaffName);
                        userTypeList.add("Non_teaching");
                        Log.i(TAG, "onDataChange: Non Teaching Staff having symptom: " + nonTeachingStaffName);
//                        simpleListAdapter.clear();
                        userListAdapter.notifyDataSetChanged();
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
                    String outsiderName = postSnapshot.getKey();
                    String status = postSnapshot.getValue(String.class);

                    if(status.equals("Yes")) {
//                        String displayName = outsiderName + " (\\033[3mVisitor\\033[0m)";
                        usersList.add(outsiderName);
                        userTypeList.add("Outsiders");
                        Log.i(TAG, "onDataChange: Outsider having symptom: " + outsiderName);
                        userListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        progressBar.setVisibility(View.GONE);

        userListAdapter.clear();
        usersList.clear();

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = usersList.get(position);
                String userType = userTypeList.get(position);
                Intent intent = new Intent(SymptomaticUsersListActivity.this,UserDetailsActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userType",userType);
                intent.putExtra("fetchUsingPRN","no");
                startActivity(intent);
            }
        });

    }
}