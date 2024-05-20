package com.example.clinic_appointment.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.databinding.ItemContainterSelectDepartmentBinding;
import com.example.clinic_appointment.listeners.DepartmentListener;
import com.example.clinic_appointment.models.Department.Department;

import java.util.List;

public class SelectDepartmentAdapter extends RecyclerView.Adapter<SelectDepartmentAdapter.ItemViewHolder> {
    private final DepartmentListener listener;
    private final List<Department> departments;

    public SelectDepartmentAdapter(DepartmentListener listener, List<Department> departments) {
        this.listener = listener;
        this.departments = departments;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(ItemContainterSelectDepartmentBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setData(departments.get(position));
        holder.binding.getRoot().setOnClickListener(v -> listener.onClick(departments.get(holder.getBindingAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainterSelectDepartmentBinding binding;

        public ItemViewHolder(@NonNull ItemContainterSelectDepartmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Department department) {
            binding.tvDepartment.setText(department.getName());
        }
    }
}
