package com.example.clinic_appointment.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.clinic_appointment.activities.LoginActivity;
import com.example.clinic_appointment.activities.UpdateProfileActivity;
import com.example.clinic_appointment.databinding.FragmentAccountBinding;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    private final SharedPrefs sharedPrefs = SharedPrefs.getInstance();
    private FragmentAccountBinding binding;
    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Integer resultCode = result.getResultCode();
                if (resultCode == Activity.RESULT_OK) {
                    handleIsLoginDisplaying(true);
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(getLayoutInflater());
        initiate();
        eventHandling();
        return binding.getRoot();
    }

    private void initiate() {
        handleIsLoginDisplaying(!sharedPrefs.getData(Constants.KEY_CURRENT_NAME, String.class).equals(""));
    }

    private void eventHandling() {
        binding.llLogout.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.show();
            RetrofitClient.getPublicAppointmentService().logout(SharedPrefs.getInstance().getData(Constants.KEY_REFRESH_TOKEN, String.class)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                }
            });
            sharedPrefs.clear();
            handleIsLoginDisplaying(false);
        });
        binding.tvLogin.setOnClickListener(v -> mStartForResult.launch(new Intent(requireActivity(), LoginActivity.class)));
        binding.llAccountInformation.setOnClickListener(v -> startActivity(new Intent(requireActivity(), UpdateProfileActivity.class)));
    }

    private void handleIsLoginDisplaying(boolean isLogin) {
        if (!isLogin) {
            binding.tvLogin.setVisibility(View.VISIBLE);
            binding.tvName.setVisibility(View.GONE);
        } else {
            binding.tvName.setVisibility(View.VISIBLE);
            binding.tvName.setText(sharedPrefs.getData(Constants.KEY_CURRENT_NAME, String.class));
            binding.tvLogin.setVisibility(View.GONE);
        }
    }
}