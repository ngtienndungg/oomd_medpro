package com.example.clinic_appointment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.viewpager2.widget.ViewPager2;

import com.example.clinic_appointment.adapters.RemoteViewPagerAdapter;
import com.example.clinic_appointment.databinding.ActivityRemoteBinding;
import com.example.clinic_appointment.utilities.JwtTokenCreator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RemoteActivity extends BaseActivity {

    private ActivityRemoteBinding binding;
    private String token;
    public static StringeeClient client;
    public static Map<String, StringeeCall2> call2Map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        token = JwtTokenCreator.createJwtToken();
        initStringeeConnection();
    }

    private void initiate() {
        RemoteViewPagerAdapter viewPagerAdapter = new RemoteViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                TabLayout.Tab tab = binding.tabLayout.getTabAt(position);
                if (tab != null) {
                    tab.select();
                }
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Đặt lịch hẹn");
                    break;
                case 1:
                    tab.setText("Tin nhắn & cuộc gọi");
                    break;
            }
        }).attach();
    }

    private void initStringeeConnection() {
        client = new StringeeClient(this);
        client.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(StringeeClient stringeeClient, boolean b) {
                Log.d("TokenCheck", "Connected as " + stringeeClient.getUserId());
            }

            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean b) {

            }

            @Override
            public void onIncomingCall(StringeeCall stringeeCall) {

            }

            @Override
            public void onIncomingCall2(StringeeCall2 stringeeCall2) {
                runOnUiThread(()->{
                    call2Map.put(stringeeCall2.getCallId(), stringeeCall2);
                    Intent intent = new Intent(RemoteActivity.this, CallActivity.class);
                    intent.putExtra("callId", stringeeCall2.getCallId());
                    intent.putExtra("isIncomingCall", true);
                    startActivity(intent);
                });
            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, StringeeError stringeeError) {

            }

            @Override
            public void onRequestNewToken(StringeeClient stringeeClient) {

            }

            @Override
            public void onCustomMessage(String s, JSONObject jsonObject) {

            }

            @Override
            public void onTopicMessage(String s, JSONObject jsonObject) {

            }
        });
        Log.d("TokenCheck", token);
        client.connect(token);
    }
}