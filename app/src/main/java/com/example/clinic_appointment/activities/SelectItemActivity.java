package com.example.clinic_appointment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.SelectHealthFacilityAdapter;
import com.example.clinic_appointment.databinding.ActivitySelectItemBinding;
import com.example.clinic_appointment.listeners.HealthFacilityListener;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.Department.DepartmentResponse;
import com.example.clinic_appointment.models.HealthFacility.HealthFacilitiesResponse;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.Searchable;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectItemActivity extends AppCompatActivity implements HealthFacilityListener {
    private ActivitySelectItemBinding binding;
    private String itemType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        eventHandling();
    }

    private void initiate() {
        itemType = getItemType();
        if (itemType.equals(Constants.TYPE_HOSPITAL)) {
            getInitiateHospitalList();
        }
        if (itemType.equals(Constants.TYPE_DEPARTMENT)) {
            getInitiateDepartmentList();
        }
    }

    private String getItemType() {
        return getIntent().getStringExtra(Constants.KEY_ITEM_TYPE);
    }

    private void getInitiateHospitalList() {
        binding.pbLoading.setVisibility(View.VISIBLE);
        binding.tvAllMatch.setText(getString(R.string.from_all_hospital_and_clinic));
        binding.tvResult.setText(getString(R.string.popular_hospital_and_clinic));
        binding.etSearchInput.setHint(R.string.search_hint_select_hospital);
        Call<HealthFacilitiesResponse> call = RetrofitClient.getPublicAppointmentService().getAllHealthFacilities();
        call.enqueue(new Callback<HealthFacilitiesResponse>() {
            @Override
            public void onResponse(@NonNull Call<HealthFacilitiesResponse> call, @NonNull Response<HealthFacilitiesResponse> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    List<HealthFacility> healthFacilities = response.body().getHealthFacilities();
                    SelectHealthFacilityAdapter adapter = new SelectHealthFacilityAdapter(healthFacilities, SelectItemActivity.this, getApplicationContext());
                    binding.rvResult.setAdapter(adapter);
                    binding.rvResult.setVisibility(View.VISIBLE);
                    binding.pbLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HealthFacilitiesResponse> call, @NonNull Throwable t) {
                displayError();
            }
        });
    }

    private void getInitiateDepartmentList() {
        binding.pbLoading.setVisibility(View.VISIBLE);
        binding.tvAllMatch.setText(getString(R.string.from_all_department));
        binding.tvResult.setText(getString(R.string.department_list));
        binding.etSearchInput.setHint(R.string.search_hint_select_department);
        Call<DepartmentResponse> call = RetrofitClient.getPublicAppointmentService().getFilteredDepartment();
        call.enqueue(new Callback<DepartmentResponse>() {
            @Override
            public void onResponse(@NonNull Call<DepartmentResponse> call, @NonNull Response<DepartmentResponse> response) {

            }

            @Override
            public void onFailure(@NonNull Call<DepartmentResponse> call, @NonNull Throwable t) {
                displayError();
            }
        });
    }

    private void eventHandling() {
        binding.tvClose.setOnClickListener(v -> onBackPressed());
        binding.tvAllMatch.setOnClickListener(v -> {
            setResult(Constants.RESULT_ALL_MATCH, new Intent().putExtra(Constants.RETURN_TYPE, itemType));
            onBackPressed();
        });
    }

    private void displayError() {
        binding.pbLoading.setVisibility(View.GONE);
        binding.rlError.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_default, R.anim.slide_out_right);
    }

    public void onSelect(Searchable item) {
        Class<? extends Searchable> itemClass = item.getClass();
        if (itemClass.equals(HealthFacility.class)) {
            setResult(Constants.TYPE_HOSPITAL, item);
        } else if (itemClass.equals(Department.class)) {
            setResult(Constants.TYPE_DEPARTMENT, item);
        }
    }

    private void setResult(String returnType, Searchable item) {
        Intent intent = new Intent();
        intent.putExtra(Constants.RETURN_TYPE, returnType);
        intent.putExtra(Constants.KEY_SELECTED_ITEM, item);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public void onClick(HealthFacility healthFacility) {

    }

    @Override
    public void onProvinceSelect(String name) {

    }
}