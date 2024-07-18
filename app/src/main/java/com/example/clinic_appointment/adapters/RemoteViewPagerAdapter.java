package com.example.clinic_appointment.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.clinic_appointment.fragments.ConversationFragment;
import com.example.clinic_appointment.fragments.SelectClinicFragment;

public class RemoteViewPagerAdapter extends FragmentStateAdapter {

    public RemoteViewPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new SelectClinicFragment();
        }
        return new ConversationFragment();
    }
}
