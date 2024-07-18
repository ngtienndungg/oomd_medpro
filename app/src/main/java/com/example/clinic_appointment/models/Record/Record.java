package com.example.clinic_appointment.models.Record;

import java.util.List;

public class Record {
    private boolean success;
    private List<MedicineData> data;
    private int counts;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<MedicineData> getData() {
        return data;
    }

    public void setData(List<MedicineData> data) {
        this.data = data;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public static class MedicineData {
        private String _id;
        private String description;
        private List<Medicine> medicineArr;
        private int totalPrice;
        private String createdAt;
        private String updatedAt;
        private int __v;

        public String getId() {
            return _id;
        }

        public void setId(String _id) {
            this._id = _id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Medicine> getMedicineArr() {
            return medicineArr;
        }

        public void setMedicineArr(List<Medicine> medicineArr) {
            this.medicineArr = medicineArr;
        }

        public int getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(int totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public int getV() {
            return __v;
        }

        public void setV(int __v) {
            this.__v = __v;
        }
    }

    public static class Medicine {
        private MedicineDetail medicineID;
        private String instraction;
        private List<String> dosage;
        private int quantity;
        private int price;
        private boolean isPaid;
        private String _id;

        // Getters and Setters

        public MedicineDetail getMedicineID() {
            return medicineID;
        }

        public void setMedicineID(MedicineDetail medicineID) {
            this.medicineID = medicineID;
        }

        public String getInstraction() {
            return instraction;
        }

        public void setInstraction(String instraction) {
            this.instraction = instraction;
        }

        public List<String> getDosage() {
            return dosage;
        }

        public void setDosage(List<String> dosage) {
            this.dosage = dosage;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public boolean isPaid() {
            return isPaid;
        }

        public void setPaid(boolean isPaid) {
            this.isPaid = isPaid;
        }

        public String getId() {
            return _id;
        }

        public void setId(String _id) {
            this._id = _id;
        }

        public static class MedicineDetail {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }

}
