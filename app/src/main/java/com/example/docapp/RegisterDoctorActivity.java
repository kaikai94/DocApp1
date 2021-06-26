package com.example.docapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.docapp.classes.DoctorSpeciality;
import com.example.docapp.databinding.ActivityRegisterDoctorBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterDoctorActivity extends AppCompatActivity {

    private static final String TAG = "Register";
    private ActivityRegisterDoctorBinding binding;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private String specialityId;
    String userID;
    List<DoctorSpeciality> specialityList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityRegisterDoctorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        final Button register_btn = binding.registerBtn;
        final TextInputEditText email_tb = binding.regEmail;
        final TextInputEditText password_tb = binding.regPassword;
        final TextInputEditText confirm_password_tb = binding.regConPassword;
        final TextInputEditText name_tb = binding.regName;
        final TextInputEditText age_tb = binding.regAge;
        final TextInputEditText icno_tb = binding.regIcno;
        final TextInputEditText badgeNo_tb = binding.ardBadgeNoText;
        final MaterialAutoCompleteTextView speciality_dd = binding.ardSpecialityDrpDown;

        List<String> tempList = new ArrayList<>();
        ReadSpecialityList(list -> {
            list.forEach(cat -> { tempList.add(cat.getName());});
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tempList);
            speciality_dd.setAdapter(adapter);
        });

        speciality_dd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                specialityId = specialityList.get(position).getDocumentId();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_tb.getText().toString();
                String password = password_tb.getText().toString();
                String confPass = confirm_password_tb.getText().toString();
                String name = name_tb.getText().toString();
                int age = Integer.parseInt(age_tb.getText().toString());
                String icno = icno_tb.getText().toString();
                String badgno = badgeNo_tb.getText().toString();

                if(email.isEmpty()){
                    email_tb.setError("Email is Required");
                    return;
                }
                if(password.isEmpty()){
                    password_tb.setError("Password is Required");
                    return;
                }
                if(!password.equals(confPass)){
                    confirm_password_tb.setError("Password Do not Match");
                    return;
                }
                if(name.isEmpty()){
                    name_tb.setError("Name is Required");
                    return;
                }
                if(icno.isEmpty()){
                    icno_tb.setError("Identity No is Required");
                    return;
                }
                if(badgno.isEmpty()){
                    badgeNo_tb.setError("Badge No is Required");
                    return;
                }
                if(specialityId.isEmpty()){
                    speciality_dd.setError("Speciality is Required");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(RegisterDoctorActivity.this, "User created.", Toast.LENGTH_SHORT).show();
                        userID = fAuth.getCurrentUser().getUid();

                        DocumentReference accRef = fStore.collection(getString(R.string.collection_accounts)).document(userID);
                        Map<String, Object> account  = new HashMap<>();
                        account.put(getString(R.string.account_type), getString(R.string.Doctor));
                        accRef.set(account);

                        DocumentReference userRef = fStore.collection(getString(R.string.collection_doctors)).document(userID);
                        Map<String, Object> user  = new HashMap<>();
                        user.put(getString(R.string.name), name);
                        user.put(getString(R.string.age), age);
                        user.put(getString(R.string.ic_no), icno);
                        user.put(getString(R.string.badgeNo), badgno);
                        user.put(getString(R.string.specialityId), specialityId);

                        userRef.set(user).addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterDoctorActivity.this, "Successful created account", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onSuccess: UserID " + userID);
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failed" + e.getMessage());
                            }
                        });

                        //send user to next page
                        Intent intent = new Intent(getApplicationContext(), DoctorNavigation.class);
                        intent.putExtra(getString(R.string.name), name);
                        intent.putExtra(getString(R.string.email), email);

                        startActivity(intent);
                        //Destroy all previous activity so go back button useless
                        finish();


                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(RegisterDoctorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.getMessage());
                });
            }
        });
    }

    private interface FireStoreCallback {
        void onCallback(List<DoctorSpeciality> list);
    }

    private void ReadSpecialityList(FireStoreCallback firestoreCallback){
        fStore
                .collection(getResources().getString(R.string.collection_doctors_speciality))
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DoctorSpeciality temp = new DoctorSpeciality();
                    temp.setDocumentId(doc.getId());
                    temp.setName(doc.getString(getResources().getString(R.string.name)));
                    specialityList.add(temp);
                }
                firestoreCallback.onCallback(specialityList);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}