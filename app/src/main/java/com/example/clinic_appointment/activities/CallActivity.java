package com.example.clinic_appointment.activities;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.clinic_appointment.R;
import com.stringee.call.StringeeCall2;
import com.stringee.call.StringeeCall2.MediaState;
import com.stringee.call.StringeeCall2.SignalingState;
import com.stringee.call.StringeeCall2.StringeeCallListener;
import com.stringee.common.StringeeAudioManager;
import com.stringee.listener.StatusListener;
import com.stringee.video.StringeeVideoTrack;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CallActivity extends AppCompatActivity {
    private TextView tvStatus;
    private View vIncoming;
    private FrameLayout vLocal;
    private FrameLayout vRemote;
    private ImageButton btnSpeaker;
    private ImageButton btnMute;
    private ImageButton btnSwitch;
    private ImageButton btnVideo;
    private ImageButton btnAnswer;
    private ImageButton btnReject;
    private ImageButton btnEnd;

    private StringeeCall2 call;
    private boolean isIncomingCall = false;
    private String to;
    private String callId;
    private SignalingState mSignalingState;
    private MediaState mMediaState;
    private StringeeAudioManager audioManager;
    private boolean isSpeaker = false;
    private boolean isMicOn = true;
    private boolean isVideoOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        tvStatus = findViewById(R.id.tv_status);
        vIncoming = findViewById(R.id.v_incoming);
        vLocal = findViewById(R.id.v_local);
        vRemote = findViewById(R.id.v_remote);
        btnSpeaker = findViewById(R.id.btn_speaker);
        btnSpeaker.setOnClickListener(view -> {
            runOnUiThread(() -> {
                if (audioManager != null) {
                    audioManager.setSpeakerphoneOn(!isSpeaker);
                    isSpeaker = !isSpeaker;
                    btnSpeaker.setBackgroundResource(isSpeaker ? R.drawable.btn_speaker_on : R.drawable.btn_speaker_off);
                }
            });
        });
        btnMute = findViewById(R.id.btn_mute);
        btnMute.setOnClickListener(view -> {
            runOnUiThread(() -> {
                if (call != null) {
                    call.mute(isMicOn);
                    isMicOn = !isMicOn;
                    btnMute.setBackgroundResource(isMicOn ? R.drawable.btn_mic_on : R.drawable.btn_mic_off);
                }
            });
        });
        btnSwitch = findViewById(R.id.btn_switch);
        btnSwitch.setOnClickListener(view -> {
            runOnUiThread(() -> {
                if (call != null) {
                    call.switchCamera(new StatusListener() {
                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            });
        });
        btnVideo = findViewById(R.id.btn_video);
        btnVideo.setOnClickListener(view -> {
            runOnUiThread(() -> {
                if (call != null) {
                    call.enableVideo(!isVideoOn);
                    isVideoOn = !isVideoOn;
                    btnVideo.setBackgroundResource(isVideoOn ? R.drawable.btn_video_on : R.drawable.btn_video_off);
                }
            });
        });
        btnAnswer = findViewById(R.id.btn_answer);
        btnAnswer.setOnClickListener(view -> {
            runOnUiThread(() -> {
                if (call != null) {
                    call.answer(new StatusListener() {
                        @Override
                        public void onSuccess() {

                        }
                    });
                    vIncoming.setVisibility(View.GONE);
                    btnEnd.setVisibility(View.VISIBLE);
                    btnSpeaker.setVisibility(View.VISIBLE);
                    btnMute.setVisibility(View.VISIBLE);
                    btnSwitch.setVisibility(View.VISIBLE);
                    btnVideo.setVisibility(View.VISIBLE);
                }
            });
        });
        btnReject = findViewById(R.id.btn_reject);
        btnReject.setOnClickListener(view -> {
            runOnUiThread(() -> {
                if (call != null) {
                    call.reject(new StatusListener() {
                        @Override
                        public void onSuccess() {

                        }
                    });
                    audioManager.stop();
                    finish();
                }
            });
        });
        btnEnd = findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(view -> {
            if (call != null) {
                call.hangup(new StatusListener() {
                    @Override
                    public void onSuccess() {

                    }
                });
                audioManager.stop();
                finish();
            }
        });

        if (getIntent() != null) {
            isIncomingCall = getIntent().getBooleanExtra("isIncomingCall", false);
            to = getIntent().getStringExtra("to");
            callId = getIntent().getStringExtra("callId");
        }

        vIncoming.setVisibility(isIncomingCall ? View.VISIBLE : View.GONE);
        btnSpeaker.setVisibility(isIncomingCall ? View.GONE : View.VISIBLE);
        btnMute.setVisibility(isIncomingCall ? View.GONE : View.VISIBLE);
        btnSwitch.setVisibility(isIncomingCall ? View.GONE : View.VISIBLE);
        btnVideo.setVisibility(isIncomingCall ? View.GONE : View.VISIBLE);
        btnEnd.setVisibility(isIncomingCall ? View.GONE : View.VISIBLE);

        List<String> lstPermission = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            lstPermission.add(permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(this, permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            lstPermission.add(permission.CAMERA);
        }
        if (VERSION.SDK_INT >= VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                lstPermission.add(permission.BLUETOOTH_CONNECT);
            }
        }
        if (lstPermission.size() > 0) {
            String[] permissions = new String[lstPermission.size()];
            for (int i = 0; i < lstPermission.size(); i++) {
                permissions[i] = lstPermission.get(i);
            }
            ActivityCompat.requestPermissions(this, permissions, 0);
            return;
        }

        initCall();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean isGranted = false;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            } else {
                isGranted = true;
            }
        }
        if (requestCode == 0) {
            if (!isGranted) {
                finish();
            } else {
                initCall();
            }
        }
    }

    private void initCall() {
        if (isIncomingCall) {
            call = RemoteActivity.call2Map.get(callId);
            if (call == null) {
                finish();
                return;
            }
        } else {
            call = new StringeeCall2(RemoteActivity.client, RemoteActivity.client.getUserId(), to);
            call.setVideoCall(true);
        }

        call.setCallListener(new StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall2 stringeeCall, SignalingState signalingState, String s, int i, String s1) {
                runOnUiThread(() -> {
                    mSignalingState = signalingState;
                    switch (signalingState) {
                        case CALLING:
                            tvStatus.setText("Đang gọi");
                            break;
                        case RINGING:
                            tvStatus.setText("Đang gọi đến");
                            break;
                        case ANSWERED:

                            if (mMediaState == MediaState.CONNECTED) {

                            }
                            break;
                        case BUSY:
                            tvStatus.setText("Bận");
                            audioManager.stop();
                            finish();
                            break;
                        case ENDED:

                            audioManager.stop();
                            finish();
                            break;
                    }
                });
            }

            @Override
            public void onError(StringeeCall2 stringeeCall, int i, String s) {
                runOnUiThread(() -> {
                    finish();
                    audioManager.stop();
                });
            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall2 stringeeCall, SignalingState signalingState, String s) {

            }

            @Override
            public void onMediaStateChange(StringeeCall2 stringeeCall, MediaState mediaState) {
                runOnUiThread(() -> {
                    mMediaState = mediaState;
                    if (mediaState == MediaState.CONNECTED) {
                        if (mSignalingState == SignalingState.ANSWERED) {

                        }
                    } else {
                        tvStatus.setText("Retry to connect");
                    }
                });
            }

            @Override
            public void onLocalStream(StringeeCall2 stringeeCall) {
                runOnUiThread(() -> {
                    vLocal.removeAllViews();
                    vLocal.addView(stringeeCall.getLocalView());
                    stringeeCall.renderLocalView(true);
                });
            }

            @Override
            public void onRemoteStream(StringeeCall2 stringeeCall) {
                runOnUiThread(() -> {
                    vRemote.removeAllViews();
                    vRemote.addView(stringeeCall.getRemoteView());
                    stringeeCall.renderRemoteView(false);
                });
            }

            @Override
            public void onVideoTrackAdded(StringeeVideoTrack stringeeVideoTrack) {

            }

            @Override
            public void onVideoTrackRemoved(StringeeVideoTrack stringeeVideoTrack) {

            }

            @Override
            public void onCallInfo(StringeeCall2 stringeeCall, JSONObject jsonObject) {

            }

            @Override
            public void onTrackMediaStateChange(String s, StringeeVideoTrack.MediaType mediaType, boolean b) {

            }
        });

        audioManager = new StringeeAudioManager(this);
        audioManager.start((audioDevice, set) -> {

        });
        audioManager.setSpeakerphoneOn(true);

        if (isIncomingCall) {
            call.ringing(new StatusListener() {
                @Override
                public void onSuccess() {

                }
            });
        } else {
            call.makeCall(new StatusListener() {
                @Override
                public void onSuccess() {

                }
            });
        }
    }
}