package com.example.clinic_appointment.models.User;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserResponse implements Serializable {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private User user;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
