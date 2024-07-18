package com.example.clinic_appointment.models.Rating;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RatingExclude implements Serializable {
    @SerializedName("star")
    private int star;
    @SerializedName("postedBy")
    private String postedBy;
    @SerializedName("comment")
    private String comment;

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
