package com.example.clinic_appointment.models.Doctor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DoctorResponse implements Serializable {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private List<Doctor> doctors;
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

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }
}
