package com.example.clinic_appointment.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.databinding.ActivityUpdateProfileBinding;
import com.example.clinic_appointment.models.User.UserResponse;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.SharedPrefs;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private ActivityUpdateProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        eventHandling();
    }

    private void initiate() {
        Call<UserResponse> call = RetrofitClient.getAuthenticatedAppointmentService(this).getCurrent(SharedPrefs.getInstance().getData(Constants.KEY_ACCESS_TOKEN, String.class));
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    SharedPrefs.getInstance().putData(Constants.KEY_CURRENT_NAME, Objects.requireNonNull(response.body()).getUser().getFullName());
                    binding.etFullName.setText(response.body().getUser().getFullName());
                    binding.etPhoneNumber.setText(response.body().getUser().getPhoneNumber());
                    binding.etEmail.setText(response.body().getUser().getEmail());
                    binding.etAddress.setText(response.body().getUser().getAddress());
                    if (response.body().getUser().getGender().equals("MALE")) {
                        binding.rbMale.setChecked(true);
                    } else {
                        binding.rbFemale.setChecked(true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void eventHandling() {
        binding.btRegister.setOnClickListener(v -> updateUser());
    }

    private void updateUser() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() ||
                (!binding.rbMale.isChecked() && !binding.rbFemale.isChecked()) || binding.etDateOfBirth.getText() == null) {
            makeSnackbar(getString(R.string.not_full_fill_yet));
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            makeSnackbar(getString(R.string.invalid_phone_number));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            makeSnackbar(getString(R.string.invalid_email));
        } else {
            ProgressDialog progressDialog = new ProgressDialog(UpdateProfileActivity.this);
            progressDialog.show();
            Call<UserResponse> call = RetrofitClient.getAuthenticatedAppointmentService(this).updateUser(fullName, email, binding.rbFemale.isChecked() ? "FEMALE" : "MALE", address);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                    Log.d("OK", "OK");
                    if (response.isSuccessful()) {
                        progressDialog.dismiss();
                        initiate();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                    makeSnackbar(getString(R.string.something_wrong_happened));
                }
            });
        }
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