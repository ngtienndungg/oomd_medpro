package com.example.clinic_appointment.models.HealthFacility;

import com.example.clinic_appointment.models.Address.Address;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.Rating.Rating;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HealthFacility implements Serializable {
    @SerializedName("ratings")
    List<Rating> ratings;
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("image")
    private String image;
    @SerializedName("address")
    private Address address;
    @SerializedName("specialtyID")
    private List<Department> departments;
    @SerializedName("totalRatings")
    private float averageRating;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public String getAddressString() {
        return address.getDetail() + ", " + address.getWard() + ", " + address.getDistrict() + ", " + address.getProvince();
    }
}
