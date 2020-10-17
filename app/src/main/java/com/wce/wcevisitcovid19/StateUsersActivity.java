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
    String userId,username;
    String stateName;

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
        stateName = intent.getStringExtra("state");
        stateTextView.setText(stateName);


        //trial to reduce data consumption, if not worked, remove
        //successful
        //fetching students
        DatabaseReference studentsDatabaseReference = dbRef.child("State Wise Users").child(stateName).child("Students");
        studentsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {

                    final String studentPRN = studentSnapshot.getValue(String.class);
                    DatabaseReference studNameDatabaseReference = dbRef.child("Students").child(userId).child("Name");
                    studNameDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            username = snapshot.getValue(String.class);
                            Log.i(TAG, "onDataChange: StudentName: "+username);
                            usersList.add(username);
                            userTypeList.add("Students");
                            userIdList.add(studentPRN);
                            Log.i(TAG, "onDataChange: Student in state: " +stateName +":"+ username);
                            userListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("error", error.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });


        //fetching faculties
        DatabaseReference facultyDatabaseReference = dbRef.child("State Wise Users").child(stateName).child("Faculty");
        facultyDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {

                    userId = facultySnapshot.getValue(String.class);
                    DatabaseReference facultyNameDatabaseReference = dbRef.child("Faculty").child(userId).child("Name");
                    facultyNameDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            username = snapshot.getValue(String.class);
                            Log.i(TAG, "onDataChange: facultyName: "+username);
                            usersList.add(username);
                            userTypeList.add("Faculty");
                            userIdList.add(userId);
                            Log.i(TAG, "onDataChange: Faculty in state: " +stateName +":"+ username);
                            userListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("error", error.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });


        //fetching Non-teaching staff
        DatabaseReference nonTeachingDatabaseReference = dbRef.child("State Wise Users").child(stateName).child("Non_teaching");
        nonTeachingDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot nonTeachingSnapshot : dataSnapshot.getChildren()) {

                    userId = nonTeachingSnapshot.getValue(String.class);
                    DatabaseReference facultyNameDatabaseReference = dbRef.child("Non_teaching").child(userId).child("Name");
                    facultyNameDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            username = snapshot.getValue(String.class);
                            Log.i(TAG, "onDataChange: NonTeaching Name: "+username);
                            usersList.add(username);
                            userTypeList.add("Non_teaching");
                            userIdList.add(userId);
                            Log.i(TAG, "onDataChange: NonTeaching Staff in state: " +stateName +":"+ username);
                            userListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("error", error.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });


        //fetching outsiders
        DatabaseReference outsidersDatabaseReference = dbRef.child("State Wise Users").child(stateName).child("Outsiders");
        outsidersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot outsiderSnapshot : dataSnapshot.getChildren()) {

                    userId = outsiderSnapshot.getValue(String.class);
                    DatabaseReference outsiderNameDatabaseReference = dbRef.child("Outsiders").child(userId).child("Name");
                    outsiderNameDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            username = snapshot.getValue(String.class);
                            Log.i(TAG, "onDataChange: OutsiderName: "+username);
                            usersList.add(username);
                            userTypeList.add("Outsiders");
                            userIdList.add(userId);
                            Log.i(TAG, "onDataChange: Outsider in state: " +stateName +":"+ username);
                            userListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("error", error.getMessage());
                        }
                    });

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
                userId = userIdList.get(position);
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