package com.example.clinic_appointment.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.databinding.ActivityLoginBinding;
import com.example.clinic_appointment.models.User.UserResponse;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.SharedPrefs;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final SharedPrefs sharedPrefs = SharedPrefs.getInstance();
    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Integer resultCode = result.getResultCode();
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, getString(R.string.register_successfully), Toast.LENGTH_SHORT).show();
                }
            });
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        eventHandling();
    }

    private void eventHandling() {
        binding.tvCreateAccount.setOnClickListener(v -> mStartForResult.launch(new Intent(this, RegisterActivity.class)));
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.btLogin.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();
            Call<UserResponse> call = RetrofitClient.getPublicAppointmentService()
                    .login(binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                    UserResponse userResponse = response.body();
                    if (response.body() != null && response.body().isSuccess()) {
                        sharedPrefs.putData(Constants.KEY_ACCESS_TOKEN, response.headers().get(Constants.HEADER_AUTHORIZATION));
                        sharedPrefs.putData(Constants.KEY_REFRESH_TOKEN, Objects.requireNonNull(response.headers().get(Constants.HEADER_SET_COOKIE)).split(Constants.REGEX_SEMICOLON)[0]);
                        sharedPrefs.putData(Constants.KEY_CURRENT_NAME, userResponse != null ? userResponse.getUser().getFullName() : null);
                        sharedPrefs.putData(Constants.KEY_CURRENT_PHONE_NUMBER, userResponse != null ? userResponse.getUser().getPhoneNumber() : null);
                        sharedPrefs.putData(Constants.KEY_CURRENT_EMAIL, userResponse != null ? userResponse.getUser().getEmail() : null);
                        sharedPrefs.putData(Constants.KEY_CURRENT_UID, userResponse != null ? userResponse.getUser().getId() : null);
                        sharedPrefs.putData(Constants.KEY_USER_ROLE, userResponse != null ? userResponse.getUser().getUserRole() : null);
                        progressDialog.dismiss();
                        setResult(RESULT_OK);
                        onBackPressed();
                    } else {
                        progressDialog.dismiss();
                        Snackbar.make(v, R.string.invalid_email_password, BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                    Snackbar.make(v, R.string.something_wrong, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });
        });
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