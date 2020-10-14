package com.wce.wcevisitcovid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.models.UserLocation;
import com.wce.wcevisitcovid19.utils.AlarmReceiver;
import com.wce.wcevisitcovid19.utils.DateUtils;

import java.util.HashMap;

public class NormalUserActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "NormalUserActivity";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "locationUpdate";
    private static boolean requestingLocationUpdates = false;
    Button logoutBtn;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager locationManager;
    UserLocation userLocation;
    Location mCurrentLocation;
    double latitude;
    double longitude;
    FirebaseUser currentUser;
    String userEmail,userPRN,userType;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    String bodyAche, chestPain, breathingDifficulty, dryCough, fever, headache, runnyNose, soreThroat,tiredness, none;
    String contactWithPatient, travelledContainmentZone, affectedRegion;
    String question,symptom;
    ProgressBar progressBar;
    TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);

        final Intent intent = getIntent();
        userType = intent.getStringExtra("userType");
        Log.i(TAG, "onCreate: UserType: "+userType);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        switch (userType)
        {
            case "Student":
                userType="Students";
                DatabaseReference studentDatabaseReference = ref.child("Students");
                studentDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                                                                                    String email = studentSnapshot.child("Email").getValue(String.class);
                                                                                    Log.i(TAG, "onDataChange: student email : " + email);
                                                                                    if (email.equalsIgnoreCase(userEmail)) {
                                                                                        userPRN = studentSnapshot.child("PRN").getValue(String.class);
                                                                                        Log.i(TAG, "onDataChange: matched student PRN : " + userPRN);
                                                                                        break;
                                                                                    }
                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                                Log.e(TAG, error.getMessage());
                                                                            }
                                                                        });
                break;
            case "Faculty":
                DatabaseReference facultyDatabaseReference = ref.child("Faculty");
                facultyDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                            String email = studentSnapshot.child("Email").getValue(String.class);
                            Log.i(TAG, "onDataChange: student email : " + email);
                            if (email.equalsIgnoreCase(userEmail)) {
                                userPRN = studentSnapshot.child("Employee ID").getValue(String.class);
                                Log.i(TAG, "onDataChange: matched student PRN : " + userPRN);
                                break;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, error.getMessage());
                    }
                });
                break;
            case "Non Teaching Staff":
                userType = "Non_teaching";
            DatabaseReference non_teachingDatabaseReference = ref.child("Non_teaching");
            non_teachingDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                        String email = studentSnapshot.child("Email").getValue(String.class);
                        Log.i(TAG, "onDataChange: student email : " + email);
                        if (email.equalsIgnoreCase(userEmail)) {
                            userPRN = studentSnapshot.child("Employee ID").getValue(String.class);
                            Log.i(TAG, "onDataChange: matched student PRN : " + userPRN);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage());
                }
            });

        }
                    scheduleAlarm();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        updateValuesFromBundle(savedInstanceState);

        userLocation = new UserLocation(latitude,longitude);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = currentUser.getEmail();

        Log.i(TAG, "onCreate: Current user: "+userEmail);
        checkLocationPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest());
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                checkLocationPermission();
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    mCurrentLocation = location;
                                    Log.i(TAG, "Location achieved!");
                                    updateLocationToDatabase(mCurrentLocation);
                                }
                                else {
                                    Log.i(TAG, "No location :(");
                                }
                            }
                        });
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(NormalUserActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mCurrentLocation = location;
                    Log.i(TAG, "onLocationResult: Location achieved");
                    latitude = mCurrentLocation.getLatitude();
                    longitude = mCurrentLocation.getLongitude();
                    userLocation.setLatitude(latitude);
                    userLocation.setLongitude(longitude);
                }
            }
        };

        logoutBtn = findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginStatus = new MainActivity().checkUserLoginStatus(getApplicationContext());
                SharedPreferences preferences = getApplicationContext()
                        .getSharedPreferences("WCEVISITCOVID19", 0);
                SharedPreferences.Editor editor = preferences.edit();
                if (loginStatus) {
                    editor.clear();
                    editor.apply();
                    Toast.makeText(NormalUserActivity.this, "You have been signed out successfully!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NormalUserActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(NormalUserActivity.this, "Please Sign in first!", Toast.LENGTH_SHORT).show();
            }
        });

        dateTextView = findViewById(R.id.status_date_text_view);
        DateUtils date = new DateUtils();

        dateTextView.setText(date.toString());

        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup q1 = findViewById(R.id.q1_radio_group);
                ScrollView scrollView = findViewById(R.id.scroll_view);
                boolean toSubmit = true;
                if(q1.getCheckedRadioButtonId() == -1)
                {
                    //not answered
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    q1.requestFocus();
                    toSubmit = false;
                }
                else
                {
                    //go ahead
                    RadioButton checkedButton = findViewById(q1.getCheckedRadioButtonId());
                    affectedRegion = checkedButton.getText().toString();
                }
                RadioGroup q2 = findViewById(R.id.q2_radio_group);
                if(q2.getCheckedRadioButtonId() == -1)
                {
                    //not answered
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    q2.requestFocus();
                    toSubmit =false;
                }
                else
                {
                    //go ahead
                    RadioButton checkedButton = findViewById(q2.getCheckedRadioButtonId());
                    travelledContainmentZone = checkedButton.getText().toString();
                }
                RadioGroup q3 = findViewById(R.id.q3_radio_group);
                if(q3.getCheckedRadioButtonId() == -1)
                {
                    //not answered
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    q3.requestFocus();
                    toSubmit = false;
                }
                else
                {
                    //go ahead
                    RadioButton checkedButton = findViewById(q3.getCheckedRadioButtonId());
                    contactWithPatient = checkedButton.getText().toString();
                }

                //checking symptoms clicked or not
                CheckBox cb1 = findViewById(R.id.checkbox_body_ache);
                CheckBox cb2 = findViewById(R.id.checkbox_chest_pain);
                CheckBox cb3 = findViewById(R.id.checkbox_breathing);
                CheckBox cb4 = findViewById(R.id.checkbox_dry_cough);
                CheckBox cb5 = findViewById(R.id.checkbox_fever);
                CheckBox cb6 = findViewById(R.id.checkbox_headache);
                CheckBox cb7 = findViewById(R.id.checkbox_runny_nose);
                CheckBox cb8 = findViewById(R.id.checkbox_sore_throat);
                CheckBox cb9 = findViewById(R.id.checkbox_tiredness);
                CheckBox cb10 = findViewById(R.id.checkbox_none);
                if(!(cb1.isChecked() || cb2.isChecked() || cb3.isChecked() || cb4.isChecked() || cb5.isChecked() ||
                        cb6.isChecked() || cb7.isChecked() || cb8.isChecked() || cb9.isChecked() || cb10.isChecked()))
                {
                    //nothing checked
                    LinearLayout symptomLayout = findViewById(R.id.symptoms_layout);
                    symptomLayout.requestFocus();
                    toSubmit = false;
                }
                else
                {
                    //go ahead
                    bodyAche = cb1.isChecked() ? "Yes" : "No";
                    chestPain = cb2.isChecked() ? "Yes" : "No";
                    breathingDifficulty = cb3.isChecked() ? "Yes" : "No";
                    dryCough = cb4.isChecked() ? "Yes" : "No";
                    fever = cb5.isChecked() ? "Yes" : "No";
                    headache = cb6.isChecked() ? "Yes" : "No";
                    runnyNose = cb7.isChecked() ? "Yes" : "No";
                    soreThroat = cb8.isChecked() ? "Yes" : "No";
                    tiredness = cb9.isChecked() ? "Yes" : "No";
                    none = cb10.isChecked() ? "Yes" : "No";
                }

                if(toSubmit)
                {
                    //all OK, submit now
                    progressBar.setVisibility(View.VISIBLE);
                    DateUtils date = new DateUtils();
                    DatabaseReference dailyAssessmentReference = FirebaseDatabase.getInstance().getReference("Daily_assessment");

                    //q1
                    question = "Have you been in contact with a confirmed novel coronavirus (nCoV) patient in the past 14 days?";
                    String dateString = "/"+date.getYear()+"/"+date.getMonth()+"/"+date.getDate();
                    String pathString = dateString+"/"+question+"/"+userType;
                    DatabaseReference ref = dailyAssessmentReference.child(pathString);

                    HashMap<String,String> map = new HashMap<>();
                    map.put(userPRN,contactWithPatient);
                    ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+question + ":" + contactWithPatient);
                        }
                    });

                    //q2
                    question = "Have you been to any affected countries or regions or towns in the past 14 days?";
                    pathString = dateString+"/"+question+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map1 = new HashMap<>();
                    map1.put(userPRN,affectedRegion);
                    ref.setValue(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+question + ":" + affectedRegion);
                        }
                    });

                    //q3
                    question = "Have you traveled anywhere from Containment Zone of COVID-19 in last 28-45 days?";
                    pathString = dateString+"/"+question+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map2= new HashMap<>();
                    map2.put(userPRN,travelledContainmentZone);
                    ref.setValue(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+question + ":" + travelledContainmentZone);
                        }
                    });

                    //symptoms
                    question = "Symptoms";
                    symptom = "Body aches or muscle pain";
                    pathString = dateString+"/"+question+"/"+symptom+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map3= new HashMap<>();
                    map3.put(userPRN,bodyAche);
                    ref.setValue(map3).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+symptom + ":" + bodyAche);
                        }
                    });

                    symptom = "Chest pain";
                    pathString = dateString+"/"+question+"/"+symptom+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map4= new HashMap<>();
                    map4.put(userPRN,chestPain);
                    ref.setValue(map4).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+symptom + ":" + chestPain);
                        }
                    });

                    symptom = "Difficulty in breathing";
                    pathString = dateString+"/"+question+"/"+symptom+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map5= new HashMap<>();
                    map5.put(userPRN,breathingDifficulty);
                    ref.setValue(map5).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+symptom + ":" + breathingDifficulty);
                        }
                    });

                    symptom = "Dry cough";
                    pathString = dateString+"/"+question+"/"+symptom+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map6= new HashMap<>();
                    map6.put(userPRN,dryCough);
                    ref.setValue(map6).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+symptom + ":" + dryCough);
                        }
                    });

                    symptom = "Fever";
                    pathString = dateString+"/"+question+"/"+symptom+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map7= new HashMap<>();
                    map7.put(userPRN,fever);
                    ref.setValue(map7).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+symptom + ":" + fever);
                        }
                    });

                    symptom = "Headaches";
                    pathString = dateString+"/"+question+"/"+symptom+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map8= new HashMap<>();
                    map8.put(userPRN,headache);
                    ref.setValue(map8).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+symptom + ":" + headache);
                        }
                    });

                    symptom = "Runny nose";
                    pathString = dateString+"/"+question+"/"+symptom+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map9= new HashMap<>();
                    map9.put(userPRN,runnyNose);
                    ref.setValue(map9).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+symptom + ":" + runnyNose);
                        }
                    });

                    symptom = "Sore throat";
                    pathString = dateString+"/"+question+"/"+symptom+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map10= new HashMap<>();
                    map10.put(userPRN,soreThroat);
                    ref.setValue(map10).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+symptom + ":" + soreThroat);
                        }
                    });

                    symptom = "Tiredness";
                    pathString = dateString+"/"+question+"/"+symptom+"/"+userType;
                    ref = dailyAssessmentReference.child(pathString);
                    HashMap<String,String> map11= new HashMap<>();
                    map11.put(userPRN,tiredness);
                    ref.setValue(map11).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: "+userPRN+" : "+symptom + ":" + tiredness);
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                    Log.i(TAG, "onClick: All uploaded!");
                    Toast.makeText(NormalUserActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(NormalUserActivity.this,GuidelinesActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void updateLocationToDatabase(Location mCurrentLocation) {
        latitude = mCurrentLocation.getLatitude();
        longitude = mCurrentLocation.getLongitude();
        Log.i("Location info: Lat", Double.toString(latitude));
        Log.i("Location info: Lng", Double.toString(longitude));
        final String[] prn = new String[1];

        userLocation = new UserLocation(latitude,longitude);
        DatabaseReference studentDatabaseReference = FirebaseDatabase.getInstance()
                .getReference("Students");
        studentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot studentSnapshot : snapshot.getChildren())
                {
                    String email = studentSnapshot.child("Email").getValue(String.class);
                    Log.i(TAG, "onDataChange: student email : "+email);
                    if(email.equalsIgnoreCase(userEmail))
                    {
                        userPRN = studentSnapshot.child("PRN").getValue(String.class);
                        prn[0] = userPRN;
                        Log.i(TAG, "onDataChange: matched student PRN : "+userPRN);
                        break;
                    }
                }

                Log.i(TAG, "updateLocationToDatabase: prn[0]: "+prn[0]);

                if (prn[0]!=null) {
                    DatabaseReference userLocationDatabaseReference = FirebaseDatabase.getInstance()
                            .getReference("Students").child(prn[0]);
                    userLocationDatabaseReference.child("Current Location").setValue(userLocation)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: Location saved successfully!");
                        }
                    });
                }
                else
                    Log.i(TAG, "updateLocationToDatabase: PRN not found");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());

            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                requestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        startLocationUpdates();
                    }

                } else {

                    // permission denied! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            locationManager.removeUpdates(this);
//        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(NormalUserActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        updateLocationToDatabase(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void startLocationUpdates() {
        checkLocationPermission();
        fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                locationCallback,
                Looper.getMainLooper());
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...

        // Update UI to match restored state
    }

    public void onSymptomCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_body_ache:
                if (checked)
                {
                    CheckBox cb =  findViewById(R.id.checkbox_none);
                    cb.setChecked(false);
                    bodyAche = "Yes";
                }
                else
                    bodyAche = "No";

                break;
            case R.id.checkbox_chest_pain:
                if (checked)
                {
                    CheckBox cb =  findViewById(R.id.checkbox_none);
                    cb.setChecked(false);
                    chestPain = "Yes";
                }
                else
                    chestPain = "No";

                break;
            case R.id.checkbox_breathing:
                if (checked)
                {
                    CheckBox cb =  findViewById(R.id.checkbox_none);
                    cb.setChecked(false);
                    breathingDifficulty ="Yes";
                }
                else
                    breathingDifficulty = "No";

                    break;
            case R.id.checkbox_dry_cough:
                if (checked)
                {
                    CheckBox cb =  findViewById(R.id.checkbox_none);
                    cb.setChecked(false);
                    dryCough = "Yes";
                }
                else
                    dryCough = "No";

                    break;
            case R.id.checkbox_fever:
                if (checked)
                {
                    CheckBox cb =  findViewById(R.id.checkbox_none);
                    cb.setChecked(false);
                    fever = "Yes";
                }
                else
                    fever ="No";

                    break;
            case R.id.checkbox_headache:
                if (checked)
                {
                    CheckBox cb =  findViewById(R.id.checkbox_none);
                    cb.setChecked(false);
                    headache = "Yes";
                }
                else
                    headache = "No";

                    break;
            case R.id.checkbox_runny_nose:
                if (checked)
                {
                    CheckBox cb =  findViewById(R.id.checkbox_none);
                    cb.setChecked(false);
                    runnyNose = "Yes";
                }
                else
                    runnyNose = "No";

                    break;
            case R.id.checkbox_sore_throat:
                if (checked)
                {
                    CheckBox cb =  findViewById(R.id.checkbox_none);
                    cb.setChecked(false);
                    soreThroat = "Yes";
                }
                else
                    soreThroat = "No";

                    break;
            case R.id.checkbox_tiredness:
                if (checked)
                {
                    CheckBox cb =  findViewById(R.id.checkbox_none);
                    cb.setChecked(false);
                    tiredness = "Yes";
                }
                else
                    tiredness = "No";

                    break;

            case R.id.checkbox_none:
                if (checked)
                {
                    CheckBox cb1 = findViewById(R.id.checkbox_body_ache);
                    cb1.setChecked(false);
                    CheckBox cb2 = findViewById(R.id.checkbox_chest_pain);
                    cb2.setChecked(false);
                    CheckBox cb3 = findViewById(R.id.checkbox_breathing);
                    cb3.setChecked(false);
                    CheckBox cb4 = findViewById(R.id.checkbox_dry_cough);
                    cb4.setChecked(false);
                    CheckBox cb5 = findViewById(R.id.checkbox_fever);
                    cb5.setChecked(false);
                    CheckBox cb6 = findViewById(R.id.checkbox_headache);
                    cb6.setChecked(false);
                    CheckBox cb7 = findViewById(R.id.checkbox_runny_nose);
                    cb7.setChecked(false);
                    CheckBox cb8 = findViewById(R.id.checkbox_sore_throat);
                    cb8.setChecked(false);
                    CheckBox cb9 = findViewById(R.id.checkbox_tiredness);
                    cb9.setChecked(false);
                    none = "Yes";
                }
                else
                    none = "No";
                break;
        }
    }

    private void scheduleAlarm() {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("data", "Please fill daily assessment form");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long afterTwoMinutes = SystemClock.elapsedRealtime() + 2 * 60 * 1000;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle
                    (AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            afterTwoMinutes, pendingIntent);
        else
            alarmManager.setExact
                    (AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            afterTwoMinutes, pendingIntent);
    }
}