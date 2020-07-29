package com.wce.wcevisitcovid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class StudentActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "StudentActivity";
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
    String userEmail,userPRN;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        updateValuesFromBundle(savedInstanceState);

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
                        resolvable.startResolutionForResult(StudentActivity.this,
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
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("WCEVISITCOVID19", 0);
                SharedPreferences.Editor editor = preferences.edit();
                if (loginStatus) {
                    editor.clear();
                    editor.apply();
                    Toast.makeText(StudentActivity.this, "You have been signed out successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StudentActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(StudentActivity.this, "Please Sign in first!", Toast.LENGTH_SHORT).show();
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
        DatabaseReference studentDatabaseReference = FirebaseDatabase.getInstance().getReference("Students");
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
                    DatabaseReference userLocationDatabaseReference = FirebaseDatabase.getInstance().getReference("Students").child(prn[0]);
                    userLocationDatabaseReference.child("Current Location").setValue(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                ActivityCompat.requestPermissions(StudentActivity.this,
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
}