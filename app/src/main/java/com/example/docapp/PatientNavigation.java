package com.example.docapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.docapp.patient.ProfileFragment;
import com.example.docapp.patient.ui.appointment.AppointmentFragment;
import com.example.docapp.patient.ui.home.HomeFragment;
import com.example.docapp.patient.ui.makeappointment.MakeAppointmentFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.docapp.databinding.ActivityPatientNavigationBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PatientNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = "Patient Navigation";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPatientNavigationBinding binding;
    TextView name_TextView, email_TextView;
    FirebaseAuth fAuth;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPatientNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fAuth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.appBarPatientNavigation.toolbar);

        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_patient_home, R.id.nav_make_appointment, R.id.nav_patient_appointment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient_navigation);
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient_navigation);
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
        case R.id.nav_patient_home: {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_patient_navigation, new HomeFragment(), "HOME")
                    .addToBackStack(null)
                    .commit();
            break;
        }

        case R.id.nav_make_appointment: {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_patient_navigation, new MakeAppointmentFragment(), "MAKE_APPOINTMENT")
                    .addToBackStack(null)
                    .commit();
            break;
        }

        case R.id.nav_patient_appointment: {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_patient_navigation, new AppointmentFragment(), "APPOINTMENT")
                    .addToBackStack(null)
                    .commit();
            break;
        }

        case R.id.nav_patient_profile: {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_patient_navigation, new ProfileFragment(), "Profile")
                    .addToBackStack(null)
                    .commit();
            break;
        }
        case R.id.nav_logout: {
            fAuth.signOut();
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
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