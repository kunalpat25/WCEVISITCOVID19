package com.wce.wcevisitcovid19;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.models.Admin;
import com.wce.wcevisitcovid19.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    EditText inputEmail;
    EditText inputPassword;
    Button signInBtn;
    CheckBox tncCheckBox;
    TextView tncTextView;

    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference adminDatabaseReference;

    private FirebaseAuth auth;
    Utils utils ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        utils = new Utils();
        adminDatabaseReference = firebaseDatabase.getReference("Users");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("WCEVISITCOVID19", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        progressBar.setVisibility(View.GONE);

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        signInBtn = findViewById(R.id.btn_login);
        tncCheckBox = findViewById(R.id.agree_tnc_checkbox);
        tncTextView = findViewById(R.id.agree_terms_text_view);
        tncTextView.setMovementMethod(LinkMovementMethod.getInstance());

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (validateCredentials()) {
                        progressBar.setVisibility(View.VISIBLE);
                        final String emailID = inputEmail.getText().toString();
                        final String password = inputPassword.getText().toString();
                        String emailHash = utils.generateHash(emailID);
                        adminDatabaseReference.child(emailHash).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                { // user found in database
                                    Boolean isAdmin = snapshot.child("Admin").getValue(Boolean.class);
                                    if(isAdmin)
                                    { // user is admin
                                        try{
                                            auth.signInWithEmailAndPassword(emailID,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                                        progressBar.setVisibility(View.GONE);
                                                    } else {
                                                        //Login successful
                                                        editor.putString("loginStatus", "loggedIn");
                                                        editor.putString("email", emailID);
                                                        editor.commit();
                                                        //For testing purpose
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(MainActivity.this, "Signed In successfully!", Toast.LENGTH_SHORT).show();
                                                        inputEmail.setText("");
                                                        inputPassword.setText("");

                                                        Intent intent = new Intent(MainActivity.this,AdminActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                        catch (Exception ex)
                                        {
                                            // error signing in
                                            ex.printStackTrace();
                                        }
                                    }
                                    else
                                    { // user is not admin
                                        inputEmail.setText("");
                                        inputPassword.setText("");
                                        Toast.makeText(MainActivity.this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                                else
                                { // user not found in database
                                    inputEmail.setText("");
                                    inputPassword.setText("");
                                    Toast.makeText(MainActivity.this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error", error.getMessage());
                            }
                        });
                    }
                    // else validation failed
            }
        });
    }

    private boolean validateCredentials() {
        boolean flag = false;
        final String emailID = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        final boolean isAgree = tncCheckBox.isChecked();
        if (emailID.matches("") || password.matches("") || !isAgree) {
            if(!isAgree) {
                Toast.makeText(this, "Please accept Privacy Policy to continue", Toast.LENGTH_SHORT).show();
            } else {
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
        }
        return loginStatus;
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        boolean loginStatus = checkUserLoginStatus(getApplicationContext());
        FirebaseUser currentUser = auth.getCurrentUser();

        // check if signed in
        if(currentUser != null && loginStatus)
        {
            // start AdminActivity
            Intent intent = new Intent(MainActivity.this,AdminActivity.class);
            progressBar.setVisibility(View.GONE);
            startActivity(intent);
            finish();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}