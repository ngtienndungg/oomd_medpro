package com.example.clinic_appointment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clinic_appointment.databinding.ItemContainterSelectHealthFacilityBinding;
import com.example.clinic_appointment.listeners.HealthFacilityListener;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;

import java.util.List;

public class SelectHealthFacilityAdapter extends RecyclerView.Adapter<SelectHealthFacilityAdapter.ItemViewHolder> {
    private final HealthFacilityListener listener;
    private final List<HealthFacility> healthFacilities;
    private final Context context;

    public SelectHealthFacilityAdapter(List<HealthFacility> healthFacilities, HealthFacilityListener listener, Context context) {
        this.healthFacilities = healthFacilities;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(ItemContainterSelectHealthFacilityBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectHealthFacilityAdapter.ItemViewHolder holder, int position) {
        holder.setData(healthFacilities.get(position), context);
        holder.binding.getRoot().setOnClickListener(v -> listener.onClick(healthFacilities.get(holder.getBindingAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return healthFacilities.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainterSelectHealthFacilityBinding binding;

        public ItemViewHolder(@NonNull ItemContainterSelectHealthFacilityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(HealthFacility healthFacility, Context context) {
            binding.tvName.setText(healthFacility.getName());
            binding.tvAddress.setText(healthFacility.getAddressString());
            Glide.with(context).load(healthFacility.getImage()).centerCrop().into(binding.ivImage);
        }
    }
}
