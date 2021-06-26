package com.example.docapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.docapp.databinding.ActivityDoctorNavigationBinding;
import com.example.docapp.doctor.ProfileFragment;
import com.example.docapp.doctor.ui.appointment.AppointmentFragment;
import com.example.docapp.doctor.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DoctorNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = "Patient Navigation";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDoctorNavigationBinding binding;
    TextView name_TextView, email_TextView;
    FirebaseAuth fAuth;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDoctorNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fAuth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.appBarDoctorNavigation.toolbar);

        drawer = binding.doctorDrawerLayout;
        NavigationView navigationView = binding.doctorNavView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_doctor_home, R.id.nav_doctor_appointment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_doctor_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View hView =  navigationView.inflateHeaderView(R.layout.nav_header_patient_navigation);
        name_TextView = (TextView)hView.findViewById(R.id.AccName_TextBox);
        email_TextView = (TextView)hView.findViewById(R.id.Email_TextBox);

        SetData();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.patient_navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_doctor_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void SetData(){
        if(getIntent().hasExtra(getResources().getString(R.string.name)))
        {
            name_TextView.setText(getIntent().getStringExtra(getResources().getString(R.string.name)));
            email_TextView.setText(getIntent().getStringExtra(getResources().getString(R.string.email)));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_doctor_home: {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_content_doctor_navigation, new HomeFragment(), "TITLES")
                        .addToBackStack(null)
                        .commit();
                break;
            }

            case R.id.nav_doctor_appointment: {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_content_doctor_navigation, new AppointmentFragment(), "TITLES")
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.nav_doctor_profile: {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_content_doctor_navigation, new ProfileFragment(), "TITLES")
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.nav_logout: {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //finish();
                break;
            }
        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}