package com.wce.wcevisitcovid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.adapters.VisitorsListAdapter;
import com.wce.wcevisitcovid19.models.Visitor;
import com.wce.wcevisitcovid19.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class VisitorsListActivity extends AppCompatActivity {

    private static final String TAG = "VisitorsListActivity";
    EditText editVisitDate;
    ListView  visitorsListView;
    ArrayList<String> outsiderIdList = new ArrayList<>();
    ArrayList<Visitor> visitorsList = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    VisitorsListAdapter visitorsListAdapter;
    String visitDate;
    TextView countTextView,infoTextView;
    int totalCount, facultyCount, nonTeachingCount, outsiderCount, studentCount;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitors_list);

        getSupportActionBar().setTitle(getString(R.string.visitors));

        visitorsListView = findViewById(R.id.visitorsListView);
        visitorsListAdapter = new VisitorsListAdapter(this,visitorsList);
        visitorsListView.setAdapter(visitorsListAdapter);

        countTextView = findViewById(R.id.display_count_text_view);
        infoTextView = findViewById(R.id.visitor_info_text_view);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String m = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String d = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        if (m.length() == 1)
            m = "0" + m;

        if (d.length() == 1)
            d = "0" + d;

        editVisitDate = findViewById(R.id.edit_visit_date);
        visitDate = d + "/" + m + "/" + calendar.get(Calendar.YEAR);
        editVisitDate.setHint(visitDate);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                visitorsListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                visitorsListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        DateUtils du;
        du = new DateUtils(visitDate);
        String fetchDate = du.extractDate();
        String fetchMonth = du.extractMonth();
        String fetchYear = du.extractYear();

        // fetching outsider
        DatabaseReference selectedOutsidersDatabaseReference;
        selectedOutsidersDatabaseReference = database.getReference("Daily_assessment").child(fetchYear).child(fetchMonth).child(fetchDate).child("Temperature").child("Outsiders");
        selectedOutsidersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    infoTextView.setVisibility(View.GONE);
                    Log.i(TAG, "onDataChange: Found dataSnapshot for "+ visitDate);
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String outsiderId = postSnapshot.getKey();
                        outsiderIdList.add(outsiderId);
                    }
                    outsiderCount = outsiderIdList.size();
                    updateCount();
                    DatabaseReference outsiderDatabaseReference;
                    for (String id : outsiderIdList)
                    {
                        final String finalOutsiderId = id;
                        final String[] outsiderName = new String[1];
                        outsiderDatabaseReference = database.getReference("Outsiders").child(finalOutsiderId).child("Name");
                        outsiderDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {

                                outsiderName[0] = postSnapshot.getValue(String.class);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });
                        DatabaseReference outsiderLocationReference = database.getReference("Outsiders").child(finalOutsiderId).child("Location of visit");
                        outsiderLocationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                final String locationOfVisit = postSnapshot.getValue(String.class).toLowerCase();
                                visitorsList.add(new Visitor(finalOutsiderId, outsiderName[0],"Outsiders",locationOfVisit));
                                visitorsListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });
                    }
                }
                else {
                    outsiderCount = 0;
                    updateCount();
                    Log.i(TAG, "onDataChange: No outsider data found for date " + visitDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        // fetching faculty
        final ArrayList<String> facultyIdList = new ArrayList<>();
        DatabaseReference facultiesDatabaseReference;
        facultiesDatabaseReference = database.getReference("Daily_assessment").child(fetchYear).child(fetchMonth).child(fetchDate).child("Temperature").child("Faculty");
        facultiesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    infoTextView.setVisibility(View.GONE);
                    Log.i(TAG, "onDataChange: Found dataSnapshot for "+ visitDate);
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String outsiderId = postSnapshot.getKey();
                        facultyIdList.add(outsiderId);
                    }
                    facultyCount = facultyIdList.size();
                    updateCount();
                    DatabaseReference selectedFacultyDatabaseReference;
                    for (String id : facultyIdList)
                    {
                        final String finalFacultyId = id;
                        final String[] facultyName = new String[1];
                        selectedFacultyDatabaseReference = database.getReference("Faculty").child(finalFacultyId).child("Name");
                        selectedFacultyDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                facultyName[0] = postSnapshot.getValue(String.class);
                                visitorsList.add(new Visitor(finalFacultyId, facultyName[0],"Faculty","Faculty"));
                                visitorsListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });
                    }
                }
                else {
                    facultyCount = 0;
                    updateCount();
                    Log.i(TAG, "onDataChange: No faculty data found for date " + visitDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        // fetching Non-teaching staff
        final ArrayList<String> nonTeachingIdList = new ArrayList<>();
        DatabaseReference nonTeachingDatabaseReference;
        nonTeachingDatabaseReference = database.getReference("Daily_assessment").child(fetchYear).child(fetchMonth).child(fetchDate).child("Temperature").child("Non_teaching");
        nonTeachingDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    infoTextView.setVisibility(View.GONE);
                    Log.i(TAG, "onDataChange: Found dataSnapshot for "+ visitDate);
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String nonTeachingID = postSnapshot.getKey();
                        nonTeachingIdList.add(nonTeachingID);
                    }
                    nonTeachingCount = nonTeachingIdList.size();
                    updateCount();
                    DatabaseReference selectedNonTeachingDatabaseReference;
                    for (String id : nonTeachingIdList)
                    {
                        final String finalNonTeachingId = id;
                        final String[] nonTeachingName = new String[1];
                        selectedNonTeachingDatabaseReference = database.getReference("Non_teaching").child(finalNonTeachingId).child("Name");
                        selectedNonTeachingDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                nonTeachingName[0] = postSnapshot.getValue(String.class);
                                visitorsList.add(new Visitor(finalNonTeachingId, nonTeachingName[0],"Non_teaching","Non Teaching staff"));
                                visitorsListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });
                    }
                }
                else {
                    nonTeachingCount = 0;
                    updateCount();
                    Log.i(TAG, "onDataChange: No non teaching data found for date " + visitDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        // fetching Students
        final ArrayList<String> studentsIdList = new ArrayList<>();
        DatabaseReference studentDatabaseReference;
        studentDatabaseReference = database.getReference("Daily_assessment").child(fetchYear).child(fetchMonth).child(fetchDate).child("Temperature").child("Students");
        studentDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    infoTextView.setVisibility(View.GONE);
                    Log.i(TAG, "onDataChange: Found dataSnapshot for "+ visitDate);
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String studentID = postSnapshot.getKey();
                        studentsIdList.add(studentID);
                    }
                    studentCount = studentsIdList.size();
                    updateCount();
                    DatabaseReference selectedStudentDatabaseReference;
                    for (String id : studentsIdList)
                    {
                        final String finalStudentId = id;
                        final String[] studentName = new String[1];
                        selectedStudentDatabaseReference = database.getReference("Students").child(finalStudentId).child("Name");
                        selectedStudentDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                studentName[0] = postSnapshot.getValue(String.class);
                                visitorsList.add(new Visitor(finalStudentId, studentName[0],"Students","Student"));
                                visitorsListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });
                    }
                }
                else {
                    studentCount = 0;
                    updateCount();
                    Log.i(TAG, "onDataChange: No student data found for date " + visitDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error", error.getMessage());
            }
        });

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                month+=1;
                String smonth = String.valueOf(month);
                if(smonth.length() == 1)
                    smonth = "0"+ month;

                String date = String.valueOf(dayOfMonth);
                if(date.length() == 1)
                    date = "0"+ date;
                visitDate = date+"/"+smonth+"/"+year;
                editVisitDate.setText(visitDate);
            }
        };

        editVisitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(VisitorsListActivity.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editVisitDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                outsiderIdList.clear();
                visitorsList.clear();
                visitorsListAdapter.notifyDataSetChanged();

                //trying to reduce time and data to fetch from firebase more
                DateUtils du;
                du = new DateUtils(visitDate);
                String fetchDate = du.extractDate();
                String fetchMonth = du.extractMonth();
                String fetchYear = du.extractYear();

                // fetching outsider
                DatabaseReference selectedOutsidersDatabaseReference;
                selectedOutsidersDatabaseReference = database.getReference("Daily_assessment").child(fetchYear).child(fetchMonth).child(fetchDate).child("Temperature").child("Outsiders");
                selectedOutsidersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            infoTextView.setVisibility(View.GONE);
                            Log.i(TAG, "onDataChange: Found dataSnapshot for "+ visitDate);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String outsiderId = postSnapshot.getKey();
                                outsiderIdList.add(outsiderId);
                            }
                            outsiderCount = outsiderIdList.size();
                            updateCount();
                            DatabaseReference outsiderDatabaseReference;
                            for (String id : outsiderIdList)
                            {
                                final String finalOutsiderId = id;
                                final String[] outsiderName = new String[1];
                                outsiderDatabaseReference = database.getReference("Outsiders").child(finalOutsiderId).child("Name");
                                outsiderDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot postSnapshot) {

                                        outsiderName[0] = postSnapshot.getValue(String.class);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("error", error.getMessage());
                                    }
                                });
                                DatabaseReference outsiderLocationReference = database.getReference("Outsiders").child(finalOutsiderId).child("Location of visit");
                                outsiderLocationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                        final String locationOfVisit = postSnapshot.getValue(String.class).toLowerCase();
                                        visitorsList.add(new Visitor(finalOutsiderId, outsiderName[0],"Outsiders",locationOfVisit));
                                        visitorsListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("error", error.getMessage());
                                    }
                                });
                            }
                        }
                        else {
                            outsiderCount = 0;
                            updateCount();
                            Log.i(TAG, "onDataChange: No outsider data found for date " + visitDate);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error", error.getMessage());
                    }
                });

                // fetching faculty
                final ArrayList<String> facultyIdList = new ArrayList<>();
                DatabaseReference facultiesDatabaseReference;
                facultiesDatabaseReference = database.getReference("Daily_assessment").child(fetchYear).child(fetchMonth).child(fetchDate).child("Temperature").child("Faculty");
                facultiesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            infoTextView.setVisibility(View.GONE);
                            Log.i(TAG, "onDataChange: Found dataSnapshot for "+ visitDate);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String outsiderId = postSnapshot.getKey();
                                facultyIdList.add(outsiderId);
                            }
                            facultyCount = facultyIdList.size();
                            updateCount();
                            DatabaseReference selectedFacultyDatabaseReference;
                            for (String id : facultyIdList)
                            {
                                final String finalFacultyId = id;
                                final String[] facultyName = new String[1];
                                selectedFacultyDatabaseReference = database.getReference("Faculty").child(finalFacultyId).child("Name");
                                selectedFacultyDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                        facultyName[0] = postSnapshot.getValue(String.class);
                                        visitorsList.add(new Visitor(finalFacultyId, facultyName[0],"Faculty","Faculty"));
                                        visitorsListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("error", error.getMessage());
                                    }
                                });
                            }
                        }
                        else {
                            facultyCount = 0;
                            updateCount();
                            Log.i(TAG, "onDataChange: No faculty data found for date " + visitDate);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error", error.getMessage());
                    }
                });

                // fetching Non-teaching staff
                final ArrayList<String> nonTeachingIdList = new ArrayList<>();
                DatabaseReference nonTeachingDatabaseReference;
                nonTeachingDatabaseReference = database.getReference("Daily_assessment").child(fetchYear).child(fetchMonth).child(fetchDate).child("Temperature").child("Non_teaching");
                nonTeachingDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            infoTextView.setVisibility(View.GONE);
                            Log.i(TAG, "onDataChange: Found dataSnapshot for "+ visitDate);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String nonTeachingID = postSnapshot.getKey();
                                nonTeachingIdList.add(nonTeachingID);
                            }
                            nonTeachingCount = nonTeachingIdList.size();
                            updateCount();
                            DatabaseReference selectedNonTeachingDatabaseReference;
                            for (String id : nonTeachingIdList)
                            {
                                final String finalNonTeachingId = id;
                                final String[] nonTeachingName = new String[1];
                                selectedNonTeachingDatabaseReference = database.getReference("Non_teaching").child(finalNonTeachingId).child("Name");
                                selectedNonTeachingDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                        nonTeachingName[0] = postSnapshot.getValue(String.class);
                                        visitorsList.add(new Visitor(finalNonTeachingId, nonTeachingName[0],"Non_teaching","Non Teaching staff"));
                                        visitorsListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("error", error.getMessage());
                                    }
                                });
                            }
                        }
                        else {
                            nonTeachingCount = 0;
                            updateCount();
                            Log.i(TAG, "onDataChange: No non teaching data found for date " + visitDate);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error", error.getMessage());
                    }
                });

                // fetching Students
                final ArrayList<String> studentsIdList = new ArrayList<>();
                DatabaseReference studentDatabaseReference;
                studentDatabaseReference = database.getReference("Daily_assessment").child(fetchYear).child(fetchMonth).child(fetchDate).child("Temperature").child("Students");
                studentDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            infoTextView.setVisibility(View.GONE);
                            Log.i(TAG, "onDataChange: Found dataSnapshot for "+ visitDate);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String studentID = postSnapshot.getKey();
                                studentsIdList.add(studentID);
                            }
                            studentCount = studentsIdList.size();
                            updateCount();
                            DatabaseReference selectedStudentDatabaseReference;
                            for (String id : studentsIdList)
                            {
                                final String finalStudentId = id;
                                final String[] studentName = new String[1];
                                selectedStudentDatabaseReference = database.getReference("Students").child(finalStudentId).child("Name");
                                selectedStudentDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                        studentName[0] = postSnapshot.getValue(String.class);
                                        visitorsList.add(new Visitor(finalStudentId, studentName[0],"Students","Student"));
                                        visitorsListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("error", error.getMessage());
                                    }
                                });
                            }
                        }
                        else {
                            studentCount = 0;
                            updateCount();
                            Log.i(TAG, "onDataChange: No student data found for date " + visitDate);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error", error.getMessage());
                    }
                });

            }
        });

        visitorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Visitor> filteredList = visitorsListAdapter.getFilteredVisitorsList();
                String userName,userType,userId;
                try {
                    userName = filteredList.get(position).getName();
                    userType = filteredList.get(position).getType();
                    userId = filteredList.get(position).getId();
                }
                catch (Exception ex)
                { // if a name is selected which is not available in filteredList, show it from visitorsList
                    userName = visitorsList.get(position).getName();
                    userType = visitorsList.get(position).getType();
                    userId = visitorsList.get(position).getId();
                }
                Intent intent = new Intent(VisitorsListActivity.this,UserDetailsActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userType",userType);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        outsiderIdList.clear();
        visitorsList.clear();
    }

    public void updateCount()
    {
        totalCount = outsiderCount + facultyCount + nonTeachingCount + studentCount;
        countTextView.setText(String.valueOf(totalCount));
        if(totalCount == 0)
            infoTextView.setVisibility(View.VISIBLE);
    }
}