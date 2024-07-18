package com.example.clinic_appointment.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.RatingAdapter;
import com.example.clinic_appointment.databinding.ActivityHealthFacilityInformationBinding;
import com.example.clinic_appointment.databinding.LayoutDialogRateBinding;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.models.HealthFacility.HealthFacilityResponse;
import com.example.clinic_appointment.models.Rating.Rating;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.SharedPrefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthFacilityInformationActivity extends AppCompatActivity {
    ImageView[] starImageRootViews;
    private ActivityHealthFacilityInformationBinding binding;
    private HealthFacility healthFacility;
    private AlertDialog alertDialog;
    private int selectedStar = 5;
    private int starNumbers = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthFacilityInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        starImageRootViews = new ImageView[]{
                binding.ivStar5,
                binding.ivStar4,
                binding.ivStar3,
                binding.ivStar2,
                binding.ivStar1
        };
        initiate();
        eventHandling();
    }

    private void initiate() {
        if (SharedPrefs.getInstance().getData(Constants.KEY_ACCESS_TOKEN, String.class).equals("")) {
            binding.tvYourRating.setVisibility(View.GONE);
            binding.tvYourComment.setVisibility(View.GONE);
            binding.llYourStar.setVisibility(View.GONE);
            binding.tvChange.setVisibility(View.GONE);
        }
        healthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
        if (healthFacility != null) {
            binding.tvName.setText(healthFacility.getName());
            binding.tvDescription.setText(Html.fromHtml(healthFacility.getDescription(), Html.FROM_HTML_MODE_COMPACT));
            binding.tvAddress.setText(healthFacility.getAddressString());
            Glide.with(this).load(healthFacility.getImage()).centerCrop().into(binding.ivLogo);
            Call<HealthFacilityResponse> call = RetrofitClient.getPublicAppointmentService().getHealthFacilityById(healthFacility.getId());
            call.enqueue(new Callback<HealthFacilityResponse>() {
                @Override
                public void onResponse(@NonNull Call<HealthFacilityResponse> call, @NonNull Response<HealthFacilityResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getHealthFacility().getRatings().size() == 0) {
                            binding.tvNoRating.setVisibility(View.VISIBLE);
                        } else {
                            List<Rating> ratings = new ArrayList<>(response.body().getHealthFacility().getRatings());
                            for (Rating rating : ratings) {
                                if (Objects.equals(rating.getPostedBy().getId(), SharedPrefs.getInstance().getData(Constants.KEY_CURRENT_UID, String.class))) {
                                    binding.tvYourComment.setVisibility(View.VISIBLE);
                                    binding.llYourStar.setVisibility(View.VISIBLE);
                                    binding.tvChange.setText(getString(R.string.change_rating));
                                    starNumbers = rating.getStar();

                                    for (int i = 0; i < starImageRootViews.length; i++) {
                                        starImageRootViews[i].setVisibility(starNumbers >= (5 - i) ? View.VISIBLE : View.GONE);
                                    }
                                    binding.tvYourComment.setText(rating.getComment());
                                    ratings.remove(rating);
                                    break;
                                }
                            }
                            RatingAdapter ratingAdapter = new RatingAdapter(ratings);
                            binding.tvRating.append("(" + response.body().getHealthFacility().getAverageRating() + ")");
                            binding.rvRatings.setAdapter(ratingAdapter);
                            binding.rvRatings.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<HealthFacilityResponse> call, @NonNull Throwable t) {

                }
            });
        }
    }

    private void eventHandling() {
        binding.tvDepartmentList.setOnClickListener(v -> {
            Intent intent = new Intent(this, DepartmentSelectionActivity.class);
            intent.putExtra(Constants.KEY_HEALTH_FACILITY, healthFacility);
            startActivity(intent);
        });
        binding.tvDoctorList.setOnClickListener(v -> {
            Intent intent = new Intent(this, DoctorSelectionActivity.class);
            intent.putExtra(Constants.KEY_HEALTH_FACILITY, healthFacility);
            startActivity(intent);
        });
        binding.tvChange.setOnClickListener(v -> {
            displayConfirmationDialog();
        });
    }

    private void displayConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutDialogRateBinding confirmationDialogBinding = LayoutDialogRateBinding.inflate(getLayoutInflater());
        builder.setView(confirmationDialogBinding.getRoot());
        confirmationDialogBinding.etComment.setText(binding.tvYourComment.getText().toString());
        final ImageView[] starImageViews = {
                confirmationDialogBinding.ivStar1,
                confirmationDialogBinding.ivStar2,
                confirmationDialogBinding.ivStar3,
                confirmationDialogBinding.ivStar4,
                confirmationDialogBinding.ivStar5};

        final int numStars = starImageViews.length;

        for (int i = 0; i < numStars; i++) {
            final int starPosition = i;
            starImageViews[i].setOnClickListener(v -> {
                selectedStar = starPosition + 1;
                for (int j = 0; j < numStars; j++) {
                    starImageViews[j].setImageResource(j < selectedStar ? R.mipmap.ic_gold_star : R.mipmap.ic_star_white);
                    Log.d("Star test", String.valueOf(selectedStar));
                }
            });
        }
        starImageViews[starNumbers - 1].performClick();
        alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        confirmationDialogBinding.tvTitle.setText(getString(R.string.rate));
        confirmationDialogBinding.tvPositiveAction.setOnClickListener(v -> {
            confirmationDialogBinding.tvPositiveAction.setVisibility(View.GONE);
            confirmationDialogBinding.tvNegativeAction.setVisibility(View.GONE);
            confirmationDialogBinding.pbLoading.setVisibility(View.VISIBLE);
            Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(this).rateClinic(
                    selectedStar,
                    confirmationDialogBinding.etComment.getText().toString(),
                    healthFacility.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    confirmationDialogBinding.pbLoading.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        starNumbers = selectedStar;
                        alertDialog.dismiss();
                        binding.tvYourComment.setText(confirmationDialogBinding.etComment.getText().toString());
                        for (int i = 0; i < starImageRootViews.length; i++) {
                            starImageRootViews[i].setVisibility(selectedStar >= (5 - i) ? View.VISIBLE : View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    confirmationDialogBinding.pbLoading.setVisibility(View.GONE);
                }
            });
        });
        confirmationDialogBinding.ivClose.setOnClickListener(v -> {
            alertDialog.dismiss();
            alertDialog = null;
        });
        confirmationDialogBinding.tvNegativeAction.setOnClickListener(v -> {
            alertDialog.dismiss();
            alertDialog = null;
        });
        alertDialog.show();
    }
}