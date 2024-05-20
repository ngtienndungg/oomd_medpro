package com.example.clinic_appointment.models.Rating;

import com.example.clinic_appointment.models.User.User;
import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("star")
    private int star;
    @SerializedName("postedBy")
    private User postedBy;
    @SerializedName("comment")
    private String comment;

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public User getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(User postedBy) {
        this.postedBy = postedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
