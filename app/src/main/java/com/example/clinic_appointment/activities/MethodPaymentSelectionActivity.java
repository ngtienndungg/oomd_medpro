package com.example.clinic_appointment.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.databinding.ActivitySelectPaymentMethodBinding;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.models.PatientProfile.PatientProfile;
import com.example.clinic_appointment.models.Schedule.ScheduleExclude;
import com.example.clinic_appointment.utilities.Constants;

public class MethodPaymentSelectionActivity extends AppCompatActivity {
    private ActivitySelectPaymentMethodBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectPaymentMethodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        eventHandling();
    }

    private void eventHandling() {
        binding.tvOnsite.setOnClickListener(v -> paymentClick(binding.tvOnsite.getText().toString()));
        binding.tvZalopay.setOnClickListener(v -> paymentClick(binding.tvZalopay.getText().toString()));
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void paymentClick(String paymentMethod) {
        Intent intent = new Intent(this, PaymentInformationActivity.class);
        intent.putExtra(Constants.KEY_PAYMENT_METHOD, paymentMethod);
        Doctor selectedDoctor = (Doctor) getIntent().getSerializableExtra(Constants.KEY_DOCTOR);
        Department selectedDepartment = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
        HealthFacility selectedHealthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
        ScheduleExclude selectedSchedule = (ScheduleExclude) getIntent().getSerializableExtra(Constants.KEY_DATE);
        String selectedTime = getIntent().getStringExtra(Constants.KEY_TIME);
        PatientProfile selectedPatient = (PatientProfile) getIntent().getSerializableExtra(Constants.KEY_PATIENT_ID);
        intent.putExtra(Constants.KEY_DATE, selectedSchedule);
        intent.putExtra(Constants.KEY_DOCTOR, selectedDoctor);
        intent.putExtra(Constants.KEY_DEPARTMENT, selectedDepartment);
        intent.putExtra(Constants.KEY_HEALTH_FACILITY, selectedHealthFacility);
        intent.putExtra(Constants.KEY_TIME, selectedTime);
        intent.putExtra(Constants.KEY_PATIENT_ID, selectedPatient);
        startActivity(intent);
    }
}