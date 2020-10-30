package com.wce.wcevisitcovid19;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wce.wcevisitcovid19.utils.DateUtils;

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
    TextView meetingWithTextView;
    TextView inTimeTextView;
    TextView outTimeTextView;
    ProgressBar progressBar;
    ImageView callImageView;
    ImageView locationImageView;
    private GoogleMap mMap;
    private LocationSource.OnLocationChangedListener mListener;
    private static final String TAG = "UserDetailsActivity";
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
    String userId;
    String username;
    String userType;
    String inTime;
    String outTime;
    String meetingWith;

    LinearLayout addressLayout;
    LinearLayout classLayout;
    LinearLayout prnLayout;
    LinearLayout quarantinedLayout;
    LinearLayout quarantinePeriodLayout;
    LinearLayout locationLayout;
    LinearLayout birthDateLayout;
    LinearLayout departmentLayout;
    LinearLayout emailLayout;
    LinearLayout locOfVisitLayout;
    LinearLayout purposeOfVisitLayout;
    LinearLayout dateOfVisitLayout;
    LinearLayout meetingWithLayout;
    LinearLayout inTimeLayout;
    LinearLayout outTimeLayout;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        intent = getIntent();
        username = intent.getStringExtra("userName");
        userType = intent.getStringExtra("userType");
        userId = intent.getStringExtra("userId");


        progressBar = findViewById(R.id.progressBar);

        usernameTextView = findViewById(R.id.user_name_text_view);
        userTypeTextView = findViewById(R.id.user_type_text_view);
        contactTextView = findViewById(R.id.contact_number_text_view);
        districtTextView = findViewById(R.id.dist_text_view);
        stateTextView = findViewById(R.id.stat_text_view);
        userTypeImageView =  findViewById(R.id.user_type_image_view);
        userTypeImageView.setAlpha(0.3f);
        callImageView = findViewById(R.id.call_image);
        locationImageView = findViewById(R.id.marker_image);
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
        meetingWithTextView = findViewById(R.id.meeting_with_text_view);
        inTimeTextView = findViewById(R.id.in_time_text_view);
        outTimeTextView = findViewById(R.id.out_time_text_view);


        addressLayout = findViewById(R.id.address_layout);
        classLayout = findViewById(R.id.class_layout);
        prnLayout = findViewById(R.id.prn_layout);
        quarantinedLayout = findViewById(R.id.quarantined_layout);
        quarantinePeriodLayout = findViewById(R.id.quarantine_period_layout);
        locationLayout = findViewById(R.id.current_location_layout);
        birthDateLayout = findViewById(R.id.age_layout);
        departmentLayout = findViewById(R.id.department_layout);
        emailLayout = findViewById(R.id.email_layout);
        locOfVisitLayout = findViewById(R.id.location_of_visit_layout);
        purposeOfVisitLayout = findViewById(R.id.purpose_of_visit_layout);
        dateOfVisitLayout = findViewById(R.id.date_of_visit_layout);
        meetingWithLayout = findViewById(R.id.meeting_with_layout);
        inTimeLayout = findViewById(R.id.in_time_layout);
        outTimeLayout = findViewById(R.id.out_time_layout);


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

                Log.i(TAG, "onCreate: Student id: "+userId);
                DatabaseReference studentDatabaseReference = dbRef.child("Students").child(userId);
                studentDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                        String studentName;

                        studentName = studentSnapshot.child("Name").getValue(String.class);
                        contact = studentSnapshot.child("Contact number").getValue(String.class);
                        district = studentSnapshot.child("District").getValue(String.class);
                        state = studentSnapshot.child("State").getValue(String.class);
                        PRN = studentSnapshot.child("PRN").getValue(String.class);
                        Log.i(TAG, "onDataChange: Found Student: "+studentName+" "+PRN);
                        address = studentSnapshot.child("Address").getValue(String.class);
                        studentClass = studentSnapshot.child("Class").getValue(String.class);
                        isQuarantined = studentSnapshot.child("Quarantined in lockdown").getValue(String.class);
                        if("yes".equalsIgnoreCase(isQuarantined))
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

                        progressBar.setVisibility(View.GONE);
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

                DatabaseReference facultyDatabaseReference = dbRef.child("Faculty").child(userId);
                facultyDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot facultySnapshot) {

                        String facultyName = facultySnapshot.child("Name").getValue(String.class);
                        contact = facultySnapshot.child("Contact number").getValue(String.class);
                        district = facultySnapshot.child("District").getValue(String.class);
                        state = facultySnapshot.child("State").getValue(String.class);
                        address = facultySnapshot.child("Address").getValue(String.class);
                        isQuarantined = facultySnapshot.child("Quarantined in lockdown").getValue(String.class);
                        if("yes".equalsIgnoreCase(isQuarantined))
                        {
                            quarantinePeriodLayout.setVisibility(View.VISIBLE);
                            quarantinePeriod = facultySnapshot.child("Quarantine period").getValue(String.class);
                        }
                        email = facultySnapshot.child("Email").getValue(String.class);
                        try {
                            birthDate = facultySnapshot.child("Birth date").getValue(String.class);
                            DateUtils du = new DateUtils(birthDate);
                            age = String.valueOf(du.getAge(Integer.parseInt(du.extractYear()),Integer.parseInt(du.extractMonth()),Integer.parseInt(du.extractDate())));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Long longAge = facultySnapshot.child("Age").getValue(Long.class);
                            age = String.valueOf(longAge);
                        }
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
                        usernameTextView.setText(facultyName);
                        progressBar.setVisibility(View.GONE);
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

                DatabaseReference nonTeachingDatabaseReference = dbRef.child("Non_teaching").child(userId);
                nonTeachingDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot nonTeachingStaffSnapshot) {
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
                        try {
                            birthDate = nonTeachingStaffSnapshot.child("Birth date").getValue(String.class);
                            DateUtils du = new DateUtils(birthDate);
                            age = String.valueOf(du.getAge(Integer.parseInt(du.extractYear()),Integer.parseInt(du.extractMonth()),Integer.parseInt(du.extractDate())));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Long longAge = nonTeachingStaffSnapshot.child("Age").getValue(Long.class);
                            age = String.valueOf(longAge);
                        }
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
                meetingWithLayout.setVisibility(View.VISIBLE);
                inTimeLayout.setVisibility(View.VISIBLE);
                outTimeLayout.setVisibility(View.VISIBLE);


                //trying to reduce fetch time and data
                DatabaseReference outsidersDatabaseReference = dbRef.child("Outsiders").child(userId);
                outsidersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                            String outsiderName = dataSnapshot.child("Name").getValue(String.class);


                        contact = dataSnapshot.child("Contact number").getValue(String.class);
                        district = dataSnapshot.child("District").getValue(String.class);
                        state = dataSnapshot.child("State").getValue(String.class);
//                                address = nonTeachingStaffSnapshot.child("Address").getValue(String.class);
//                                isQuarantined = nonTeachingStaffSnapshot.child("Quarantined in lockdown").getValue(String.class);
//                                if(isQuarantined.equalsIgnoreCase("yes"))
//                                {
//                                quarantinePeriodLayout.setVisibility(View.VISIBLE);
//                                    quarantinePeriod = outsiderSnapshot.child("Quarantine period").getValue(String.class);
//                                }


//                                    email = dataSnapshot.child("Email").getValue(String.class);

                        try
                        {
                            birthDate = dataSnapshot.child("Birth date").getValue(String.class);

                            DateUtils du = new DateUtils(birthDate);
                            age = String.valueOf(du.getAge(Integer.parseInt(du.extractYear()),Integer.parseInt(du.extractMonth()),Integer.parseInt(du.extractDate())));
                        }
                        catch (Exception e)
                        {
                            Long longAge = dataSnapshot.child("Age").getValue(Long.class);
                            age = String.valueOf(longAge);
                        }

                        locOfVisit = dataSnapshot.child("Location of visit").getValue(String.class);
                        purposeOfVisit = dataSnapshot.child("Purpose of visit").getValue(String.class);
                        dateOfVisit = dataSnapshot.child("Date of visit").getValue(String.class);
                        meetingWith = dataSnapshot.child("Meeting with").getValue(String.class);
                        inTime = dataSnapshot.child("In Time").getValue(String.class);
                        outTime = dataSnapshot.child("Out Time").getValue(String.class);
                        //showing user's image
//                        String imgUri = dataSnapshot.child("Image data").getValue(String.class);
//                        Log.i(TAG, "onDataChange: imageURI: "+imgUri);
//                                byte[] decodedString = Base64.decode(imgUri, Base64.DEFAULT);
//                                Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                                userTypeImageView.setImageBitmap(bitMap);

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
                        meetingWithTextView.setText(meetingWith);
                        inTimeTextView.setText(inTime);
                        outTimeTextView.setText(outTime);
//                                quarantinePeriodTextView.setText(quarantinePeriod);
                        progressBar.setVisibility(View.GONE);
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