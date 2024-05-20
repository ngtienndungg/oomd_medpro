package com.example.clinic_appointment.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.AddressAdapter;
import com.example.clinic_appointment.adapters.SelectHealthFacilityAdapter;
import com.example.clinic_appointment.databinding.ActivitySelectHealthFacilityBinding;
import com.example.clinic_appointment.listeners.HealthFacilityListener;
import com.example.clinic_appointment.listeners.ProvinceListener;
import com.example.clinic_appointment.models.Address.VietnamProvinceResponse;
import com.example.clinic_appointment.models.HealthFacility.HealthFacilitiesResponse;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthFacilitySelectionActivity extends AppCompatActivity implements HealthFacilityListener {
    private static List<VietnamProvinceResponse.VietnamProvince> provinces = new ArrayList<>();
    private static List<HealthFacility> originalHealthFacilities = new ArrayList<>();
    private static List<HealthFacility> dynamicHealthFacilities;
    private final Handler handler = new Handler();
    private final long SEARCH_DELAY_MILLIS = 300;
    private ActivitySelectHealthFacilityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectHealthFacilityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        eventHandling();
    }

    private void initiate() {
        Call<HealthFacilitiesResponse> call = RetrofitClient.getPublicAppointmentService().getAllHealthFacilities();
        call.enqueue(new Callback<HealthFacilitiesResponse>() {
            @Override
            public void onResponse(@NonNull Call<HealthFacilitiesResponse> call, @NonNull Response<HealthFacilitiesResponse> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    originalHealthFacilities = response.body().getHealthFacilities();
                    dynamicHealthFacilities = new ArrayList<>(originalHealthFacilities);
                    SelectHealthFacilityAdapter adapter = new SelectHealthFacilityAdapter(dynamicHealthFacilities, HealthFacilitySelectionActivity.this, getApplicationContext());
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

        Call<VietnamProvinceResponse> callProvince = RetrofitClient.getProvinceApiService().getEntireProvinces();
        callProvince.enqueue(new Callback<VietnamProvinceResponse>() {
            @Override
            public void onResponse(@NonNull Call<VietnamProvinceResponse> call, @NonNull Response<VietnamProvinceResponse> response) {
                if (response.body() != null) {
                    provinces = response.body().getProvinces();
                    VietnamProvinceResponse.VietnamProvince allMatch = new VietnamProvinceResponse.VietnamProvince();
                    allMatch.setProvinceName(getString(R.string.all_province_city));
                    provinces.add(0, allMatch);
                }
            }

            @Override
            public void onFailure(@NonNull Call<VietnamProvinceResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void displayError() {
        binding.pbLoading.setVisibility(View.GONE);
        binding.llError.setVisibility(View.VISIBLE);
    }

    private void eventHandling() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.etCity.setOnClickListener(v -> {
            ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
            modalBottomSheet.setListener(this);
            modalBottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });
        binding.etSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> search(editable.toString().trim(), binding.etCity.getText().toString().trim()), SEARCH_DELAY_MILLIS);
            }
        });
    }

    private void search(String healthFacilityName, String provinceName) {
        dynamicHealthFacilities.clear();
        for (HealthFacility healthFacility : originalHealthFacilities) {
            if (healthFacility.getName().toLowerCase().contains(healthFacilityName)
                    && healthFacility.getAddress().getProvince().contains(provinceName)) {
                dynamicHealthFacilities.add(healthFacility);
            }
        }
        displayList();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayList() {
        if (dynamicHealthFacilities.size() > 0) {
            binding.tvNotFound.setVisibility(View.GONE);
            Objects.requireNonNull(binding.rvHealthFacility.getAdapter()).notifyDataSetChanged();
        } else {
            binding.tvNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(HealthFacility healthFacility) {
        if (getIntent().getStringExtra(Constants.KEY_SOURCE_ACTIVITY) == null) {
            if (getIntent().getStringExtra(Constants.KEY_ACTION) == null) {
                Intent intent = new Intent(this, DepartmentSelectionActivity.class);
                intent.putExtra(Constants.KEY_HEALTH_FACILITY, healthFacility);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, HealthFacilityInformationActivity.class); //This
                intent.putExtra(Constants.KEY_HEALTH_FACILITY, healthFacility);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra(Constants.KEY_HEALTH_FACILITY, healthFacility);
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onProvinceSelect(String provinceName) {
        search(binding.etSearchName.getText().toString().trim(), provinceName);
        binding.etCity.setText(provinceName);
        displayList();
    }

    public static class ModalBottomSheet extends BottomSheetDialogFragment implements ProvinceListener {
        private HealthFacilityListener listener;

        public void setListener(HealthFacilityListener listener) {
            this.listener = listener;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            int PROVINCE_NUMBER = 64;
            View view = inflater.inflate(R.layout.layout_provinces, container, false);
            RecyclerView rvProvinces = view.findViewById(R.id.rvProvinces);
            TextView tvError = view.findViewById(R.id.tvError);
            if (provinces.size() < PROVINCE_NUMBER) {
                tvError.setVisibility(View.VISIBLE);
            } else {
                rvProvinces.setAdapter(new AddressAdapter(provinces, this));
                rvProvinces.setVisibility(View.VISIBLE);
            }
            return view;
        }

        @Override
        public void onClick(VietnamProvinceResponse.VietnamProvince province) {
            listener.onProvinceSelect(province.getProvinceName().equals(getString(R.string.all_province_city)) ? "" : province.getProvinceName());
            dismiss();
        }
    }
}