package com.wce.wcevisitcovid19;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.adapters.SeniorCitizensListAdapter;
import com.wce.wcevisitcovid19.adapters.UserListAdapter;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;

public class SeniorCitizensListActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef_Faculty = database.getReference("Faculty");
    private DatabaseReference dbRef_Non_teaching = database.getReference("Non_teaching");
    private DatabaseReference dbRef_Outsiders = database.getReference("Outsiders");
    private static final String TAG = "SeniorCitizensList";
    ArrayList<String> seniorCitizensList = new ArrayList<>();
    ArrayList<String> userTypeList = new ArrayList<>();
    ArrayList<String> userIdList = new ArrayList<>();
    UserListAdapter userListAdapter;
    Button logoutBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_citizens);

        progressBar = findViewById(R.id.progressBar);

        final ListView seniorCitizensListView = findViewById(R.id.seniorCitizensListView);
        userListAdapter = new UserListAdapter(this,seniorCitizensList,userTypeList);
        seniorCitizensListView.setAdapter(userListAdapter);

        logoutBtn = findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginStatus =  new MainActivity().checkUserLoginStatus(getApplicationContext());
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("WCEVISITCOVID19", 0);
                SharedPreferences.Editor editor = preferences.edit();
                if (loginStatus) {
                    editor.clear();
                    editor.apply();
                    Toast.makeText(SeniorCitizensListActivity.this, "You have been signed out successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SeniorCitizensListActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(SeniorCitizensListActivity.this, "Please Sign in first!", Toast.LENGTH_SHORT).show();
            }
        });

        final int year = Calendar.getInstance().get(Calendar.YEAR);
        final int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        final int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        dbRef_Faculty.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String[] Birth_Date;
                    try {
                        Birth_Date = ((String) postSnapshot.child("Birth date").getValue()).split("/");
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    LocalDate today=LocalDate.of(year,month,date);
                    LocalDate birth_date=LocalDate.of(Integer.parseInt(Birth_Date[2]),Integer.parseInt(Birth_Date[1]),Integer.parseInt(Birth_Date[0]));

                    int age= Period.between(birth_date,today).getYears();

                    if(age>=65) {
                        String facultyName = postSnapshot.getKey();
                        String facultyId = postSnapshot.getKey();
                        userIdList.add(facultyId);
                        seniorCitizensList.add(facultyName);
                        userTypeList.add("Faculty");
                        userListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        dbRef_Non_teaching.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String[] Birth_Date=null;
                    try {
                        Birth_Date = ((String) postSnapshot.child("Birth date").getValue()).split("/");
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    LocalDate today=LocalDate.of(year,month,date);
                    LocalDate birth_date=LocalDate.of(Integer.parseInt(Birth_Date[2]),Integer.parseInt(Birth_Date[1]),Integer.parseInt(Birth_Date[0]));

                    int age= Period.between(birth_date,today).getYears();

                    if(age>=65) {
                        String nonTeachingStaffName = postSnapshot.getKey();

                        seniorCitizensList.add(nonTeachingStaffName);
                        userTypeList.add("Non_teaching");
                        userListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        dbRef_Outsiders.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String[] Birth_Date=null;
                    try {
                        Birth_Date = ((String) postSnapshot.child("Birth date").getValue()).split("/");
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    LocalDate today=LocalDate.of(year,month,date);
                    LocalDate birth_date=LocalDate.of(Integer.parseInt(Birth_Date[2]),Integer.parseInt(Birth_Date[1]),Integer.parseInt(Birth_Date[0]));

                    int age= Period.between(birth_date,today).getYears();

                    if(age>=65) {
                        String outsiderName = postSnapshot.getKey();
//                        String displayName = outsiderName + " (\\033[3mVisitor\\033[0m)";
                        seniorCitizensList.add(outsiderName);
                        userTypeList.add("Outsiders");
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

        seniorCitizensListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = seniorCitizensList.get(position);
                String userType = userTypeList.get(position);
                Intent intent = new Intent(SeniorCitizensListActivity.this,UserDetailsActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userType",userType);
                startActivity(intent);
            }
        });

        userListAdapter.clear();
        seniorCitizensList.clear();
    }
}