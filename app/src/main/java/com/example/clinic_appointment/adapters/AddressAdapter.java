package com.example.clinic_appointment.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.databinding.ItemContainerAddressBinding;
import com.example.clinic_appointment.listeners.ProvinceListener;
import com.example.clinic_appointment.models.Address.VietnamProvinceResponse;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ItemViewHolder> {
    private final List<VietnamProvinceResponse.VietnamProvince> provinces;
    private final ProvinceListener listener;

    public AddressAdapter(List<VietnamProvinceResponse.VietnamProvince> provinces, ProvinceListener listener) {
        this.provinces = provinces;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(ItemContainerAddressBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setData(provinces.get(position));
        holder.binding.getRoot().setOnClickListener(v -> listener.onClick(provinces.get(holder.getBindingAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return provinces.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerAddressBinding binding;

        public ItemViewHolder(@NonNull ItemContainerAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(VietnamProvinceResponse.VietnamProvince province) {
            binding.tvAddress.setText(province.getProvinceName());
        }
    }
}
