package com.example.clinic_appointment.models.Department;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DepartmentResponse implements Serializable {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private List<Department> departments;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
