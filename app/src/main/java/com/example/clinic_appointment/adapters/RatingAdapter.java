package com.example.clinic_appointment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.databinding.ItemContainerRatingBinding;
import com.example.clinic_appointment.models.Rating.Rating;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ItemViewHolder> {
    private final List<Rating> ratings;

    public RatingAdapter(List<Rating> ratings) {
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(ItemContainerRatingBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setData(ratings.get(position));
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerRatingBinding binding;

        public ItemViewHolder(@NonNull ItemContainerRatingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Rating rating) {
            binding.tvName.setText(rating.getPostedBy().getFullName());
            binding.tvComment.setText(rating.getComment());
            int starNumbers = rating.getStar();
            ImageView[] starImageViews = {
                    binding.ivStar5,
                    binding.ivStar4,
                    binding.ivStar3,
                    binding.ivStar2,
                    binding.ivStar1
            };
            for (int i = 0; i < starImageViews.length; i++) {
                starImageViews[i].setVisibility(starNumbers >= (5 - i) ? View.VISIBLE : View.GONE);
            }
        }
    }
}
