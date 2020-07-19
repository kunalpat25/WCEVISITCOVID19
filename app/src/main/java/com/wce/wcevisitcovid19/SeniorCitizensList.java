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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;

public class SeniorCitizensList extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef_Faculty = database.getReference("Faculty");
    private DatabaseReference dbRef_Non_teaching = database.getReference("Non_teaching");
    private DatabaseReference dbRef_Outsiders = database.getReference("Outsiders");
    private static final String TAG = "SeniorCitizensList";
    ArrayList<String> seniorCitizensList = new ArrayList<>();
    SeniorCitizensListAdapter seniorCitizensListAdapter;
    Button logoutBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_citizens);

        final ListView seniorCitizensListView = findViewById(R.id.seniorCitizensListView);

        seniorCitizensListAdapter = new SeniorCitizensListAdapter(this,seniorCitizensList);

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
                    Toast.makeText(SeniorCitizensList.this, "You have been signed out successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SeniorCitizensList.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(SeniorCitizensList.this, "Please Sign in first!", Toast.LENGTH_SHORT).show();
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

                    String [] Birth_Date = ((String)postSnapshot.child("Birth date").getValue()).split("/");

                    LocalDate today=LocalDate.of(year,month,date);
                    LocalDate birth_date=LocalDate.of(Integer.parseInt(Birth_Date[2]),Integer.parseInt(Birth_Date[1]),Integer.parseInt(Birth_Date[0]));

                    int age= Period.between(birth_date,today).getYears();

                    if(age>=65) {
                        seniorCitizensList.add(postSnapshot.getKey());
                    }
                    seniorCitizensListView.setAdapter(seniorCitizensListAdapter);
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

                    String [] Birth_Date = ((String)postSnapshot.child("Birth date").getValue()).split("/");

                    LocalDate today=LocalDate.of(year,month,date);
                    LocalDate birth_date=LocalDate.of(Integer.parseInt(Birth_Date[2]),Integer.parseInt(Birth_Date[1]),Integer.parseInt(Birth_Date[0]));

                    int age= Period.between(birth_date,today).getYears();

                    if(age>=65) {
                        seniorCitizensList.add(postSnapshot.getKey());
                    }
                    seniorCitizensListView.setAdapter(seniorCitizensListAdapter);
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

                    String [] Birth_Date = ((String)postSnapshot.child("Birth date").getValue()).split("/");

                    LocalDate today=LocalDate.of(year,month,date);
                    LocalDate birth_date=LocalDate.of(Integer.parseInt(Birth_Date[2]),Integer.parseInt(Birth_Date[1]),Integer.parseInt(Birth_Date[0]));

                    int age= Period.between(birth_date,today).getYears();

                    if(age>=65) {
                        seniorCitizensList.add(postSnapshot.getKey());
                    }
                    seniorCitizensListView.setAdapter(seniorCitizensListAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });
    }
}