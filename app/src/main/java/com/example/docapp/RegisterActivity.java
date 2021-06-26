package com.example.docapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.example.docapp.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register";
    private ActivityRegisterBinding binding;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_register);

        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
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

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_tb.getText().toString();
                String password = password_tb.getText().toString();
                String confPass = confirm_password_tb.getText().toString();
                String fullname = name_tb.getText().toString();
                String age = age_tb.getText().toString();
                String icno = icno_tb.getText().toString();

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
                if(fullname.isEmpty()){
                    name_tb.setError("Name is Required");
                    return;
                }
                if(age.isEmpty()){
                    age_tb.setError("Age is Required");
                    return;
                }
                if(icno.isEmpty()){
                    icno_tb.setError("Identity No is Required");
                    return;
                }

                Toast.makeText(RegisterActivity.this, "Data Validated", Toast.LENGTH_SHORT).show();

                fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(RegisterActivity.this, "User created.", Toast.LENGTH_SHORT).show();
                        userID = fAuth.getCurrentUser().getUid();

                        DocumentReference accRef = fStore.collection(getString(R.string.collection_accounts)).document(userID);
                        Map<String, Object> account  = new HashMap<>();
                        account.put(getString(R.string.account_type), getString(R.string.Patient));
                        accRef.set(account);

                        DocumentReference userRef = fStore.collection(getString(R.string.collection_patients)).document(userID);
                        Map<String, Object> user  = new HashMap<>();
                        user.put(getString(R.string.name), fullname);
                        user.put(getString(R.string.age), Integer.parseInt(age));
                        user.put(getString(R.string.ic_no), icno);

                        userRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterActivity.this, "Successful created account", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onSuccess: UserID " + userID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failed" + e.getMessage());
                            }
                        });

                        //send user to next page
                        Intent intent = new Intent(getApplicationContext(), PatientNavigation.class);
                        intent.putExtra(getString(R.string.name), fullname);
                        intent.putExtra(getString(R.string.email), email);

                        startActivity(intent);
                        //Destroy all previous activity so go back button useless
                        finish();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }
                });
            }
        });
    }
}