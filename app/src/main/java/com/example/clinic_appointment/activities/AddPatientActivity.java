package com.example.clinic_appointment.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.databinding.ActivityAddPatientBinding;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPatientActivity extends AppCompatActivity {

    private ActivityAddPatientBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getIntent().getStringExtra("name") != null) {
            binding.etFullName.setText(getIntent().getStringExtra("name"));
            binding.etPhoneNumber.setText(getIntent().getStringExtra("phone"));
            binding.btRegister.setText("Cập nhật hồ sơ");
            binding.btRemove.setVisibility(View.VISIBLE);
        }

        eventHandling();
    }

    private void eventHandling() {
        binding.etDateOfBirth.listen();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.btRegister.setOnClickListener(v -> {
            if (getIntent().getStringExtra("name") != null) {
                updateAccount();
            } else {
                createNewAccount();
            }
        });
        binding.btRemove.setOnClickListener(v -> {
            deleteAccount();
        });
    }

    private void createNewAccount() {
        Log.d("TAG", "createNewAccount: ");
        String fullName = binding.etFullName.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        String dateOfBirth = Objects.requireNonNull(binding.etDateOfBirth.getText()).toString();
        if (fullName.isEmpty() || phoneNumber.isEmpty() ||
                (!binding.rbMale.isChecked() && !binding.rbFemale.isChecked()) || binding.etDateOfBirth.getText() == null) {
            makeSnackbar(getString(R.string.not_full_fill_yet));
        } else {
            ProgressDialog progressDialog = new ProgressDialog(AddPatientActivity.this);
            progressDialog.show();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            long timestamp = 0;
            try {
                Date date = dateFormat.parse(dateOfBirth);
                timestamp = date.getTime() + 20 * 3600 * 1000;
                System.out.println("Timestamp: " + timestamp);
            } catch (ParseException e) {
                System.out.println("Chuỗi ngày tháng không hợp lệ");
            }
            Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(this).addPatientProfile(fullName, phoneNumber, binding.rbFemale.isChecked() ? "FEMALE" : "MALE", timestamp);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Log.d("OK", "OK");
                    if (response.isSuccessful()) {
                        progressDialog.dismiss();
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                    makeSnackbar(getString(R.string.something_wrong_happened));
                }
            });
        }
    }


    private void updateAccount() {
        String fullName = binding.etFullName.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        String dateOfBirth = Objects.requireNonNull(binding.etDateOfBirth.getText()).toString();
        if (fullName.isEmpty() || phoneNumber.isEmpty() ||
                (!binding.rbMale.isChecked() && !binding.rbFemale.isChecked()) || binding.etDateOfBirth.getText() == null) {
            makeSnackbar(getString(R.string.not_full_fill_yet));
        } else {
            ProgressDialog progressDialog = new ProgressDialog(AddPatientActivity.this);
            progressDialog.show();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            long timestamp = 0;
            try {
                Date date = dateFormat.parse(dateOfBirth);
                timestamp = date.getTime() + 20 * 3600 * 1000;
                System.out.println("Timestamp: " + timestamp);
            } catch (ParseException e) {
                System.out.println("Chuỗi ngày tháng không hợp lệ");
            }
            Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(this).updatePatientProfile(getIntent().getStringExtra("id"), fullName, phoneNumber, binding.rbFemale.isChecked() ? "FEMALE" : "MALE", timestamp);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Log.d("OK", "OK");
                    if (response.isSuccessful()) {
                        progressDialog.dismiss();
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                    makeSnackbar(getString(R.string.something_wrong_happened));
                }
            });
        }
    }

    private void deleteAccount() {
        ProgressDialog progressDialog = new ProgressDialog(AddPatientActivity.this);
        progressDialog.show();
        Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(this).deletePatientProfile(getIntent().getStringExtra("id"));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d("OK", "OK");
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    setResult(RESULT_OK);
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                makeSnackbar(getString(R.string.something_wrong_happened));
            }
        });
    }

    private void makeSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, 10000)
                .setBackgroundTint(getColor(R.color.colorErrorBackground))
                .setTextColor(getColor(R.color.colorBlack))
                .setAction(getString(R.string.accept), v -> binding.nestedScrollView.smoothScrollTo(0, binding.getRoot().getTop()))
                .setActionTextColor(getColor(R.color.colorAction))
                .show();
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
}