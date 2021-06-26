package com.example.docapp.patient;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.docapp.R;
import com.example.docapp.classes.Patient;
import com.example.docapp.classes.UserInterface;
import com.example.docapp.classes.UsersFactory;
import com.example.docapp.databinding.FragmentPatientProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    private static String TAG = "Patient profile";
    private FragmentPatientProfileBinding binding;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    UsersFactory uF = new UsersFactory();
    UserInterface pat = uF.getUsers("Patient");

    TextInputEditText name_text,age_text, ic_text;
    Button updateBtn, cancelBtn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPatientProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        name_text = binding.fppNameText;
        age_text = binding.fppAgeText;
        ic_text = binding.fppIdentityText;

        updateBtn = binding.updateBtn;
        cancelBtn = binding.cancelBtn;

        DocumentReference userRef = fStore.collection(getResources().getString(R.string.collection_accounts)).document(fAuth.getUid());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDocument = task.getResult();
                if (userDocument.exists()) {

                    DocumentReference docRef = fStore.collection(getResources().getString(R.string.collection_patients)).document(fAuth.getUid());
                    docRef.get().addOnCompleteListener(docTask -> {
                        if (docTask.isSuccessful()) {
                            DocumentSnapshot doctorDocument = docTask.getResult();
                            if (doctorDocument.exists()) {
                                //TODO
                                pat = doctorDocument.toObject(Patient.class);
                                name_text.setText(pat.getName());
                                age_text.setText(String.valueOf(pat.getAge()));
                                ic_text.setText(pat.getIc_no());
                            }
                        }
                    });

                }
            }
        });

        updateBtn.setOnClickListener(v -> {
            DocumentReference docRef = fStore.collection(getResources().getString(R.string.collection_patients)).document(fAuth.getUid());
            pat.setName(name_text.getText().toString());
            pat.setAge(Integer.parseInt(age_text.getText().toString()));
            pat.setIc_no(ic_text.getText().toString());

            docRef
                    .set(pat)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Successfully update profile!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Fail to update profile!", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
