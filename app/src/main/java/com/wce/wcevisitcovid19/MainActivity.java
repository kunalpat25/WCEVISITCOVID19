package com.wce.wcevisitcovid19;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.wce.wcevisitcovid19.models.Faculty;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    EditText inputEmail;
    EditText inputPassword;
    Button signInBtn;
    Button logoutBtn;
    Button registerBtn;
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference facultyDatabaseReference;
    Faculty faculty;
    List<Faculty> facultyList = new ArrayList<>();

    private FirebaseAuth auth;
    boolean isFaculty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        facultyDatabaseReference = firebaseDatabase.getReference("Faculty");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("WCEVISITCOVID19", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if(checkUserLoginStatus(this))
        {
            facultyDatabaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Faculty faculty = postSnapshot.getValue(Faculty.class);
                        facultyList.add(faculty);
                    }
                    boolean flag = true;

                    //checking user is a faculty or not
                    for (int i = 0; i < facultyList.size(); i++) {
                        Log.i(TAG, "onDataChange: Faculty Email: " + facultyList.get(i).getEmail());
                        String facultyEmail = facultyList.get(i).getEmail();
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("WCEVISITCOVID19", 0);
                        String emailID = preferences.getString("email", null);
                        if (emailID.equalsIgnoreCase(facultyEmail)) {
                            Log.i(TAG, "onDataChange: Entered Email: " + emailID);
                            Log.i(TAG, "onDataChange: Now breaking loop..got faculty");
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            startActivity(intent);
                            flag = false;
                            finish();
                        }
                    }
                    //if not login student
                    if(flag)
                    {
                        Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error", error.getMessage());
                }
            });
        }

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        signInBtn = findViewById(R.id.btn_login);
        logoutBtn = findViewById(R.id.btn_logout);
        registerBtn = findViewById(R.id.btn_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateCredentials()) {
                    final String emailID = inputEmail.getText().toString();
                    final String password = inputPassword.getText().toString();
                    auth.createUserWithEmailAndPassword(emailID, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(MainActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Registration failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                                        inputEmail.setText("");
                                        inputPassword.setText("");
                                    }
                                }
                            });

                }
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginStatus = checkUserLoginStatus(getApplicationContext());
                if (loginStatus) {
                    editor.clear();
                    editor.apply();
                    Toast.makeText(MainActivity.this, "You have been signed out successfully!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MainActivity.this, "Please Sign in first!", Toast.LENGTH_SHORT).show();
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
                        progressBar.setVisibility(View.INVISIBLE);
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
                                            progressBar.setVisibility(View.GONE);
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
                                                editor.apply();
                                                //For demo purpose
                                                Toast.makeText(MainActivity.this, "Signed In successfully!", Toast.LENGTH_SHORT).show();
                                                inputEmail.setText("");
                                                inputPassword.setText("");
                                                //Handle next Activity
                                                setIsFaculty(false);
                                                facultyDatabaseReference.addValueEventListener(new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                            Faculty faculty = postSnapshot.getValue(Faculty.class);
                                                            facultyList.add(faculty);
                                                        }
                                                        boolean flag = true;

                                                        //checking user is a faculty or not
                                                        for (int i = 0; i < facultyList.size(); i++) {
                                                            Log.i(TAG, "onDataChange: Faculty Email: " + facultyList.get(i).getEmail());
                                                            String facultyEmail = facultyList.get(i).getEmail();
                                                            if (emailID.equalsIgnoreCase(facultyEmail)) {
//                                                                setIsFaculty(true);
                                                                Log.i(TAG, "onDataChange: Entered Email: " + emailID);
                                                                Log.i(TAG, "onDataChange: Now breaking loop..got faculty");
                                                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                                                startActivity(intent);
                                                                flag = false;
                                                                finish();
                                                                break;
                                                            }
                                                        }
                                                        //if not login student
                                                        if(flag)
                                                        {
                                                            Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.e("error", error.getMessage());
                                                    }
                                                });
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
        if (emailID.matches("") || password.matches("")) {
            Toast.makeText(MainActivity.this, "Please input credentials", Toast.LENGTH_SHORT).show();
            if (TextUtils.isEmpty(emailID)) {
                inputEmail.requestFocus();
            } else {
                inputPassword.requestFocus();
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

    public void setIsFaculty(boolean status)
    {
        this.isFaculty = status;
    }
    public boolean getIsFaculty()
    {
        return this.isFaculty;
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}