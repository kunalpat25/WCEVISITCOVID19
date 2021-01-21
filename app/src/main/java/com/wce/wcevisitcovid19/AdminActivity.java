package com.wce.wcevisitcovid19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class AdminActivity extends AppCompatActivity
{
    private static final String TAG = "AdminActivity";
    Button logoutBtn;
    ArrayAdapter<String> adapter;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        final ListView menuListView = findViewById(R.id.menuListView);

        ArrayList<String> menuList = new ArrayList<>();
        menuList.add(getString(R.string.symptoms));
        menuList.add(getString(R.string.district_wise_list));
        menuList.add(getString(R.string.state_wise_list));
        menuList.add("Visitors' List");
        menuList.add(getString(R.string.without_mask));
        menuList.add(getString(R.string.violating_covid19_guidelines));

        adapter= new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                menuList);
        menuListView.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String m = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String d = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        if(m.length() == 1)
            m = "0"+ m;

        if(d.length() == 1)
            d ="0"+ d;

        EditText editText = findViewById(R.id.edit_status_date);
        editText.setHint(d+"/"+ m +"/"+calendar.get(Calendar.YEAR));

        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String activity ="";
                Intent intent;
                switch (position)
                {
                    case 0:
                        activity = "symptoms";
                        intent = new Intent(AdminActivity.this,SymptomsActivity.class);
                        EditText editDate = findViewById(R.id.edit_status_date);
                        String date;
                        String month;
                        String year;
                        if(editDate.getText().length() > 0) {
                            date = editDate.getText().toString().substring(0, 2);
                            month = editDate.getText().toString().substring(3, 5);
                            month = String.valueOf(Integer.parseInt(month) + 1);
                            year = editDate.getText().toString().substring(6);
                            if (month.length() == 1)
                                month = "0" + month;

                            if (date.contains("/"))
                                date = "0" + date.substring(0, 1);
                        }
                        else
                        {
                            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                            year = String.valueOf(calendar.get(Calendar.YEAR));
                            month = String.valueOf(calendar.get(Calendar.MONTH)+1);
                            date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

                            if(month.length() == 1)
                                month = "0"+ month;

                            if(date.length() == 1)
                                date ="0"+ date;

                        }

                        intent.putExtra("year",year);
                        intent.putExtra("month",month);
                        intent.putExtra("date",date);

                        Log.i(TAG, "onCreate: Today's date:"+date+" "+month+" "+year);
                        startActivity(intent);
                        break;
                    case 1:
                        activity = "district";
                        intent = new Intent(AdminActivity.this, DistrictsListActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        activity = "state";
                        intent = new Intent(AdminActivity.this,StatesListActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        activity = "visitors";
                        intent = new Intent(AdminActivity.this,VisitorsListActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        activity = "without_mask";
                        intent = new Intent(AdminActivity.this,WithoutMaskActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        activity = "violating_guidelines";
                        intent = new Intent(AdminActivity.this,ViolatingGuidelinesActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        logoutBtn = findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginStatus = new MainActivity().checkUserLoginStatus(getApplicationContext());
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("WCEVISITCOVID19", 0);
                SharedPreferences.Editor editor = preferences.edit();
                if (loginStatus) {
                    editor.clear();
                    editor.apply();
                    Toast.makeText(AdminActivity.this, "You have been signed out successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(AdminActivity.this, "Please Sign in first!", Toast.LENGTH_SHORT).show();
            }
        });

        final EditText editDate= (EditText) findViewById(R.id.edit_status_date);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String smonth = String.valueOf(month);
                if(smonth.length() == 1)
                    smonth = "0"+ month;

                String date = String.valueOf(dayOfMonth);
                if(date.length() == 1)
                    date = "0"+ date;
                String text = date+"/"+smonth+"/"+year;
                editDate.setText(text);
            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AdminActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}