package com.xfl.kakaotalkbot;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.xfl.kakaotalkbot.MessageUI.MessageListAdapter;
import com.xfl.kakaotalkbot.MessageUI.UserMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by XFL on 2/19/2018.
 */

public class DebugModeScreen extends AppCompatActivity {

    static Map<String, List<UserMessage>> savedMessageList = new HashMap<>();
    static List<UserMessage> messageList = new ArrayList<>();

    String scriptName;
    RecyclerView mMessageRecycler;
    MessageListAdapter mMessageAdapter;


    public static void appendReply(final String value) {

        messageList.add(new UserMessage("BOT", value));


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_debugmode);
        final Button sendBtn = findViewById(R.id.button_chatbox_send);
        final EditText room = findViewById(R.id.txt_room);
        final EditText sender = findViewById(R.id.txt_sender);
        final EditText msgTxt = findViewById(R.id.edittext_chatbox);
        final CheckBox chk_groupchat = findViewById(R.id.chk_groupchat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        scriptName = getIntent().getExtras().getString("scriptName");
        if (savedMessageList.get(scriptName) == null)
            savedMessageList.put(scriptName, new ArrayList<UserMessage>());
        messageList = savedMessageList.get(scriptName);

        mMessageRecycler = findViewById(R.id.recyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(DebugModeScreen.this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(msgTxt.getText().toString())) return;
                if (room.getText().toString().isEmpty()) {
                    room.setText("DEBUGROOM");
                }
                if (sender.getText().toString().isEmpty()) {
                    sender.setText("DEBUGSENDER");
                }

                messageList.add(new UserMessage("USER", msgTxt.getText().toString()));
                int newMsgPosition = messageList.size() - 1;
                mMessageAdapter.notifyItemInserted(newMsgPosition);
                mMessageRecycler.scrollToPosition(newMsgPosition);
                NotificationListener.debugRoom = room.getText().toString();
                Thread thr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationListener.callResponder(scriptName, room.getText().toString(), msgTxt.getText().toString(), sender.getText().toString(), chk_groupchat.isChecked(), new ImageDB(BitmapFactory.decodeResource(MainApplication.getContext().getResources(), R.drawable.ic_lock_bugreport)), "DEBUGMODE", null, true);

                    }
                });
                thr.start();
                try {
                    thr.join();
                } catch (Exception e) {
                }
                mMessageAdapter.notifyItemInserted(messageList.size() - 1);
                mMessageRecycler.scrollToPosition(messageList.size() - 1);

                msgTxt.setText("");
            }
        });


    }

    private void refreshView() {
        mMessageAdapter.notifyItemInserted(messageList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savedMessageList.put(scriptName, messageList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clear_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clearlogs) {
            mMessageAdapter.clear();
        }
        return super.onOptionsItemSelected(item);
    }
}
