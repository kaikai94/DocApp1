package com.example.docapp.doctor.ui.appointment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

public class FragmentUpcoming extends Fragment {

    private final static String TAG = "Upcoming Tab";
    List<String> dateList = new ArrayList<>();
    List<String> timeList = new ArrayList<>();
    List<String> appointmentIDList = new ArrayList<>();
    List<String> statusList = new ArrayList<>();
    RecyclerView recyclerView;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    List<String> list;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_child, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);

        ReadData(list -> {
            AppointmentAdapter myAdapter = new AppointmentAdapter(getActivity(), dateList, timeList, statusList, appointmentIDList);
            recyclerView.setAdapter(myAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        });

        return rootView;
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
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ResetList();
                        DateTime now = new DateTime().withZone(DateTimeZone.forID("Asia/Kuala_Lumpur"));

                        for (DocumentSnapshot doc : task.getResult()) {

                            DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
                            DateTime dt = new DateTime( doc.getTimestamp(getResources().getString(R.string.date_time)).getSeconds() * 1000 ) ;
                            DateTime dtMalaysia = dt.withZone(DateTimeZone.forID("Asia/Kuala_Lumpur"));
                            int retVal = dateTimeComparator.compare(now, dtMalaysia);
                            if(retVal <= 0)
                            {
                                dateList.add(dtMalaysia.toString("yyyy-MM-dd"));
                                timeList.add(dtMalaysia.toString("h:mm a"));
                                appointmentIDList.add(doc.getId());
                                statusList.add(doc.getString(getResources().getString(R.string.status)));
                            }
                        }
                        firestoreCallback.onCallback(list);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            ReadData(list -> {
                AppointmentAdapter myAdapter = new AppointmentAdapter(getActivity(), dateList, timeList, statusList, appointmentIDList);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            });
        }
    }

    public void ResetList()
    {
        dateList.clear();
        timeList.clear();
        appointmentIDList.clear();
        statusList.clear();
    }
}
