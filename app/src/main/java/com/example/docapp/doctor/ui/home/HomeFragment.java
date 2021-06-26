package com.example.docapp.doctor.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docapp.R;
import com.example.docapp.databinding.FragmentDoctorHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final static String TAG = "Home";
    private FragmentDoctorHomeBinding binding;
    RecyclerView recyclerView;
    TextView countTextView, pending_Count_View;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    List<String> list;
    List<String> dateList = new ArrayList<>();
    List<String> timeList = new ArrayList<>();
    List<String> appointmentIDList = new ArrayList<>();
    int document_size = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDoctorHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recycler_view);
        countTextView = root.findViewById(R.id.countTextView);
        pending_Count_View = root.findViewById(R.id.pending_count_TextView);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        ReadData(list -> {
            AppointmentAdapter myAdapter = new AppointmentAdapter(getActivity(), dateList, timeList, appointmentIDList);
            recyclerView.setAdapter(myAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        });


        ReadPendingData(list -> {

        });
        return root;
    }

    private interface FireStoreCallback {
        void onCallback(List<String> list);
    }

    private void ReadData(FireStoreCallback firestoreCallback){
        fStore
        .collection(getResources().getString(R.string.collection_appointments))
        .whereEqualTo(getResources().getString(R.string.doctorId), fAuth.getUid())
        .whereEqualTo(getResources().getString(R.string.status), getResources().getString(R.string.status_approve))
        .get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DateTime now = new DateTime().withZone(DateTimeZone.forID("Asia/Kuala_Lumpur"));
                for (DocumentSnapshot doc : task.getResult()) {
                    DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
                    DateTime dt = new DateTime( doc.getTimestamp(getResources().getString(R.string.date_time)).getSeconds() * 1000 ) ;
                    DateTime dtMalaysia = dt.withZone(DateTimeZone.forID("Asia/Kuala_Lumpur"));
                    int retVal = dateTimeComparator.compare(now, dtMalaysia);
                    if(retVal == 0)
                    {
                        dateList.add(dtMalaysia.toString("yyyy-MM-dd"));
                        timeList.add(dtMalaysia.toString("h:mm a"));
                        appointmentIDList.add(doc.getId());
                        document_size++;
                    }
                }
                countTextView.setText(String.valueOf(document_size));
                firestoreCallback.onCallback(list);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

    }

    private void ReadPendingData(FireStoreCallback firestoreCallback){
        fStore
                .collection(getResources().getString(R.string.collection_appointments))
                .whereEqualTo(getResources().getString(R.string.doctorId), fAuth.getUid())
                .whereEqualTo(getResources().getString(R.string.status), getResources().getString(R.string.status_pending_approve))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DateTime now = new DateTime().withZone(DateTimeZone.forID("Asia/Kuala_Lumpur"));
                        int pending_document_size = 0;
                        for (DocumentSnapshot doc : task.getResult()) {
                            DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
                            DateTime dt = new DateTime( doc.getTimestamp(getResources().getString(R.string.date_time)).getSeconds() * 1000 ) ;
                            DateTime dtMalaysia = dt.withZone(DateTimeZone.forID("Asia/Kuala_Lumpur"));
                            int retVal = dateTimeComparator.compare(now, dtMalaysia);
                            if(retVal <= 0)
                            {
                                pending_document_size++;
                            }
                        }
                        pending_Count_View.setText(String.valueOf(pending_document_size));
                        firestoreCallback.onCallback(list);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}