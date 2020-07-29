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
import com.wce.wcevisitcovid19.adapters.SimpleListAdapter;
import com.wce.wcevisitcovid19.adapters.UserListAdapter;

import java.util.ArrayList;

public class QuarantinedStampedActivity extends AppCompatActivity {

    private static final String TAG = "QuarantinedStampedActiv";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();
    ArrayList<String> usersList = new ArrayList<>();
    ArrayList<String> userTypeList = new ArrayList<>();
    UserListAdapter userListAdapter;
    ListView quarantinedUsersListView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarantined_stamped);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        quarantinedUsersListView = findViewById(R.id.quarantined_stamped_list_view);
        userListAdapter = new UserListAdapter(this,usersList,userTypeList);
        quarantinedUsersListView.setAdapter(userListAdapter);

        //fetching students
        DatabaseReference studentsDatabaseReference = dbRef.child("Students");
        studentsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentName ;
                    try {
                        studentName =(String)studentSnapshot.child("Name").getValue();
                    } catch (NullPointerException e) {
                        studentName = studentSnapshot.getKey();
                        e.printStackTrace();
                    }
                    String quarantinedStatus = studentSnapshot.child("Quarantined in lockdown").getValue(String.class);

                    if(quarantinedStatus.equalsIgnoreCase("yes")) {
//                        String displayName = studentName + " (\\033[3mStudent\\033[0m)";
                        usersList.add(studentName);
                        userTypeList.add("Students");
                        Log.i(TAG, "onDataChange: Student Quarantined in lockdown: " +quarantinedStatus +":"+ studentName);
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
                    String facultyName = null;
                    try {
                        facultyName = facultySnapshot.child("Name").getValue(String.class);
                    } catch (NullPointerException e) {
                        facultyName = facultySnapshot.getKey();
                        e.printStackTrace();
                    }
                    String quarantinedStatus = facultySnapshot.child("Quarantined in lockdown").getValue(String.class);
                    if(quarantinedStatus.equalsIgnoreCase("yes")) {
//                        String displayName = facultyName + " (\\033[3mFaculty\\033[0m)";
                        usersList.add(facultyName);
                        userTypeList.add("Faculty");
                        Log.i(TAG, "onDataChange: Faculty Quarantined in lockdown " +quarantinedStatus +":"+ facultyName);
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
       /* DatabaseReference nonTeachingDatabaseReference = dbRef.child("Non_teaching");
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
                    String quarantinedStatus;
                    try {
                        quarantinedStatus = nonTeachingSnapshot.child("Quarantined in lockdown").getValue(String.class);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        continue;
                    }


                    if(quarantinedStatus.equalsIgnoreCase("yes")) {
                        usersList.add(nonTeachingStaffName);
                        userTypeList.add("Non_teaching");
                        Log.i(TAG, "onDataChange: Non-teaching staff Quarantined in lockdown: " +quarantinedStatus +":"+ nonTeachingStaffName);
//                        simpleListAdapter.clear();
                        simpleListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });*/


        //fetching outsiders
       /* DatabaseReference outsiderDatabaseReference = dbRef.child("Outsiders");
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
                    String quarantinedStatus;
                    try {
                        quarantinedStatus = outsiderSnapshot.child("Quarantined in lockdown").getValue(String.class);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        continue;
                    }


                    if(quarantinedStatus.equalsIgnoreCase("yes")) {
                        usersList.add(outsiderName);
                        userTypeList.add("Outsiders");
                        Log.i(TAG, "onDataChange: Outsider Quarantined in lockdown " +quarantinedStatus +":"+ outsiderName);
                        simpleListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });*/

        progressBar.setVisibility(View.GONE);

        quarantinedUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = usersList.get(position);
                String userType = userTypeList.get(position);
                Intent intent = new Intent(QuarantinedStampedActivity.this,UserDetailsActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userType",userType);
                startActivity(intent);
            }
        });

        usersList.clear();
        userListAdapter.clear();
    }
}