package com.example.clinic_appointment.models.PatientProfile;

import com.example.clinic_appointment.utilities.Constants;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class PatientProfile implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phone")
    private String phoneNumber;
    @SerializedName("dob")
    private Date dateOfBirth;
    @SerializedName("gender")
    private String gender;

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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
