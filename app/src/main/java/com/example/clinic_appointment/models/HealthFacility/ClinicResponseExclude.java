package com.example.clinic_appointment.models.HealthFacility;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClinicResponseExclude implements Serializable {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private ClinicExclude clinicExclude;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public ClinicExclude getHealthFacility() {
        return clinicExclude;
    }

    public void setHealthFacility(ClinicExclude clinicExclude) {
        this.clinicExclude = clinicExclude;
    }
}
