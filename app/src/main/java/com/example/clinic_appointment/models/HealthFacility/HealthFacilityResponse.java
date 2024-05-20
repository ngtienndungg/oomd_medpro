package com.example.clinic_appointment.models.HealthFacility;

import com.google.gson.annotations.SerializedName;

public class HealthFacilityResponse {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private HealthFacility healthFacility;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public HealthFacility getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(HealthFacility healthFacility) {
        this.healthFacility = healthFacility;
    }
}
