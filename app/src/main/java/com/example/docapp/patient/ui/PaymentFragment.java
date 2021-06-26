package com.example.docapp.patient.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.docapp.R;
import com.example.docapp.databinding.FragmentPaymentBinding;
import com.example.docapp.patient.ui.appointment.AppointmentFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class PaymentFragment extends Fragment {

    private FragmentPaymentBinding binding;
    private String appointmentID;
    private static String TAG = "Payment";
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        final Button payBtn = binding.payBtn;
        appointmentID = this.getArguments().getString(getResources().getString(R.string.appointment_id));

        payBtn.setOnClickListener(v -> {
            DocumentReference documentRef = fStore.collection(getString(R.string.collection_appointments)).document(appointmentID);

            documentRef
                    .update(getString(R.string.status), getString(R.string.status_pending_approve))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            Toast.makeText(getContext(), "Successful make payment.", Toast.LENGTH_SHORT).show();

                            getActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .remove(PaymentFragment.this)
                                    .replace(R.id.nav_host_fragment_content_patient_navigation, new AppointmentFragment(), "Appointment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
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