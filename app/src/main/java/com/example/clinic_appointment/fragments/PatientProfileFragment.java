package com.example.clinic_appointment.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.clinic_appointment.activities.AddPatientActivity;
import com.example.clinic_appointment.adapters.PatientProfileAdapter;
import com.example.clinic_appointment.databinding.FragmentPatientProfileFragmentBinding;
import com.example.clinic_appointment.listeners.PatientProfileListener;
import com.example.clinic_appointment.models.PatientProfile.PatientProfile;
import com.example.clinic_appointment.models.PatientProfile.PatientProfileResponse;
import com.example.clinic_appointment.networking.clients.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientProfileFragment extends Fragment implements PatientProfileListener {

    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Integer resultCode = result.getResultCode();
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(requireActivity(), "Bạn đã tạo hồ sơ thành công!!", Toast.LENGTH_SHORT).show();
                }
            });

    private FragmentPatientProfileFragmentBinding binding;
    private List<PatientProfile> patientProfiles;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPatientProfileFragmentBinding.inflate(inflater, container, false);
        initiate();
        eventHandling();
        return binding.getRoot();
    }

    private void initiate() {
        getProfiles();
        final SwipeRefreshLayout pullToRefresh = binding.swipeLayout;
        pullToRefresh.setOnRefreshListener(() -> {
            getProfiles();
            pullToRefresh.setRefreshing(false);
        });

    }

    private void getProfiles() {
        binding.pbLoading.setVisibility(View.VISIBLE);
        Call<PatientProfileResponse> call = RetrofitClient.getAuthenticatedAppointmentService(requireActivity()).getAllPatientProfiles();
        call.enqueue(new Callback<PatientProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<PatientProfileResponse> call, @NonNull Response<PatientProfileResponse> response) {
                if (response.body() != null && response.body().isSuccess() && !response.body().getPatientProfiles().isEmpty()) {
                    patientProfiles = new ArrayList<>();
                    patientProfiles = response.body().getPatientProfiles();
                    PatientProfileAdapter adapter = new PatientProfileAdapter(PatientProfileFragment.this, patientProfiles);
                    binding.rvPatientProfile.setAdapter(adapter);
                    binding.rvPatientProfile.setVisibility(View.VISIBLE);
                    binding.pbLoading.setVisibility(View.GONE);
                } else {
                    binding.pbLoading.setVisibility(View.GONE);
                    binding.tvNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientProfileResponse> call, @NonNull Throwable t) {
                binding.pbLoading.setVisibility(View.GONE);
                displayError();
            }
        });
    }

    @Override
    public void onClick(PatientProfile patientProfile) {
        Intent intent = new Intent(requireActivity(), AddPatientActivity.class);
        intent.putExtra("name", patientProfile.getFullName());
        intent.putExtra("phone", patientProfile.getPhoneNumber());
        intent.putExtra("id", patientProfile.getId());
        mStartForResult.launch(intent);
    }

    private void displayError() {
        binding.pbLoading.setVisibility(View.GONE);
        binding.llError.setVisibility(View.VISIBLE);
    }

    private void eventHandling() {
        binding.ivCreate.setOnClickListener(v -> mStartForResult.launch(new Intent(requireActivity(), AddPatientActivity.class)));
    }
}