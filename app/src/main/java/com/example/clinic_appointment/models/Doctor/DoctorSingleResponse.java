package com.example.clinic_appointment.models.Doctor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DoctorSingleResponse implements Serializable {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private Doctor doctors;
    @SerializedName("counts")
    private int count;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Doctor getDoctors() {
        return doctors;
    }

    public void setDoctors(Doctor doctors) {
        this.doctors = doctors;
    }
}
