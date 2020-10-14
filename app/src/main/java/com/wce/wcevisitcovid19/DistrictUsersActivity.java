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

public class DistrictUsersActivity extends AppCompatActivity {

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
        final String districtName = intent.getStringExtra("district");
        Log.i(TAG, "onCreate: districtName from intent: "+districtName);
        districtTextView.setText(districtName);

        //fetching students
        DatabaseReference studentsDatabaseReference = dbRef.child("Students");
        studentsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {

                    String studentName,studentId;
                    try {
                        Log.i(TAG, "onDataChange: StudentName: "+studentSnapshot.child("Name").getValue(String.class));
                        studentName = studentSnapshot.child("Name").getValue(String.class);
                    } catch (NullPointerException e) {
                        studentName = studentSnapshot.getKey();
                        e.printStackTrace();
                    }
                    String district="default";
                    try {
                        district = studentSnapshot.child("District").getValue(String.class);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onDataChange: district: "+ district,e );
                        continue;
                    }
                    studentId = studentSnapshot.getKey();
                    if(districtName.equals(district)) {
                        usersList.add(studentName);
                        userTypeList.add("Students");
                        userIdList.add(studentId);
                        Log.i(TAG, "onDataChange: Student in district: " +district +":"+ studentName);
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
                    String facultyName,facultyId;
                    try {
                        Log.i(TAG, "onDataChange: FacultyName: "+facultySnapshot.child("Name").getValue(String.class));

                        facultyName = facultySnapshot.child("Name").getValue(String.class);
                    } catch (Exception e) {
                        facultyName = facultySnapshot.getKey();
                        e.printStackTrace();
                    }
                    String district = facultySnapshot.child("District").getValue(String.class);
                    facultyId = facultySnapshot.getKey();
                    if(district.equalsIgnoreCase(districtName)) {
//                        String displayName = facultyName + " (\\033[3mFaculty\\033[0m)";
                        usersList.add(facultyName);
                        userTypeList.add("Faculty");
                        userIdList.add(facultyId);
                        Log.i(TAG, "onDataChange: Faculty in district: " +district +":"+ facultyName);
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
                    String nonTeachingStaffName,nonTeachingStaffId;
                    try {
                        Log.i(TAG, "onDataChange: NonteachingstaffName: "+nonTeachingSnapshot.child("Name").getValue(String.class));

                        nonTeachingStaffName = nonTeachingSnapshot.child("Name").getValue(String.class);
                    }
                    catch (NullPointerException e)
                    {
                        nonTeachingStaffName = nonTeachingSnapshot.getKey();
                    }
                    String district;
                    try {
                        district = nonTeachingSnapshot.child("District").getValue(String.class);
                    }
                    catch (NullPointerException e)
                    {
                        e.printStackTrace();
                        continue;
                    }

                    nonTeachingStaffId = nonTeachingSnapshot.getKey();
                    if(district.equals(districtName)) {
//                        String displayName = nonTeachingStaffName + " (\\033[3mNon-Teaching Staff\\033[0m)";
                        usersList.add(nonTeachingStaffName);
                        userTypeList.add("Non_teaching");
                        userIdList.add(nonTeachingStaffId);
                        Log.i(TAG, "onDataChange: Non-teaching staff in district: " +district +":"+ nonTeachingStaffName);
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
                    String outsiderName,district="",outsiderId;
                    try
                    {
                        district = outsiderSnapshot.child("District").getValue(String.class);
                    }
                    catch (NullPointerException e)
                    {
                        district="";
                        e.printStackTrace();
                        continue;
                    }

                    if(districtName.equals(district)) {
                        outsiderName = outsiderSnapshot.child("Name").getValue(String.class);
                        outsiderId = outsiderSnapshot.getKey();
                        usersList.add(outsiderName);
                        userTypeList.add("Outsiders");
                        userIdList.add(outsiderId);
                        Log.i(TAG, "onDataChange: Outsider in district " +district +":"+ outsiderName);
                        userListAdapter.notifyDataSetChanged();
                    }
//                    try {
//                        Log.i(TAG, "onDataChange: OutsiderName: "+outsiderSnapshot.child("Name").getValue(String.class));
//                        outsiderName = outsiderSnapshot.child("Name").getValue(String.class);
//                    }
//                    catch (NullPointerException e)
//                    {
//                        Log.i(TAG, "onDataChange: OutsiderName: "+outsiderSnapshot.getKey());
//                        outsiderName = outsiderSnapshot.getKey();
//                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        progressBar.setVisibility(View.GONE);

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

        userListAdapter.clear();
        usersList.clear();
    }
}