package com.example.clinic_appointment.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.databinding.ItemContainerReceivedMessageBinding;
import com.example.clinic_appointment.databinding.ItemContainterSentMessageBinding;
import com.example.clinic_appointment.listeners.MessageListener;
import com.example.clinic_appointment.models.message.Message;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVE = 0;
    private final List<Message> messages;
    private final String senderId;
    private final MessageListener listener;
    private Bitmap receiverProfileImage;
    private static Context context;

    public ChatAdapter(Bitmap receiverProfileImage, List<Message> messages, String senderId, MessageListener listener, Context context) {
        this.listener = listener;
        this.receiverProfileImage = receiverProfileImage;
        this.messages = messages;
        this.senderId = senderId;
        ChatAdapter.context = context;
    }

    public void setReceiverProfileImage(Bitmap bitmap) {
        receiverProfileImage = bitmap;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new SentMessageViewHolder(ItemContainterSentMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false));
        } else {
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(messages.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(messages.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainterSentMessageBinding binding;

        public SentMessageViewHolder(ItemContainterSentMessageBinding itemContainterSentMessageBinding) {
            super(itemContainterSentMessageBinding.getRoot());
            binding = itemContainterSentMessageBinding;
        }

        public void setData(Message message) {
            binding.getRoot().setOnLongClickListener(v -> {
                listener.onHoldListener(message, getAdapterPosition());
                return false;
            });
            binding.itemContainerSentMessageIvImage.setImageBitmap(null);

            if (message.getMessageContent() != null) {
                binding.itemContainerSentMessageTvMessage.setText(message.getMessageContent());
                if (message.getMessageContent().equals("This message has been recalled")) {
                    binding.itemContainerSentMessageTvMessage.setTextColor(context.getColor(R.color.colorError));
                } else {
                    binding.itemContainerSentMessageTvMessage.setTextColor(context.getColor(R.color.colorWhite));
                }
                binding.getRoot().setBackgroundColor(Color.TRANSPARENT);
            } else {
                binding.getRoot().setBackgroundColor(Color.TRANSPARENT);
                binding.itemContainerSentMessageImageLoading.setVisibility(View.VISIBLE);
                binding.itemContainerSentMessageTvMessage.setVisibility(View.GONE);
                StorageReference reference = FirebaseStorage.getInstance().getReference(message.getMessageImage());
                File tempFile;
                try {
                    tempFile = File.createTempFile("tempFile", ".jpg");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                reference.getFile(tempFile).addOnCompleteListener(task -> {
                    binding.itemContainerSentMessageImageLoading.setVisibility(View.GONE);
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                        binding.itemContainerSentMessageIvImage.setImageBitmap(bitmap);
                        binding.itemContainerSentMessageIvImage.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        public ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        public void setData(Message message) {
            binding.itemContainerReceivedMessageIvImage.setImageBitmap(null);

            if (message.getMessageContent() != null) {
                binding.itemContainerReceivedMessageTvMessage.setText(message.getMessageContent());
                if (message.getMessageContent().equals("This message has been recalled")) {
                    binding.itemContainerReceivedMessageTvMessage.setTextColor(context.getColor(R.color.colorError));
                } else {
                    binding.itemContainerReceivedMessageTvMessage.setTextColor(context.getColor(R.color.colorBlack));
                }
                binding.getRoot().setBackgroundColor(Color.TRANSPARENT);
            } else {
                binding.getRoot().setBackgroundColor(Color.TRANSPARENT);
                binding.itemContainerReceivedMessageImageLoading.setVisibility(View.VISIBLE);
                binding.itemContainerReceivedMessageTvMessage.setVisibility(View.GONE);
                StorageReference reference = FirebaseStorage.getInstance().getReference(message.getMessageImage());
                File tempFile;
                try {
                    tempFile = File.createTempFile("tempFile", ".jpg");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                reference.getFile(tempFile).addOnCompleteListener(task -> {
                    binding.itemContainerReceivedMessageImageLoading.setVisibility(View.GONE);
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                        binding.itemContainerReceivedMessageIvImage.setImageBitmap(bitmap);
                        binding.itemContainerReceivedMessageIvImage.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}
