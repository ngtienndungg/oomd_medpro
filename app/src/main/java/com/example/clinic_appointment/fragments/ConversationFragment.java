package com.example.clinic_appointment.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.clinic_appointment.activities.ChatActivity;
import com.example.clinic_appointment.adapters.RecentConversationAdapter;
import com.example.clinic_appointment.databinding.FragmentRecentConversationBinding;
import com.example.clinic_appointment.listeners.ChatListener;
import com.example.clinic_appointment.models.message.Message;
import com.example.clinic_appointment.models.message.User;
import com.example.clinic_appointment.utilities.Constants;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConversationFragment extends Fragment implements ChatListener {

    private FragmentRecentConversationBinding binding;
    private FirebaseFirestore database;
    private List<Message> conversations;
    private RecentConversationAdapter recentConversationAdapter;

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    Message message = new Message();
                    message.setSenderId(senderId);
                    message.setReceiverId(receiverId);
                    if ("cHSLta2KI8Q6DukIgUsK1Z3E1Jy2".equals(senderId)) {
                        message.setConversationId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                        message.setConversationName(documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME));
                        message.setConversationImage(documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE));
                    } else {
                        message.setConversationId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                        message.setConversationName(documentChange.getDocument().getString(Constants.KEY_SENDER_NAME));
                        message.setConversationImage(documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE));
                    }
                    message.setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    message.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    conversations.add(message);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversations.get(i).getSenderId().equals(senderId) && conversations.get(i).getReceiverId().equals(receiverId)) {
                            conversations.get(i).setMessageContent(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                            conversations.get(i).setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                            break;
                        }
                    }
                }
            }
            conversations.sort((o1, o2) -> o2.getDateObject().compareTo(o1.getDateObject()));
            recentConversationAdapter.notifyDataSetChanged();
            binding.fragmentRecentConversationRvRecentMessage.smoothScrollToPosition(0);
            binding.fragmentRecentConversationPbLoading.setVisibility(View.GONE);
            binding.fragmentRecentConversationRvRecentMessage.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecentConversationBinding.inflate(getLayoutInflater());
        initiate();
        listenConversation();
        return binding.getRoot();
    }

    private void initiate() {
        database = FirebaseFirestore.getInstance();
        conversations = new ArrayList<>();
        recentConversationAdapter = new RecentConversationAdapter(conversations, this);
        binding.fragmentRecentConversationRvRecentMessage.setAdapter(recentConversationAdapter);
    }

    private void listenConversation() {
        binding.fragmentRecentConversationPbLoading.setVisibility(View.VISIBLE);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, "cHSLta2KI8Q6DukIgUsK1Z3E1Jy2")
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, "cHSLta2KI8Q6DukIgUsK1Z3E1Jy2")
                .addSnapshotListener(eventListener);
    }

    @Override
    public void onRecentConversationClicked(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}