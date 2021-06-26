package com.example.docapp.patient.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.docapp.R;
import com.example.docapp.databinding.FragmentPatientHomeBinding;
import com.example.docapp.patient.ui.appointment.AppointmentFragment;
import com.example.docapp.patient.ui.makeappointment.MakeAppointmentFragment;

public class HomeFragment extends Fragment {


    private FragmentPatientHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPatientHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button makeNewAppointmentBtn = binding.makeNewAppointmentBtn;
        final Button viewAppointmentBtn = binding.viewAppointmentBtn;
        makeNewAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextFrag= new MakeAppointmentFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_content_patient_navigation, nextFrag, "MakeAppointmentFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        viewAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextFrag= new AppointmentFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_content_patient_navigation, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}