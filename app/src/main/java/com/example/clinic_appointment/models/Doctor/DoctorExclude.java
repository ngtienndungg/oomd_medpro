package com.example.clinic_appointment.models.Doctor;

import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.HealthFacility.ClinicExclude;
import com.example.clinic_appointment.models.Rating.Rating;
import com.example.clinic_appointment.models.User.User;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DoctorExclude implements Serializable {
    @SerializedName("_id")
    private User doctorInformation;

    @SerializedName("specialtyID")
    private Department departmentInformation;
    @SerializedName("clinicID")
    private ClinicExclude healthFacility;

    @SerializedName("description")
    private String description;
    @SerializedName("position")
    private String academicLevel;
    @SerializedName("schedules")
    private int[] doctorSchedules;
    @SerializedName("ratings")
    private List<Rating> ratings;
    @SerializedName("totalRatings")
    private float averageRating;

    public int[] getDoctorSchedules() {
        return doctorSchedules;
    }

    public void setDoctorSchedules(int[] doctorSchedules) {
        this.doctorSchedules = doctorSchedules;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public String getAcademicLevel() {
        return academicLevel;
    }

    public void setAcademicLevel(String academicLevel) {
        this.academicLevel = academicLevel;
    }

    public User getDoctorInformation() {
        return doctorInformation;
    }

    public void setDoctorInformation(User doctorInformation) {
        this.doctorInformation = doctorInformation;
    }

    public Department getDepartmentInformation() {
        return departmentInformation;
    }

    public void setDepartmentInformation(Department departmentInformation) {
        this.departmentInformation = departmentInformation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClinicExclude getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(ClinicExclude healthFacility) {
        this.healthFacility = healthFacility;
    }

    public String getScheduleString() {
        if (doctorSchedules == null || doctorSchedules.length == 0) {
            return "Không có lịch khám";
        } else {
            StringBuilder resultStringBuilder = new StringBuilder();
            for (int dayOfWeek : doctorSchedules) {
                switch (dayOfWeek) {
                    case 1:
                        resultStringBuilder.append(", hai");
                        break;
                    case 2:
                        resultStringBuilder.append(", ba");
                        break;
                    case 3:
                        resultStringBuilder.append(", tư");
                        break;
                    case 4:
                        resultStringBuilder.append(", năm");
                        break;
                    case 5:
                        resultStringBuilder.append(", sáu");
                        break;
                    case 6:
                        resultStringBuilder.append(", bảy");
                        break;
                    case 0:
                        resultStringBuilder.append(", chủ nhật");
                        break;
                }
            }
            String resultString = String.valueOf(resultStringBuilder.delete(0, 2));
            if (resultString.charAt(0) == 'c') {
                return resultString.substring(0, 1).toUpperCase() + resultString.substring(1);
            } else {
                return "Thứ " + resultString;
            }
        }
    }
}
