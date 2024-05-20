package com.example.clinic_appointment.models.Address;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VietnamProvinceResponse implements Serializable {
    @SerializedName("results")
    private List<VietnamProvince> provinces;

    public List<VietnamProvince> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<VietnamProvince> provinces) {
        this.provinces = provinces;
    }

    public static class VietnamProvince {
        @SerializedName("province_id")
        private String provinceId;
        @SerializedName("province_name")
        private String provinceName;
        @SerializedName("province_type")
        private String provinceType;

        public String getProvinceId() {
            return provinceId;
        }

        public void setProvinceId(String provinceId) {
            this.provinceId = provinceId;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public String getProvinceType() {
            return provinceType;
        }

        public void setProvinceType(String provinceType) {
            this.provinceType = provinceType;
        }
    }
}
