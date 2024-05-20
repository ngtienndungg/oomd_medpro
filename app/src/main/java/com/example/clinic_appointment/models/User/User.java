package com.example.clinic_appointment.models.User;

import com.example.clinic_appointment.utilities.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("email")
    private String email;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("mobile")
    private String phoneNumber;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("role")
    private int userRole;
    @SerializedName("address")
    private String address;
    @SerializedName("refreshToken")
    private String refreshToken;
    @SerializedName("gender")
    private String gender;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGenderVietnamese() {
        if (Objects.equals(gender, Constants.GENDER_FEMALE_ENG)) {
            return "Nữ";
        } else if (Objects.equals(gender, Constants.GENDER_MALE_ENG)) {
            return "Nam";
        }
        return null;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }
}
