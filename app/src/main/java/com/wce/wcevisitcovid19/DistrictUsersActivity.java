package com.wce.wcevisitcovid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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

public class DistrictUsersActivity extends AppCompatActivity
{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();
    TextView districtTextView ;

    ArrayList<String> usersList = new ArrayList<>();
    ArrayList<String> userTypeList = new ArrayList<>();
    ArrayList<String> userIdList = new ArrayList<>();
    private static final String TAG = "DistrictUsersActivity";
    UserListAdapter userListAdapter;
    ListView districtUsersListView;
    ProgressBar progressBar;

    String districtName;
    String username,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district_users);

        progressBar = findViewById(R.id.progressBar);

        districtUsersListView = findViewById(R.id.district_users_list_view);
        districtTextView = findViewById(R.id.district_text_view);
        userListAdapter = new UserListAdapter(this,usersList,userTypeList);
        districtUsersListView.setAdapter(userListAdapter);

        Intent intent = getIntent();
        districtName = intent.getStringExtra("district");
        Log.i(TAG, "onCreate: districtName from intent: "+districtName);
        districtTextView.setText(districtName);

        //trial to reduce data consumption, if not worked, remove
        //fetching students
        DatabaseReference studentsDatabaseReference = dbRef.child("District Wise Users").child(districtName).child("Students");
        studentsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    final String userId = studentSnapshot.getValue(String.class);
                    DatabaseReference studNameDatabaseReference = dbRef.child("Students").child(userId).child("Name");
                    studNameDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            username = snapshot.getValue(String.class);
                            Log.i(TAG, "onDataChange: StudentName: "+username);
                            usersList.add(username);
                            userTypeList.add("Students");
                            userIdList.add(userId);
                            Log.i(TAG, "onDataChange: Student in district: " +districtName +":"+ username);
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
        DatabaseReference facultyDatabaseReference = dbRef.child("District Wise Users").child(districtName).child("Faculty");
        facultyDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {

                    final String userId = facultySnapshot.getValue(String.class);
                    DatabaseReference facultyNameDatabaseReference = dbRef.child("Faculty").child(userId).child("Name");
                    facultyNameDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            username = snapshot.getValue(String.class);
                            Log.i(TAG, "onDataChange: facultyName: "+username);
                            usersList.add(username);
                            userTypeList.add("Faculty");
                            userIdList.add(userId);
                            Log.i(TAG, "onDataChange: Faculty in district: " +districtName +":"+ username);
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
        DatabaseReference nonTeachingDatabaseReference = dbRef.child("District Wise Users").child(districtName).child("Non_teaching");
        nonTeachingDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot nonTeachingSnapshot : dataSnapshot.getChildren()) {

                    final String userId = nonTeachingSnapshot.getValue(String.class);
                    DatabaseReference facultyNameDatabaseReference = dbRef.child("Non_teaching").child(userId).child("Name");
                    facultyNameDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            username = snapshot.getValue(String.class);
                            Log.i(TAG, "onDataChange: NonTeaching Name: "+username);
                            usersList.add(username);
                            userTypeList.add("Non_teaching");
                            userIdList.add(userId);
                            Log.i(TAG, "onDataChange: NonTeaching Staff in district: " +districtName +":"+ username);
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
        DatabaseReference outsidersDatabaseReference = dbRef.child("District Wise Users").child(districtName).child("Outsiders");
        outsidersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot outsiderSnapshot : dataSnapshot.getChildren()) {

                    final String userId = outsiderSnapshot.getValue(String.class);
                    DatabaseReference outsiderNameDatabaseReference = dbRef.child("Outsiders").child(userId).child("Name");
                    outsiderNameDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            username = snapshot.getValue(String.class);
                            Log.i(TAG, "onDataChange: OutsiderName: "+username);
                            usersList.add(username);
                            userTypeList.add("Outsiders");
                            userIdList.add(userId);
                            Log.i(TAG, "onDataChange: Outsider in district: " +districtName +":"+ username);
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

        userTypeList.clear();
        usersList.clear();
        userIdList.clear();


        districtUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = usersList.get(position);
                String userType = userTypeList.get(position);
                String userId = userIdList.get(position);
                Intent intent = new Intent(DistrictUsersActivity.this,UserDetailsActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userType",userType);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        }  );

    }
}