package com.example.clinic_appointment.models.Address;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable {
    @SerializedName("province")
    private String province;
    @SerializedName("district")
    private String district;
    @SerializedName("ward")
    private String ward;
    @SerializedName("detail")
    private String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }
}
