package com.wce.wcevisitcovid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    Button logoutBtn;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        final ListView menuListView = findViewById(R.id.menuListView);

        List<String> menuList = new ArrayList();
        menuList.add(getString(R.string.symptoms));
        menuList.add(getString(R.string.district_wise_list));
        menuList.add(getString(R.string.state_wise_list));
        menuList.add(getString(R.string.senior_citizens));
        menuList.add(getString(R.string.quarantine_stamped));
        menuList.add(getString(R.string.travelled_from_containment_zone));
        menuList.add(getString(R.string.group_more_than_five));
        menuList.add(getString(R.string.without_mask));
        menuList.add(getString(R.string.violating_covid19_guidelines));

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                menuList);
        menuListView.setAdapter(adapter);

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
                        activity = "senior";
                        intent = new Intent(AdminActivity.this, SeniorCitizensListActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        activity = "quarantine";
                        intent = new Intent(AdminActivity.this,QuarantinedStampedActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        activity = "containment";
                        break;
                    case 6:
                        activity = "group_5+";
                        break;
                    case 7:
                        activity = "without_mask";
                        break;
                    case 8:
                        activity = "violating_guidelines";
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
    }
}