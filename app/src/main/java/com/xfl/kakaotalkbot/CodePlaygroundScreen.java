package com.xfl.kakaotalkbot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.faendir.rhino_android.RhinoAndroidHelper;
import com.xfl.kakaotalkbot.MessageUI.MessageListAdapter;
import com.xfl.kakaotalkbot.MessageUI.UserMessage;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

import java.util.ArrayList;
import java.util.List;


public class CodePlaygroundScreen extends AppCompatActivity {
    static final List<UserMessage> messageList = new ArrayList<>();
    static Context parseContext;
    static ScriptableObject execScope;
    static Function responder;
    RecyclerView mMessageRecycler;
    MessageListAdapter mMessageAdapter;

    public static void initializeScript() {
        System.gc();

        ScriptableObject scope;

        try {


            parseContext = RhinoAndroidHelper.prepareContext();

            parseContext.setOptimizationLevel(-1);

            parseContext.setLanguageVersion(org.mozilla.javascript.Context.VERSION_ES6);
            scope = (ScriptableObject) parseContext.initStandardObjects(new ImporterTopLevel(parseContext));
            parseContext.setWrapFactory(new PrimitiveWrapFactory());
            Script script_real = parseContext.compileString("function response(str){try{return eval(str)+'';}catch(e){return e.name+'\\n'+e.message;}}", "CodePlayground", 0, null);
            Api.scriptName = "CodePlayground";
            ScriptableObject.defineClass(scope, Api.class);
            ScriptableObject.defineClass(scope, DataBase.class);
            ScriptableObject.defineClass(scope, Utils.class);
            ScriptableObject.defineClass(scope, com.xfl.kakaotalkbot.Log.class);
            ScriptableObject.defineClass(scope, AppData.class);
            ScriptableObject.defineClass(scope, Bridge.class);
            execScope = scope;

            script_real.exec(parseContext, scope);


            responder = (Function) scope.get("response", scope);
            Toast.makeText(MainApplication.getContext(), "Ready", Toast.LENGTH_SHORT).show();
            Context.exit();
        } catch (final Exception e) {
            e.printStackTrace();

            return;

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_codeplayground);
        parseContext = RhinoAndroidHelper.prepareContext();
        final EditText msgTxt = findViewById(R.id.sand_edittext_chatbox);
        final Button sendBtn = findViewById(R.id.sand_button_chatbox_send);
        mMessageRecycler = findViewById(R.id.sand_recyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(CodePlaygroundScreen.this));
        mMessageRecycler.setAdapter(mMessageAdapter);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(msgTxt.getText().toString())) return;


                messageList.add(new UserMessage(false, msgTxt.getText().toString(), ""));
                int newMsgPosition = messageList.size() - 1;
                mMessageAdapter.notifyItemInserted(newMsgPosition);
                mMessageRecycler.scrollToPosition(newMsgPosition);

                Thread thr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Context.enter();
                        appendReply(runScript(msgTxt.getText().toString()));
                        Context.exit();

                    }
                });
                thr.start();


                msgTxt.setText("");
            }
        });
        /*
        final ChatView chatView = findViewById(R.id.codeplaygroundchatview);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(final ChatMessage chatMessage) {
                if (!chatMessage.getMessage().isEmpty()) {
                    Thread thr = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Context.enter();
                            msg = runScript(chatMessage.getMessage());
                            Context.exit();
                            NotificationListener.UIHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    chatView.addMessage(new ChatMessage(msg, new Date().getTime(), ChatMessage.Type.RECEIVED));

                                }
                            });
                        }
                    });
                    thr.start();


                }

                return true;
            }
        });*/
    }

    private void appendReply(final String str) {

        NotificationListener.UIHandler.post(new Runnable() {
            @Override
            public void run() {
                messageList.add(new UserMessage(true, str, ""));
                mMessageAdapter.notifyItemInserted(messageList.size() - 1);
                mMessageRecycler.scrollToPosition(messageList.size() - 1);
            }
        });

    }

    private String runScript(String str) {


        parseContext.setWrapFactory(new PrimitiveWrapFactory());
        parseContext.setLanguageVersion(Context.VERSION_ES6);
        parseContext.setOptimizationLevel(-1);

        return responder.call(parseContext, execScope, execScope, new Object[]{str}).toString();
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
