package com.example.clinic_appointment.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clinic_appointment.databinding.ItemContainerImageBinding;

import java.util.List;

public class ImageUrlAdapter extends RecyclerView.Adapter<ImageUrlAdapter.ItemViewHolder> {
    private final List<String> imageUrls;

    public ImageUrlAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageUrlAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageUrlAdapter.ItemViewHolder(ItemContainerImageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageUrlAdapter.ItemViewHolder holder, int position) {
        holder.setData(imageUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerImageBinding binding;

        public ItemViewHolder(@NonNull ItemContainerImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(String imageUrl) {
            Glide.with(binding.ivImage.getContext()).load(imageUrl).into(binding.ivImage);
        }
    }
}
