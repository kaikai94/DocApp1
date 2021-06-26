package com.example.docapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.example.docapp.databinding.ActivityLoginBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn, registerBtn, registerDocBtn;
    TextInputEditText username_tb, password_tb;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private ActivityLoginBinding binding;
    private final static String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loginBtn = binding.loginBtn;
        registerBtn = binding.registerBtn;
        registerDocBtn = binding.registerDoctorBtn;
        username_tb = binding.LogET1;
        password_tb = binding.LogET2;

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        registerDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterDoctorActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fAuth.signInWithEmailAndPassword(
                        username_tb.getText().toString(), password_tb.getText().toString())
                        .addOnSuccessListener(authResult -> {
                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));

                            DocumentReference accRef = fStore.collection(getString(R.string.collection_accounts)).document(authResult.getUser().getUid());

                            accRef.get().addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    String accType = document.getString(getString(R.string.account_type));
                                    if (document.exists() && accType.equals("Patient")) {

                                        DocumentReference patRef = fStore.collection(getString(R.string.collection_patients)).document(authResult.getUser().getUid());
                                        patRef.get().addOnCompleteListener(patTask -> {
                                            DocumentSnapshot doc1 = patTask.getResult();
                                            Intent intent = new Intent(getApplicationContext(), PatientNavigation.class);
                                            intent.putExtra(getResources().getString(R.string.name), doc1.getString(getResources().getString(R.string.name)));
                                            intent.putExtra(getResources().getString(R.string.email), authResult.getUser().getEmail());

                                            startActivity(intent);
                                            finish();
                                        });
                                    }
                                    else if (document.exists() && accType.equals("Doctor")) {

                                        DocumentReference patRef = fStore.collection(getString(R.string.collection_doctors)).document(authResult.getUser().getUid());
                                        patRef.get().addOnCompleteListener(patTask -> {
                                            DocumentSnapshot doc2 = patTask.getResult();
                                            Intent intent = new Intent(getApplicationContext(), DoctorNavigation.class);
                                            intent.putExtra(getResources().getString(R.string.name), doc2.getString(getResources().getString(R.string.name)));
                                            intent.putExtra(getResources().getString(R.string.email), authResult.getUser().getEmail());

                                            startActivity(intent);
                                            finish();
                                        });
                                    }
                                }
                            });

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() != null) {
            DocumentReference accRef = fStore.collection(getString(R.string.collection_accounts)).document(fAuth.getUid());

            accRef.get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String accType = document.getString(getString(R.string.account_type));
                    if (document.exists() && accType.equals("Patient")) {

                        DocumentReference patRef = fStore.collection(getString(R.string.collection_patients)).document(fAuth.getUid());
                        patRef.get().addOnCompleteListener(patTask -> {
                            DocumentSnapshot doc1 = patTask.getResult();
                            Intent intent = new Intent(getApplicationContext(), PatientNavigation.class);
                            intent.putExtra(getResources().getString(R.string.name), doc1.getString(getResources().getString(R.string.name)));
                            intent.putExtra(getResources().getString(R.string.email), fAuth.getCurrentUser().getEmail());

                            startActivity(intent);
                            finish();
                        });
                    }
                    else if (document.exists() && accType.equals("Doctor")) {

                        DocumentReference patRef = fStore.collection(getString(R.string.collection_doctors)).document(fAuth.getUid());
                        patRef.get().addOnCompleteListener(patTask -> {
                            DocumentSnapshot doc2 = patTask.getResult();
                            Intent intent = new Intent(getApplicationContext(), DoctorNavigation.class);
                            intent.putExtra(getResources().getString(R.string.name), doc2.getString(getResources().getString(R.string.name)));
                            intent.putExtra(getResources().getString(R.string.email), fAuth.getCurrentUser().getEmail());

                            startActivity(intent);
                            finish();
                        });
                    }
                }
            });
        }
    }
}