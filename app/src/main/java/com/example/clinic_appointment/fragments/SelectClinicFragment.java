package com.example.clinic_appointment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.clinic_appointment.adapters.SelectHealthFacilityAdapter;
import com.example.clinic_appointment.databinding.FragmentSelectClinicBinding;
import com.example.clinic_appointment.listeners.HealthFacilityListener;
import com.example.clinic_appointment.models.HealthFacility.HealthFacilitiesResponse;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.networking.clients.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectClinicFragment extends Fragment implements HealthFacilityListener {

    private FragmentSelectClinicBinding binding;
    private List<HealthFacility> originalHealthFacilities = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectClinicBinding.inflate(getLayoutInflater());
        initiate();
        return binding.getRoot();
    }

    private void displayError() {
        binding.pbLoading.setVisibility(View.GONE);
        binding.llError.setVisibility(View.VISIBLE);
    }

    private void initiate() {
        Call<HealthFacilitiesResponse> call = RetrofitClient.getPublicAppointmentService().getAllHealthFacilities();
        call.enqueue(new Callback<HealthFacilitiesResponse>() {
            @Override
            public void onResponse(@NonNull Call<HealthFacilitiesResponse> call, @NonNull Response<HealthFacilitiesResponse> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    originalHealthFacilities = response.body().getHealthFacilities();
                    SelectHealthFacilityAdapter adapter = new SelectHealthFacilityAdapter(originalHealthFacilities, SelectClinicFragment.this, requireActivity());
                    binding.rvHealthFacility.setAdapter(adapter);
                    binding.rvHealthFacility.setVisibility(View.VISIBLE);
                    binding.pbLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HealthFacilitiesResponse> call, @NonNull Throwable t) {
                displayError();
            }
        });
    }

    @Override
    public void onClick(HealthFacility healthFacility) {

    }

    @Override
    public void onProvinceSelect(String provinceName) {

    }
}