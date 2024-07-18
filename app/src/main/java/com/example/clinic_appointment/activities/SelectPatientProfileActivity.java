package com.example.clinic_appointment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.adapters.PatientProfileAdapter;
import com.example.clinic_appointment.databinding.ActivitySelectPatientProfileBinding;
import com.example.clinic_appointment.listeners.PatientProfileListener;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.models.PatientProfile.PatientProfile;
import com.example.clinic_appointment.models.PatientProfile.PatientProfileResponse;
import com.example.clinic_appointment.models.Schedule.ScheduleExclude;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectPatientProfileActivity extends AppCompatActivity implements PatientProfileListener {

    private Doctor selectedDoctor;
    private Department selectedDepartment;
    private HealthFacility selectedHealthFacility;
    private ScheduleExclude selectedSchedule;
    private String timeNumber;
    private ActivitySelectPatientProfileBinding binding;
    private List<PatientProfile> patientProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectPatientProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
    }

    private void initiate() {
        selectedDoctor = (Doctor) getIntent().getSerializableExtra(Constants.KEY_DOCTOR);
        selectedDepartment = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
        selectedHealthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
        selectedSchedule = (ScheduleExclude) getIntent().getSerializableExtra(Constants.KEY_DATE);
        timeNumber = getIntent().getStringExtra(Constants.KEY_TIME);
        binding.pbLoading.setVisibility(View.VISIBLE);
        Call<PatientProfileResponse> call = RetrofitClient.getAuthenticatedAppointmentService(this).getAllPatientProfiles();
        call.enqueue(new Callback<PatientProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<PatientProfileResponse> call, @NonNull Response<PatientProfileResponse> response) {
                if (response.body() != null && response.body().isSuccess() && !response.body().getPatientProfiles().isEmpty()) {
                    patientProfiles = new ArrayList<>();
                    patientProfiles = response.body().getPatientProfiles();
                    PatientProfileAdapter adapter = new PatientProfileAdapter(SelectPatientProfileActivity.this, patientProfiles);
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

    private void displayError() {
        binding.pbLoading.setVisibility(View.GONE);
        binding.llError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(PatientProfile patientProfile) {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        Doctor selectedDoctor = (Doctor) getIntent().getSerializableExtra(Constants.KEY_DOCTOR);
        Department selectedDepartment = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
        HealthFacility selectedHealthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
        ScheduleExclude selectedSchedule = (ScheduleExclude) getIntent().getSerializableExtra(Constants.KEY_DATE);
        intent.putExtra(Constants.KEY_DATE, selectedSchedule);
        intent.putExtra(Constants.KEY_DOCTOR, selectedDoctor);
        intent.putExtra(Constants.KEY_DEPARTMENT, selectedDepartment);
        intent.putExtra(Constants.KEY_HEALTH_FACILITY, selectedHealthFacility);
        intent.putExtra(Constants.KEY_TIME, timeNumber);
        intent.putExtra(Constants.KEY_PATIENT_ID, patientProfile);
        startActivity(intent);
    }
}