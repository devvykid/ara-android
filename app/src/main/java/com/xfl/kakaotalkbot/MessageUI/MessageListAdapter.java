package com.xfl.kakaotalkbot.MessageUI;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xfl.kakaotalkbot.MainApplication;
import com.xfl.kakaotalkbot.R;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<UserMessage> mMessageList;

    public MessageListAdapter(Context context, List<UserMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    public void clear() {
        int size = mMessageList.size();
        mMessageList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addItem(UserMessage item, int position) {
        mMessageList.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        UserMessage message = mMessageList.get(position);

        if (message.getIsBot()) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            // If some other user sent the message

            return VIEW_TYPE_MESSAGE_SENT;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserMessage message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Context ctx = MainApplication.getContextForJava();
                Toast.makeText(ctx, ctx.getResources().getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                String str = ((TextView) v.findViewById(R.id.message_body)).getText().toString();
                ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(str, str);
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView senderText;

        SentMessageHolder(View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.message_sender);
            messageText = itemView.findViewById(R.id.message_body);
        }

        void bind(UserMessage message) {
            messageText.setText(message.getText());
            senderText.setText(message.getName());
            // Format the stored timestamp into a readable String using method.

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;


        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.message_body);


        }

        void bind(UserMessage message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.


            // Insert the profile image from the URL into the ImageView.

        }
    }
}