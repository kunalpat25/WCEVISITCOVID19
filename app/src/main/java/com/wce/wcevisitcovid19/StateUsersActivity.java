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

public class StateUsersActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();
    TextView stateTextView ;

    ArrayList<String> usersList = new ArrayList<>();
    ArrayList<String> userTypeList = new ArrayList<>();
    ArrayList<String> userIdList = new ArrayList<>();
    private static final String TAG = "StateUsersActivity";
    UserListAdapter userListAdapter;
    ListView stateUsersListView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_users);

        progressBar = findViewById(R.id.progressBar);

        stateUsersListView = findViewById(R.id.state_users_list_view);
        stateTextView = findViewById(R.id.state_text_view);
        userListAdapter = new UserListAdapter(this,usersList,userTypeList);
        stateUsersListView.setAdapter(userListAdapter);

        Intent intent = getIntent();
        final String stateName = intent.getStringExtra("state");
        stateTextView.setText(stateName);

        //fetching students
        DatabaseReference studentsDatabaseReference = dbRef.child("Students");
        studentsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentName ;
                    try {
                        studentName = studentSnapshot.child("Name").getValue(String.class);
                    } catch (NullPointerException e) {
                        studentName = "";
                        e.printStackTrace();
                    }
                    String state = studentSnapshot.child("State").getValue(String.class);
                    String studentId = studentSnapshot.getKey();
                    if(stateName.equals(state)) {
                        usersList.add(studentName);
                        userTypeList.add("Students");
                        userIdList.add(studentId);
                        Log.i(TAG, "onDataChange: Student in state: " +state +":"+ studentName);
                        userListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });


        //fetching faculties
        DatabaseReference facultiesDatabaseReference = dbRef.child("Faculty");
        facultiesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
                    String facultyName = "";
                    try {
                        facultyName = facultySnapshot.child("Name").getValue(String.class);
                    } catch (NullPointerException e) {
                        facultyName = facultySnapshot.getKey();
                        e.printStackTrace();
                    }
                    String state = facultySnapshot.child("State").getValue(String.class);

                    if(state.equals(stateName)) {
                        String facultyId = facultySnapshot.getKey();
                        userIdList.add(facultyId);
                        usersList.add(facultyName);
                        userTypeList.add("Faculty");
                        Log.i(TAG, "onDataChange: Faculty in state " +state +":"+ facultyName);
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


        //fetching Non-teaching staff
        DatabaseReference nonTeachingDatabaseReference = dbRef.child("Non_teaching");
        nonTeachingDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot nonTeachingSnapshot : dataSnapshot.getChildren()) {
                    String nonTeachingStaffName;
                    try {
                        nonTeachingStaffName = nonTeachingSnapshot.child("Name").getValue(String.class);
                    }
                    catch (NullPointerException e)
                    {
                        nonTeachingStaffName = nonTeachingSnapshot.getKey();
                    }
                    String state;
                    try {
                        state = nonTeachingSnapshot.child("State").getValue(String.class);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        continue;
                    }


                    if(state.equals(stateName)) {
                        String nonTeachingStaffId = nonTeachingSnapshot.getKey();
                        userIdList.add(nonTeachingStaffId);
                        usersList.add(nonTeachingStaffName);
                        userTypeList.add("Non_teaching");
                        Log.i(TAG, "onDataChange: Non-teaching staff in state: " +state +":"+ nonTeachingStaffName);
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


        //fetching outsiders
        DatabaseReference outsiderDatabaseReference = dbRef.child("Outsiders");
        outsiderDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot outsiderSnapshot : dataSnapshot.getChildren()) {
                    String outsiderName;
                    try {
                        outsiderName = outsiderSnapshot.child("Name").getValue(String.class);
                    }
                    catch (NullPointerException e)
                    {
                        outsiderName = outsiderSnapshot.getKey();
                        e.printStackTrace();
                    }
                    String state;
                    try {
                        state = outsiderSnapshot.child("State").getValue(String.class);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        continue;
                    }


                    if(state.equals(stateName)) {
                        String outsiderId = outsiderSnapshot.getKey();
                        userIdList.add(outsiderId);
                        usersList.add(outsiderName);
                        userTypeList.add("Outsiders");
                        Log.i(TAG, "onDataChange: Outsider in state " +state +":"+ outsiderName);
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

        stateUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = usersList.get(position);
                String userType = userTypeList.get(position);
                String userId = userIdList.get(position);
                Intent intent = new Intent(StateUsersActivity.this,UserDetailsActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userType",userType);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        usersList.clear();
        userListAdapter.clear();
    }
}