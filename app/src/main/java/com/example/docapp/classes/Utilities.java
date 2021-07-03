package com.example.docapp.classes;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

    //create an object of Utilities
    private static Utilities instance = new Utilities();

    //make the constructor private so that this class cannot be
    //instantiated
    private Utilities(){}

    //Get the only object available
    public static Utilities getInstance(){
        return instance;
    }

    public interface FireStoreCallback {
        void onCallback(List<DoctorSpeciality> list);
    }

    public void getSpecialityList(FireStoreCallback firestoreCallback)
    {
        List<DoctorSpeciality> list = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection("doctor_speciality")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            DoctorSpeciality temp = new DoctorSpeciality();
                            temp.setDocumentId(doc.getId());
                            temp.setName(doc.getString("name"));
                            list.add(temp);
                        }
                        firestoreCallback.onCallback(list);
                    }
                });
    }

}