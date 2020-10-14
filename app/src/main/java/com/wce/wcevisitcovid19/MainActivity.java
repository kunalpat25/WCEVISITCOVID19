package com.wce.wcevisitcovid19;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.models.Admin;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    EditText inputEmail;
    EditText inputPassword;
    Button signInBtn;

    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
//    private DatabaseReference facultyDatabaseReference;
    private DatabaseReference adminDatabaseReference;
    Admin admin;
    Spinner userTypeSpinner;
//    Faculty faculty;
//    List<Faculty> facultyList = new ArrayList<>();
    List<Admin> adminList = new ArrayList<>();

    private FirebaseAuth auth;
    boolean isFaculty;
    String userType;

    boolean isNormalUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        new NormalUserActivity().scheduleAlarm();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        adminDatabaseReference = firebaseDatabase.getReference("Admin");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("WCEVISITCOVID19", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();


        //checking already logged in user
        if(checkUserLoginStatus(this))
        {
            String typeOfUser = getUserType(this);
            if(typeOfUser.equals("Admin"))

            adminDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Admin admin = postSnapshot.getValue(Admin.class);
                        adminList.add(admin);
                    }
                    boolean flag = true;

                    //checking user is an admin or not
                    for (int i = 0; i < adminList.size(); i++) {
                        Log.i(TAG, "onDataChange: Admin Email: " + adminList.get(i).getEmail());
                        String adminEmail = adminList.get(i).getEmail();
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("WCEVISITCOVID19", 0);
                        String emailID = preferences.getString("email", null);
                        if (emailID.equalsIgnoreCase(adminEmail))
                        {
                            Log.i(TAG, "onDataChange: Entered Email: " + emailID);
                            Log.i(TAG, "onDataChange: Now breaking loop..got admin");
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);
                            flag = false;
                            finish();
                        }
                    }
                    //if not admin, login normal user
                    if(flag)
                    {
                        Intent intent = new Intent(MainActivity.this, NormalUserActivity.class);
                        progressBar.setVisibility(View.GONE);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error", error.getMessage());
                }
            });
        }
        else
            progressBar.setVisibility(View.GONE);


        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        signInBtn = findViewById(R.id.btn_login);
        userTypeSpinner = findViewById(R.id.user_type_spinner);
        final List<String> userTypesList  = new ArrayList<String>();
        userTypesList.add("Select user type");
        userTypesList.add("Admin");
        userTypesList.add("Faculty");
        userTypesList.add("Non Teaching Staff");
        userTypesList.add("Student");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, userTypesList);
        userTypeSpinner.setAdapter(adapter);
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = userTypesList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean loginStatus = checkUserLoginStatus(getApplicationContext());
                if (loginStatus) {
//                    Toast.makeText(MainActivity.this, "Already logged in..", Toast.LENGTH_SHORT).show();
                } else {
                    if (validateCredentials()) {
                        progressBar.setVisibility(View.VISIBLE);
                        final String emailID = inputEmail.getText().toString();
                        final String password = inputPassword.getText().toString();
                        try {
                            auth.signInWithEmailAndPassword(emailID, password)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            // If sign in fails, display a message to the user. If sign in succeeds
                                            // the auth state listener will be notified and logic to handle the
                                            // signed in user can be handled in the listener.

                                            if (!task.isSuccessful()) {
                                                // there was an error
                                                if (password.length() < 6) {
                                                    inputPassword.setError(getString(R.string.minimum_password));
                                                } else {
                                                    Toast.makeText(MainActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                //Login successful
                                                editor.putString("loginStatus", "loggedIn");
                                                editor.putString("email", emailID);
                                                editor.putString("userType", userType);
                                                editor.apply();
                                                //For testing purpose
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(MainActivity.this, "Signed In successfully!", Toast.LENGTH_SHORT).show();
                                                inputEmail.setText("");
                                                inputPassword.setText("");
                                                userTypeSpinner.setSelection(0);
                                                //Handle next Activity

                                                if (userType.equals("Admin"))
                                                {
                                                    adminDatabaseReference.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                Admin admin = postSnapshot.getValue(Admin.class);
                                                                adminList.add(admin);
                                                            }


                                                            //checking user is a admin or not
                                                            for (int i = 0; i < adminList.size(); i++) {
                                                                Log.i(TAG, "onDataChange: Admin Email: " + adminList.get(i).getEmail());
                                                                String adminEmail = adminList.get(i).getEmail();
                                                                if (emailID.equalsIgnoreCase(adminEmail))
                                                                {
                                                                    Log.i(TAG, "onDataChange: Entered Email: " + emailID);
                                                                    Log.i(TAG, "onDataChange: Now breaking loop..got admin");
                                                                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                                                    isNormalUser = false;
                                                                    startActivity(intent);
                                                                    finish();
                                                                    break;
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.e("error", error.getMessage());
                                                        }
                                                    });
                                            }
                                                //if not admin, login student
                                                if(isNormalUser)
                                                {
                                                    Intent intent = new Intent(MainActivity.this, NormalUserActivity.class);
                                                    intent.putExtra("userType",userType);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//
                    }
                }
            }
        });
    }

    private boolean validateCredentials() {
        boolean flag = false;
        final String emailID = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        if (emailID.matches("") || password.matches("") || userType.equals("Select user type")) {
            if (userType.equals("Select user type"))
            {
                Toast.makeText(this, "Please select User Type", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Please input credentials", Toast.LENGTH_SHORT).show();
                if (TextUtils.isEmpty(emailID)) {
                    inputEmail.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    inputPassword.requestFocus();
                }
            }
        } else
            flag = true;
        return flag;
    }

    public boolean checkUserLoginStatus(Context context) {
        boolean loginStatus = false;
        SharedPreferences preferences = context.getSharedPreferences("WCEVISITCOVID19", 0);
        String status = preferences.getString("loginStatus", null);

        String email = preferences.getString("email", null);
        if (email == null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
        }
        if (status != null && status.equals("loggedIn")) {
            loginStatus = true;
//            Toast.makeText(context, "Already Signed in: " + email, Toast.LENGTH_SHORT).show();
        }
        return loginStatus;
    }

    public String getUserType(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("WCEVISITCOVID19", 0);
        return preferences.getString("userType", null);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        progressBar.setVisibility(View.GONE);
    }
}