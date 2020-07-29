package com.wce.wcevisitcovid19;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.models.UserLocation;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

public class UserDetailsActivity extends AppCompatActivity implements LocationListener {

    TextView usernameTextView;
    TextView userTypeTextView;
    TextView contactTextView;
    TextView districtTextView;
    TextView stateTextView;
    ImageView userTypeImageView;
    TextView addressTextView;
    TextView classTextView;
    TextView prnTextView;
    TextView quarantinedTextView;
    TextView quarantinePeriodTextView;
    TextView ageTextView;
    TextView departmentTextView;
    TextView emailTextView;
    TextView locOfVisitTextView;
    TextView purposeOfVisitTextView;
    TextView dateOfVisitTextView;
    ProgressBar progressBar;
    ImageView callImageView;
    ImageView locationImageView;
    private GoogleMap mMap;
    private LocationSource.OnLocationChangedListener mListener;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();
    String address;
    String birthDate;
    String contact;
    String department;
    String district;
    String state;
    String email;
    String isQuarantined;
    String quarantinePeriod;
    String PRN;
    String studentClass;
    String age;
    String locOfVisit;
    String purposeOfVisit;
    String dateOfVisit;
    Double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        progressBar = findViewById(R.id.progressBar);

        usernameTextView = findViewById(R.id.user_name_text_view);
        userTypeTextView = findViewById(R.id.user_type_text_view);
        contactTextView = findViewById(R.id.contact_number_text_view);
        districtTextView = findViewById(R.id.dist_text_view);
        stateTextView = findViewById(R.id.stat_text_view);
        userTypeImageView = findViewById(R.id.user_type_image_view);
        userTypeImageView.setAlpha(0.3f);
        callImageView = findViewById(R.id.call_image);
        locationImageView = findViewById(R.id.marker_image);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        addressTextView = findViewById(R.id.address_text_view);
        classTextView = findViewById(R.id.class_text_view);
        prnTextView = findViewById(R.id.prn_text_view);
        quarantinedTextView = findViewById(R.id.is_quarantined_text_view);
        quarantinePeriodTextView = findViewById(R.id.quarantine_period_text_view);

        ageTextView = findViewById(R.id.age_text_view);
        departmentTextView = findViewById(R.id.department_text_view);
        emailTextView = findViewById(R.id.email_text_view);

        locOfVisitTextView = findViewById(R.id.location_of_visit_text_view);
        purposeOfVisitTextView = findViewById(R.id.purpose_of_visit_text_view);
        dateOfVisitTextView = findViewById(R.id.date_of_visit_text_view);

        LinearLayout contactLayout = findViewById(R.id.contact_number_layout);
        contactLayout.setBaselineAligned(false);

        LinearLayout addressLayout = findViewById(R.id.address_layout);
        LinearLayout classLayout = findViewById(R.id.class_layout);
        LinearLayout prnLayout = findViewById(R.id.prn_layout);
        LinearLayout quarantinedLayout = findViewById(R.id.quarantined_layout);
        final LinearLayout quarantinePeriodLayout = findViewById(R.id.quarantine_period_layout);
        LinearLayout locationLayout = findViewById(R.id.current_location_layout);

        final LinearLayout birthDateLayout = findViewById(R.id.age_layout);
        LinearLayout departmentLayout = findViewById(R.id.department_layout);
        LinearLayout emailLayout = findViewById(R.id.email_layout);

        LinearLayout locOfVisitLayout = findViewById(R.id.location_of_visit_layout);
        LinearLayout purposeOfVisitLayout = findViewById(R.id.purpose_of_visit_layout);
        LinearLayout dateOfVisitLayout = findViewById(R.id.date_of_visit_layout);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("userName");
        String userType = intent.getStringExtra("userType");


        callImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String uri = "tel:" + contact;
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        locationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latitude!=null && longitude!=null)
                {
                    Toast.makeText(UserDetailsActivity.this, "Latitude :"+latitude+" Longitude :"+longitude, Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude",latitude);
                    bundle.putDouble("longitude",longitude);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    MapsFragment fragment = new MapsFragment();
                    fragment.setArguments(bundle);
                    transaction.add(R.id.container,fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        usernameTextView.setText(username);

        //checking user type and showing data accordingly
        switch (userType)
        {
            case "Students":
                userTypeImageView.setImageResource(R.drawable.ic_student);
                userTypeTextView.setText("Student");

                addressLayout.setVisibility(View.VISIBLE);
                classLayout.setVisibility(View.VISIBLE);
                prnLayout.setVisibility(View.VISIBLE);
                quarantinedLayout.setVisibility(View.VISIBLE);
                locationLayout.setVisibility(View.VISIBLE);

                final String fetchUsingPRN = intent.getStringExtra("fetchUsingPRN");

                DatabaseReference studentsDatabaseReference = dbRef.child("Students");
                studentsDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                            String studentName;
                            if(fetchUsingPRN!=null && fetchUsingPRN.equals("yes"))
                            {
                                studentName = studentSnapshot.child("PRN").getValue(String.class);
                            }
                            else {
                                studentName = studentSnapshot.child("Name").getValue(String.class);
                            }

                            if(studentName.equalsIgnoreCase(username)) {
                                contact = studentSnapshot.child("Contact number").getValue(String.class);
                                district = studentSnapshot.child("District").getValue(String.class);
                                state = studentSnapshot.child("State").getValue(String.class);
                                PRN = studentSnapshot.child("PRN").getValue(String.class);
                                address = studentSnapshot.child("Address").getValue(String.class);
                                studentClass = studentSnapshot.child("Class").getValue(String.class);
                                isQuarantined = studentSnapshot.child("Quarantined in lockdown").getValue(String.class);
                                if(isQuarantined.equalsIgnoreCase("yes"))
                                {
                                    quarantinePeriodLayout.setVisibility(View.VISIBLE);
                                    quarantinePeriod = studentSnapshot.child("Quarantine period").getValue(String.class);
                                }
                                latitude = studentSnapshot.child("Current Location").child("latitude").getValue(Double.class);
                                longitude = studentSnapshot.child("Current Location").child("longitude").getValue(Double.class);

                                contactTextView.setText(contact);
                                districtTextView.setText(district);
                                stateTextView.setText(state);
                                prnTextView.setText(PRN);
                                addressTextView.setText(address);
                                classTextView.setText(studentClass);
                                quarantinedTextView.setText(isQuarantined);
                                quarantinePeriodTextView.setText(quarantinePeriod);

                                if(fetchUsingPRN != null && fetchUsingPRN.equals("yes"))
                                    usernameTextView.setText(studentSnapshot.child("Name").getValue(String.class));

                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error", error.getMessage());
                    }
                });
                break;

            case "Faculty":
                userTypeImageView.setImageResource(R.drawable.ic_staff);
                userTypeTextView.setText("Faculty");

                addressLayout.setVisibility(View.VISIBLE);
                quarantinedLayout.setVisibility(View.VISIBLE);
                birthDateLayout.setVisibility(View.VISIBLE);
                departmentLayout.setVisibility(View.VISIBLE);
                emailLayout.setVisibility(View.VISIBLE);

                DatabaseReference facultyDatabaseReference = dbRef.child("Faculty");
                facultyDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
                            String facultyName = facultySnapshot.child("Name").getValue(String.class);

                            if(facultyName.equalsIgnoreCase(username)) {
                                contact = facultySnapshot.child("Contact number").getValue(String.class);
                                district = facultySnapshot.child("District").getValue(String.class);
                                state = facultySnapshot.child("State").getValue(String.class);
                                address = facultySnapshot.child("Address").getValue(String.class);
                                isQuarantined = facultySnapshot.child("Quarantined in lockdown").getValue(String.class);
                                if(isQuarantined.equalsIgnoreCase("yes"))
                                {
                                    quarantinePeriodLayout.setVisibility(View.VISIBLE);
                                    quarantinePeriod = facultySnapshot.child("Quarantine period").getValue(String.class);
                                }
                                email = facultySnapshot.child("Email").getValue(String.class);
                                birthDate = facultySnapshot.child("Birth date").getValue(String.class);
                                assert birthDate != null;
                                age = getAge(birthDate);
                                department = facultySnapshot.child("Department").getValue(String.class);

                                contactTextView.setText(contact);
                                districtTextView.setText(district);
                                stateTextView.setText(state);
                                addressTextView.setText(address);
                                quarantinedTextView.setText(isQuarantined);
                                ageTextView.setText(age);
                                emailTextView.setText(email);
                                departmentTextView.setText(department);
                                quarantinePeriodTextView.setText(quarantinePeriod);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error", error.getMessage());
                    }
                });
                break;

            case "Non_teaching":
                userTypeImageView.setImageResource(R.drawable.ic_staff);
                userTypeTextView.setText("Non-Teaching Staff");

//                addressLayout.setVisibility(View.VISIBLE);
//                quarantinedLayout.setVisibility(View.VISIBLE);
                birthDateLayout.setVisibility(View.VISIBLE);
                departmentLayout.setVisibility(View.VISIBLE);
//                emailLayout.setVisibility(View.VISIBLE);

                DatabaseReference nonTeachingDatabaseReference = dbRef.child("Non_teaching");
                nonTeachingDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot nonTeachingStaffSnapshot : dataSnapshot.getChildren()) {
                            String nonTeachingStaffName = nonTeachingStaffSnapshot.child("Name").getValue(String.class);

                            if(nonTeachingStaffName.equalsIgnoreCase(username)) {
                                contact = nonTeachingStaffSnapshot.child("Contact").getValue(String.class);
                                district = nonTeachingStaffSnapshot.child("District").getValue(String.class);
                                state = nonTeachingStaffSnapshot.child("State").getValue(String.class);
//                                address = nonTeachingStaffSnapshot.child("Address").getValue(String.class);
//                                isQuarantined = nonTeachingStaffSnapshot.child("Quarantined in lockdown").getValue(String.class);
//                                if(isQuarantined.equalsIgnoreCase("yes"))
//                                {
//                                quarantinePeriodLayout.setVisibility(View.VISIBLE);
//                                    quarantinePeriod = nonTeachingStaffSnapshot.child("Quarantine period").getValue(String.class);
//                                }
//                                email = nonTeachingStaffSnapshot.child("Email").getValue(String.class);
                                birthDate = nonTeachingStaffSnapshot.child("Birth date").getValue(String.class);
                                assert birthDate != null;
                                age = getAge(birthDate);
                                department = nonTeachingStaffSnapshot.child("Department").getValue(String.class);

                                contactTextView.setText(contact);
                                districtTextView.setText(district);
                                stateTextView.setText(state);
//                                addressTextView.setText(address);
//                                quarantinedTextView.setText(isQuarantined);
                                ageTextView.setText(age);
//                                emailTextView.setText(email);
                                departmentTextView.setText(department);
//                                quarantinePeriodTextView.setText(quarantinePeriod);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error", error.getMessage());
                    }
                });
                break;

            case "Outsiders":
                userTypeImageView.setImageResource(R.drawable.ic_visitor);
                userTypeTextView.setText("Visitor");

                //                addressLayout.setVisibility(View.VISIBLE);
//                quarantinedLayout.setVisibility(View.VISIBLE);
                birthDateLayout.setVisibility(View.VISIBLE);
//                emailLayout.setVisibility(View.VISIBLE);
                locOfVisitLayout.setVisibility(View.VISIBLE);
                purposeOfVisitLayout.setVisibility(View.VISIBLE);
                dateOfVisitLayout.setVisibility(View.VISIBLE);

                DatabaseReference outsidersDatabaseReference = dbRef.child("Outsiders");
                outsidersDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot outsiderSnapshot : dataSnapshot.getChildren()) {
                            String outsiderName = outsiderSnapshot.child("Name").getValue(String.class);

                            if(outsiderName.equalsIgnoreCase(username)) {
                                contact = outsiderSnapshot.child("Contact number").getValue(String.class);
                                district = outsiderSnapshot.child("District").getValue(String.class);
                                state = outsiderSnapshot.child("State").getValue(String.class);
//                                address = nonTeachingStaffSnapshot.child("Address").getValue(String.class);
//                                isQuarantined = nonTeachingStaffSnapshot.child("Quarantined in lockdown").getValue(String.class);
//                                if(isQuarantined.equalsIgnoreCase("yes"))
//                                {
//                                quarantinePeriodLayout.setVisibility(View.VISIBLE);
//                                    quarantinePeriod = outsiderSnapshot.child("Quarantine period").getValue(String.class);
//                                }
//                                email = nonTeachingStaffSnapshot.child("Email").getValue(String.class);
                                birthDate = outsiderSnapshot.child("Birth date").getValue(String.class);
                                assert birthDate != null;
                                age = getAge(birthDate);
                                locOfVisit = outsiderSnapshot.child("Location of visit").getValue(String.class);
                                purposeOfVisit = outsiderSnapshot.child("Purpose of visit").getValue(String.class);
                                dateOfVisit = outsiderSnapshot.child("Date of visit").getValue(String.class);

                                contactTextView.setText(contact);
                                districtTextView.setText(district);
                                stateTextView.setText(state);
//                                addressTextView.setText(address);
//                                quarantinedTextView.setText(isQuarantined);
                                ageTextView.setText(age);
//                                emailTextView.setText(email);
                                locOfVisitTextView.setText(locOfVisit);
                                purposeOfVisitTextView.setText(purposeOfVisit);
                                dateOfVisitTextView.setText(dateOfVisit);
//                                quarantinePeriodTextView.setText(quarantinePeriod);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("error", error.getMessage());
                    }
                });
                break;

            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getAge(String birthDate) {

        //if problem occurs on lower android versions, uncomment following code and comment another section

/*        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        int day = Integer.parseInt(birthDate.substring(0,2));
        int month = Integer.parseInt(birthDate.substring(3,5));
        int year = Integer.parseInt(birthDate.substring(6));
        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        int ageInt = age; */


        //runs only on devices above version 'O'

        String[] Birth_Date;

        Birth_Date = birthDate.split("/");

        final int year = Calendar.getInstance().get(Calendar.YEAR);
        final int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        final int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        LocalDate today=LocalDate.of(year,month,date);
        LocalDate birth_date=LocalDate.of(Integer.parseInt(Birth_Date[2]),Integer.parseInt(Birth_Date[1]),Integer.parseInt(Birth_Date[0]));

        int age= Period.between(birth_date,today).getYears();

        return Integer.toString(age);
    }

    @Override
    public void onLocationChanged(Location location) {
        if( mListener != null )
        {
            mListener.onLocationChanged( location );
            //Move the camera to the user's location once it's available!
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }
}