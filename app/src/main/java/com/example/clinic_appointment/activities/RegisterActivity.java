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
import com.example.clinic_appointment.databinding.ActivityRegisterBinding;
import com.example.clinic_appointment.models.User.UserResponse;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private final int PASSWORD_LEAST_LENGTH = 6;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        eventHandling();
    }

    private void eventHandling() {
        binding.etDateOfBirth.listen();
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.btRegister.setOnClickListener(v -> createNewAccount());
    }

    private void createNewAccount() {
        String fullName = binding.etFullName.getText().toString().trim();
        String password = binding.etPassword.getText().toString();
        String repeatPassword = binding.etRepeatPassword.getText().toString();
        String email = binding.etEmail.getText().toString().trim();
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String dateOfBirth = Objects.requireNonNull(binding.etDateOfBirth.getText()).toString();
        if (fullName.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() ||
                (!binding.rbMale.isChecked() && !binding.rbFemale.isChecked()) || binding.etDateOfBirth.getText() == null) {
            makeSnackbar(getString(R.string.not_full_fill_yet));
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            makeSnackbar(getString(R.string.invalid_phone_number));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            makeSnackbar(getString(R.string.invalid_email));
        } else if (password.length() < PASSWORD_LEAST_LENGTH) {
            makeSnackbar(getString(R.string.password_must_have_least_six));
        } else if (!password.equals(repeatPassword)) {
            makeSnackbar(getString(R.string.password_and_repeat_no_match));
        } else {
            ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.show();
            Call<UserResponse> call = RetrofitClient.getPublicAppointmentService().register(email, password, fullName, phoneNumber, address, binding.rbFemale.isChecked() ? "FEMALE" : "MALE", dateOfBirth);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                    Log.d("OK", "OK");
                    if (response.isSuccessful()) {
                        progressDialog.dismiss();
                        setResult(RESULT_OK);
                        onBackPressed();
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