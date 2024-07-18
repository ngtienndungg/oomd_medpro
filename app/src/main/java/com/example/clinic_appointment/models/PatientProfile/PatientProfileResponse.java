package com.example.clinic_appointment.models.PatientProfile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PatientProfileResponse {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private List<PatientProfile> patientProfiles;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<PatientProfile> getPatientProfiles() {
        return patientProfiles;
    }

    public void setPatientProfiles(List<PatientProfile> patientProfiles) {
        this.patientProfiles = patientProfiles;
    }
}
